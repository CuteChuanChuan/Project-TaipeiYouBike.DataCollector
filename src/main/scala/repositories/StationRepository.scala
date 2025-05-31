package repositories

import helper.ConfigHelper
import models.{ ChangeSets, DocFields, MongoConfig, ScdMetadata, StationDim, StationDimWithScd, StationFct }
import org.bson.Document
import org.mongodb.scala.model.{ Filters, Updates }
import org.mongodb.scala.{ MongoClient, MongoCollection, MongoDatabase, _ }
import org.slf4j.Logger
import services.ScdProcessor
import services.converter.{ DocumentConverter, ModelConverter }

import java.time.format.DateTimeFormatter
import java.time.{ ZoneId, ZonedDateTime }
import scala.concurrent.{ ExecutionContext, Future }

class StationRepository(private val scdProcessor: ScdProcessor)(implicit ec: ExecutionContext) {

  private val logger: Logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  private val mongoConfig:   MongoConfig               = ConfigHelper.fetchMongoConfig
  private val mongoClient:   MongoClient               = MongoClient(mongoConfig.uri)
  private val database:      MongoDatabase             = mongoClient.getDatabase(mongoConfig.database)
  private val dimCollection: MongoCollection[Document] = database.getCollection(mongoConfig.dimCollection)
  private val fctCollection: MongoCollection[Document] = database.getCollection(mongoConfig.fctCollection)

  def insertStationFacts(facts: Seq[StationFct]): Future[Unit] = {
    val docs: Seq[Document] = facts.map(DocumentConverter.fctToDocument)
    insertDocuments(collection = fctCollection, docs = docs, docType = "Fact")
  }

  def insertStationDims(newDims: Seq[StationDim], timestamp: ZonedDateTime): Future[Unit] = {
    val stationIds = newDims.map(_.stationId)

    for {
      currentDimsWithScd <- fetchCurrentStationDimsWithScd(stationIds)
      changeSets = categorizeChanges(newDims, currentDimsWithScd)
      _ <- applyChanges(changeSets, timestamp)
    } yield ()
  }

  private def fetchCurrentStationDimsWithScd(stationIds: Seq[Int]): Future[Map[Int, StationDimWithScd]] = {
    val filters = Filters.and(
      Filters.in(DocFields.Dim.STATION_ID, stationIds: _*),
      Filters.eq(DocFields.Metadata.IS_CURRENT, true)
    )

    dimCollection
      .find(filters)
      .toFuture()
      .map(docs =>
        docs.map { doc =>
          val dim      = ModelConverter.documentToDim(doc)
          val metadata = ModelConverter.documentToSCDMetadata(doc)
          dim.stationId -> StationDimWithScd(dim, metadata)
        }.toMap)
  }

  private def categorizeChanges(
    newDims:            Seq[StationDim],
    currentDimsWithScd: Map[Int, StationDimWithScd]
  ): ChangeSets = {

    val (existing, news) = newDims.partition(dim => currentDimsWithScd.contains(dim.stationId))
    val changed          = existing.filter(dim => hasChanged(dim, currentDimsWithScd))

    ChangeSets(
      toInsert = news ++ changed,
      toClose = changed.map(_.stationId)
    )
  }

  private def applyChanges(changeSets: ChangeSets, timestamp: ZonedDateTime): Future[Unit] =
    for {
      _ <- closeIfNeeded(changeSets.toClose, timestamp)
      _ <- insertIfNeeded(changeSets.toInsert, scdProcessor.createNewSCDMetadata(timestamp))
    } yield ()

  private def hasChanged(newDim: StationDim, currentDimsWithScd: Map[Int, StationDimWithScd]): Boolean =
    currentDimsWithScd.get(newDim.stationId) match {
      case Some(current) => scdProcessor.hasAttributesChanged(current.dim, newDim)
      case None          => false
    }

  private def closeIfNeeded(stationIds: Seq[Int], timestamp: ZonedDateTime): Future[Unit] =
    if (stationIds.nonEmpty) closeStationDims(stationIds, timestamp)
    else Future.successful(())

  private def insertIfNeeded(dims: Seq[StationDim], metadata: ScdMetadata): Future[Unit] =
    if (dims.nonEmpty) insertNewStationDims(dims, metadata)
    else Future.successful(())

  private def insertNewStationDims(dims: Seq[StationDim], metadata: ScdMetadata): Future[Unit] = {
    val docs: Seq[Document] = dims.map(dim => DocumentConverter.dimToDocument(dim = dim, metadata = metadata))
    insertDocuments(collection = dimCollection, docs = docs, docType = "Dimension")
  }

  private def closeStationDims(stationIds: Seq[Int], closingTime: ZonedDateTime): Future[Unit] = {
    if (stationIds.isEmpty) return Future.successful(())

    val filters = Filters.and(
      Filters.in(DocFields.Dim.STATION_ID, stationIds: _*),
      Filters.eq(DocFields.Metadata.IS_CURRENT, true)
    )

    val closedMetadataValues = scdProcessor.getClosedMetadataValues(closingTime)
    val effectiveToUTC       = closedMetadataValues.effectiveTo.withZoneSameInstant(ZoneId.of("UTC"))
    val effectiveToStr       = effectiveToUTC.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))

    val updates = Updates.combine(
      Updates.set(DocFields.Metadata.IS_CURRENT, closedMetadataValues.isCurrent),
      Updates.set(DocFields.Metadata.EFFECTIVE_TO, effectiveToStr)
    )

    dimCollection
      .updateMany(filters, updates)
      .toFuture()
      .map(_ => ())
  }

  private def insertDocuments(
    collection: MongoCollection[Document],
    docs:       Seq[Document],
    docType:    String): Future[Unit] =
    if (docs.isEmpty) Future.successful(())
    else {
      collection
        .insertMany(docs)
        .toFuture()
        .map { result =>
          logger.info(s"Successfully inserted ${result.getInsertedIds.size()} $docType documents")
          ()
        }
        .recover { case e: Exception =>
          logger.error(s"Error inserting $docType documents: ${e.getMessage}")
          throw e
        }
    }
}
