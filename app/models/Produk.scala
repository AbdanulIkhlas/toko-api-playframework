package models

import play.api.libs.json._
import anorm._
import anorm.SqlParser._

// Case class untuk merepresentasikan data di database (untuk output/response)
case class Produk(id: Long, nama: String, kategori: String, harga: Double, stok: Int, is_delete: Boolean = false)

// Case class untuk menerima data dari user (untuk input/request)
case class ProdukInput(nama: String, kategori: String, harga: Double, stok: Int)

object Produk {
  // Implicit untuk mengubah object Produk menjadi JSON (untuk response)
  implicit val writer: OWrites[Produk] = Json.writes[Produk]
//  implicit val reader: Reads[ProdukInput] = Json.reads[ProdukInput]

  // Parser Anorm: Mengubah baris data dari database (Row) menjadi object Produk
  val parser: RowParser[Produk] = {
    get[Long]("id") ~
      get[String]("nama") ~
      get[String]("kategori") ~
      get[Double]("harga") ~
      get[Int]("stok") ~
      get[Boolean]("is_delete") map {
      case id ~ nama ~ kategori ~ harga ~ stok ~ is_delete =>
        Produk(id, nama, kategori, harga, stok, is_delete)
    }
  }
}

object ProdukInput {
  // Implicit untuk membaca JSON dari user menjadi object ProdukInput (untuk request)
  implicit val reader: Reads[ProdukInput] = Json.reads[ProdukInput]
}
