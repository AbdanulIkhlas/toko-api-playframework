package models

import play.api.libs.json._
import anorm._
import anorm.SqlParser._

case class Kategori(id: Long, nama: String, is_delete: Boolean = false)
case class KategoriInput(nama: String)

object Kategori {
  implicit val format: OFormat[Kategori] = Json.format[Kategori]
  val parser: RowParser[Kategori] = {
    get[Long]("kategori.id") ~
      get[String]("kategori.nama") ~
      get[Boolean]("kategori.is_delete") map {
      case id ~ nama ~ is_delete => Kategori(id, nama, is_delete)
    }
  }
}

object KategoriInput {
  implicit val format: OFormat[KategoriInput] = Json.format[KategoriInput]
}