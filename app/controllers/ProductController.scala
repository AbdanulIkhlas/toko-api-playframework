package controllers

import javax.inject._
import play.api.mvc._
import play.api.db.Database
import play.api.libs.json._
import anorm._

import models.{Produk, ProdukInput}

@Singleton
class ProductController @Inject()(db: Database, val controllerComponents: ControllerComponents) extends BaseController {

  private val parser = Produk.parser

// menampilkan data produk
  def list(): Action[AnyContent] = Action { implicit request =>
    db.withConnection { implicit conn =>
      val products: List[Produk] = SQL"SELECT * FROM produk WHERE is_delete = FALSE".as(parser.*)
      Ok(Json.toJson(products))
    }
  }

// menampilkan produk by id
  def detail(id: Long): Action[AnyContent] = Action { implicit request =>
    db.withConnection { implicit conn =>
      val productOpt: Option[Produk] = SQL"SELECT * FROM produk WHERE id = $id AND is_delete = FALSE".as(parser.singleOpt)
      productOpt match {
        case Some(product) => Ok(Json.toJson(product))
        case None => NotFound(Json.obj("message" -> "Produk tidak ditemukan"))
      }
    }
  }

//  menambahkan produk
  def create(): Action[JsValue] = Action(parse.json) { implicit request =>
    val productResult = request.body.validate[ProdukInput]
    productResult.fold(
      errors => BadRequest(Json.obj("message" -> "Format JSON salah", "details" -> JsError.toJson(errors))),
      product => {
        db.withConnection { implicit conn =>
          val id: Option[Long] = SQL"""
            INSERT INTO produk(nama, kategori, harga, stok)
            VALUES (${product.nama}, ${product.kategori}, ${product.harga}, ${product.stok})
          """.executeInsert()
          Ok(Json.obj("message" -> "Produk berhasil ditambahkan", "id" -> id))
        }
      }
    )
  }

//  update produk by id
  def update(id: Long): Action[JsValue] = Action(parse.json) { implicit request =>
    val productResult = request.body.validate[ProdukInput]
    productResult.fold(
      errors => BadRequest(Json.obj("message" -> "Format JSON salah", "details" -> JsError.toJson(errors))),
      product => {
        db.withConnection { implicit conn =>
          val affectedRows = SQL"""
            UPDATE produk SET
            nama = ${product.nama},
            kategori = ${product.kategori},
            harga = ${product.harga},
            stok = ${product.stok}
            WHERE id = $id AND is_delete = FALSE
          """.executeUpdate()
          if (affectedRows > 0) Ok(Json.obj("message" -> "Produk berhasil diupdate"))
          else NotFound(Json.obj("message" -> "Produk tidak ditemukan"))
        }
      }
    )
  }

//  soft delete produk
  def delete(id: Long): Action[AnyContent] = Action { implicit request =>
    db.withConnection { implicit conn =>
      val affectedRows = SQL"UPDATE produk SET is_delete = TRUE WHERE id = $id".executeUpdate()
      if (affectedRows > 0) Ok(Json.obj("message" -> "Produk berhasil dihapus (soft delete)"))
      else NotFound(Json.obj("message" -> "Produk tidak ditemukan"))
    }
  }
}
