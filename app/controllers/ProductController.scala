package controllers

import javax.inject._
import play.api.mvc._
import play.api.db.Database
import play.api.libs.json._
import anorm._
import models.{ProdukDB, ProdukInput}

@Singleton
class ProductController @Inject()(db: Database, val controllerComponents: ControllerComponents) extends BaseController {

  private val parser = ProdukDB.parserWithKategori
  private val baseSql =
    """
      SELECT
        p.id as "produk.id", p.nama as "produk.nama", p.harga as "produk.harga", p.stok as "produk.stok",
        k.id as "kategori.id", k.nama as "kategori.nama", k.is_delete as "kategori.is_delete"
      FROM produk p
      LEFT JOIN kategori k ON p.kategori_id = k.id
      WHERE p.is_delete = FALSE AND k.is_delete = FALSE
    """

  def list(): Action[AnyContent] = Action {
    db.withConnection { implicit conn =>
      val resultSql = SQL(baseSql).as(parser.*)
      Ok(Json.toJson(resultSql))
    }
  }

  def detail(id: Long): Action[AnyContent] = Action {
    request => val body = request.body
    db.withConnection { implicit conn =>
      val sqlWithId = s"$baseSql AND p.id = {id}"
      SQL(sqlWithId).on("id" -> id).as(parser.singleOpt) match {
        case Some(product) => Ok(Json.toJson(product))
        case None => NotFound(Json.obj("pesan" -> "Produk tidak ditemukan"))
      }
    }
  }

  def create(): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[ProdukInput].fold(
      _ => BadRequest(Json.obj("pesan" -> "JSON salah format")),
      produkInput => {
        db.withConnection { implicit conn =>
          val id: Option[Long] = SQL"""
            INSERT INTO produk(nama, harga, stok, kategori_id)
            VALUES (${produkInput.nama}, ${produkInput.harga}, ${produkInput.stok}, ${produkInput.kategori_id})
          """.executeInsert()
          Ok(Json.obj("pesan" -> "Produk dibuat", "id" -> id))
        }
      }
    )
  }

  def update(id: Long): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[ProdukInput].fold(
      _ => BadRequest(Json.obj("pesan" -> "JSON salah format")),
      produkInput => {
        db.withConnection { implicit conn =>
          val affectedRows = SQL"""
            UPDATE produk SET
            nama = ${produkInput.nama},
            harga = ${produkInput.harga},
            stok = ${produkInput.stok},
            kategori_id = ${produkInput.kategori_id}
            WHERE id = $id AND is_delete = FALSE
          """.executeUpdate()
          if (affectedRows > 0) Ok(Json.obj("pesan" -> "Produk diupdate"))
          else NotFound(Json.obj("pesan" -> "Produk tidak ditemukan"))
        }
      }
    )
  }

  def delete(id: Long): Action[AnyContent] = Action { request =>
    db.withConnection { implicit conn =>
      val affectedRows = SQL"UPDATE produk SET is_delete = TRUE WHERE id = $id".executeUpdate()
      if (affectedRows > 0) Ok(Json.obj("pesan" -> "Produk dihapus"))
      else NotFound(Json.obj("pesan" -> "Produk tidak ditemukan"))
    }
  }
}