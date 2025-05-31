package models

case class ChangeSets(
  toInsert: Seq[StationDim],
  toClose:  Seq[Int]
)
