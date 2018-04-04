package shapeless.datatype.bigquery

import shapeless.datatype.test.SerializableUtils

object Test {

  object Color extends Enumeration {
    type Color = Value
    val Red, Green, Blue = Value
  }
  case class Record(i: Int, s: String, c: Color.Value)

  def main(args: Array[String]): Unit = {
    val r = Record(10, "hello", Color.Red)

    implicit val colorType = BigQueryType.at[Color.Value]("STRING")(_ => Color.Red, _.toString)
    val bqt = BigQueryType[Record]

    SerializableUtils.ensureSerializable(bqt)
    val fn = (r: Record) => bqt.toTableRow(r)
    SerializableUtils.ensureSerializable(fn.asInstanceOf[Serializable])
    println(bqt.toTableRow(r))
  }
}