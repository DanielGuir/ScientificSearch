package ai.lum.odinson

import java.io.File
import java.util.Date
import java.time.{ LocalDate, ZoneId }
import upickle.default._
import ai.lum.common.FileUtils._



case class Document(
  id: String,
  metadata: Seq[Field],
  sentences: Seq[Sentence],
) {
  def toJson: String = write(this)
  def toPrettyJson: String = write(this, indent = 4)
}

object Document {

  implicit val rw: ReadWriter[Document] = macroRW

  def fromJson(data: String): Document = {
    read[Document](data)
  }

  def fromJson(f: File): Document = {
    fromJson(f.readString())
  }

}



case class Sentence(
  numTokens: Long,
  fields: Seq[Field]
) {
  def toJson: String = write(this)
  def toPrettyJson: String = write(this, indent = 4)
}

object Sentence {
  implicit val rw: ReadWriter[Sentence] = macroRW
  def fromJson(data: String): Sentence = {
    read[Sentence](data)
  }
}



sealed trait Field {
  def name: String
  def store: Boolean
  def toJson: String = write(this)
  def toPrettyJson: String = write(this, indent = 4)
}

object Field {
  implicit val rw: ReadWriter[Field] = {
    ReadWriter.merge(
      TokensField.rw,
      GraphField.rw,
      StringField.rw,
      DateField.rw,
    )
  }
}

case class TokensField(
  name: String,
  tokens: Seq[String],
  store: Boolean = false,
) extends Field

object TokensField {
  implicit val rw: ReadWriter[TokensField] = macroRW
  def fromJson(data: String): TokensField = {
    read[TokensField](data)
  }
}

case class GraphField(
  name: String,
  incomingEdges: Seq[Seq[(Int, String)]],
  outgoingEdges: Seq[Seq[(Int, String)]],
  roots: Set[Int],
  store: Boolean = false,
) extends Field

object GraphField {
  implicit val rw: ReadWriter[GraphField] = macroRW
  def fromJson(data: String): GraphField = {
    read[GraphField](data)
  }
}

case class StringField(
  name: String,
  string: String,
  store: Boolean = false,
) extends Field

object StringField {
  implicit val rw: ReadWriter[StringField] = macroRW
  def fromJson(data: String): StringField = {
    read[StringField](data)
  }
}

case class DateField(
  name: String,
  date: String,
  store: Boolean = false,
) extends Field {
  val localDate = LocalDate.parse(date)
}

object DateField {

  implicit val rw: ReadWriter[DateField] = macroRW

  def fromJson(data: String): DateField = {
    read[DateField](data)
  }

  def fromDate(name: String, date: Date, store: Boolean = false): DateField = {
    val localDate = date.toInstant.atZone(ZoneId.systemDefault).toLocalDate
    fromLocalDate(name, localDate, store)
  }

  def fromLocalDate(name: String, date: LocalDate, store: Boolean = false): DateField = {
    DateField(name, date.toString, store)
  }

}
