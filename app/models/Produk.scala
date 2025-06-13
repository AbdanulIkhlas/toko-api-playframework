package models

import play.api.libs.json._
import anorm._
import anorm.SqlParser._

case class ProdukInput(nama: String, kategori_id: Long, harga: Double, stok: Int)
object ProdukInput {
  implicit val format: OFormat[ProdukInput] = Json.format[ProdukInput]
}

case class ProdukResponse(id: Long, nama: String, harga: Double, stok: Int, kategori: Kategori)
object ProdukResponse {
  implicit val kategoriFormat: OFormat[Kategori] = Kategori.format
  implicit val format: OFormat[ProdukResponse] = Json.format[ProdukResponse]
}

object ProdukDB {
  val parserWithKategori: RowParser[ProdukResponse] = {
    get[Long]("produk.id") ~
      get[String]("produk.nama") ~
      get[Double]("produk.harga") ~
      get[Int]("produk.stok") ~
      Kategori.parser map { case id ~ nama ~ harga ~ stok ~ kategoriObject =>
        ProdukResponse(id, nama, harga, stok, kategoriObject)
      }
  }
}
