package models

import java.time.ZonedDateTime

case class ScdMetadata(
  effectiveFrom: ZonedDateTime,
  effectiveTo:   Option[ZonedDateTime],
  isCurrent:     Boolean
)
