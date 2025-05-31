package services

import models.{ ClosedMetadataValues, ScdMetadata, StationDim }

import java.time.ZonedDateTime

class ScdProcessor {

  def createNewSCDMetadata(timestamp: ZonedDateTime): ScdMetadata =
    ScdMetadata(effectiveFrom = timestamp, effectiveTo = None, isCurrent = true)

  def getClosedMetadataValues(closingTime: ZonedDateTime): ClosedMetadataValues =
    ClosedMetadataValues(
      effectiveTo = closingTime,
      isCurrent = false
    )

  def hasAttributesChanged(currentDim: StationDim, newDim: StationDim): Boolean =
    currentDim.name != newDim.name ||
      currentDim.nameZh != newDim.nameZh ||
      currentDim.address != newDim.address ||
      currentDim.addressZh != newDim.addressZh ||
      currentDim.latitude != newDim.latitude ||
      currentDim.longitude != newDim.longitude ||
      currentDim.totalDocks != newDim.totalDocks ||
      currentDim.districtZh != newDim.districtZh ||
      currentDim.isActive != newDim.isActive
}
