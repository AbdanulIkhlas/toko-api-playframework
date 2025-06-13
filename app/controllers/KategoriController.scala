package controllers

import javax.inject._
import play.api.mvc._
import play.api.db.Database
import play.api.libs.json._
import anorm._
import models.{Kategori, KategoriInput}

@Singleton
class KategoriController @Inject()(db: Database, val controllerComponents: ControllerComponents) extends BaseController {

  private val parser = Kategori.parser
  private def baseSelect(whereClause: String = "WHERE is_delete = FALSE") =
    s"""SELECT id as "kategori.id", nama as "kategori.nama", is_delete as "kategori.is_delete" FROM kategori $whereClause"""

  def create(): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[KategoriInput].fold(
      _ => BadRequest(Json.obj("pesan" -> "JSON salah format")),
      kategoriInput => {
        db.withConnection { implicit conn =>
          val id: Option[Long] = SQL"INSERT INTO kategori(nama) VALUES (${kategoriInput.nama})".executeInsert()
          Ok(Json.obj("pesan" -> "Kategori dibuat", "id" -> id))
        }
      }
    )
  }

  def list(): Action[AnyContent] = Action {
    db.withConnection { implicit conn =>
      Ok(Json.toJson(SQL(baseSelect()).as(parser.*)))
    }
  }

  def detail(id: Long): Action[AnyContent] = Action {
    db.withConnection { implicit conn =>
      SQL(baseSelect("WHERE id = {id} AND is_delete = FALSE")).on("id" -> id).as(parser.singleOpt) match {
        case Some(kategori) => Ok(Json.toJson(kategori))
        case None => NotFound(Json.obj("pesan" -> "Kategori tidak ditemukan"))
      }
    }
  }

  def update(id: Long): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[KategoriInput].fold(
      _ => BadRequest(Json.obj("pesan" -> "JSON salah format")),
      kategoriInput => {
        db.withConnection { implicit conn =>
          val affectedRows = SQL"UPDATE kategori SET nama = ${kategoriInput.nama} WHERE id = $id AND is_delete = FALSE".executeUpdate()
          if (affectedRows > 0) Ok(Json.obj("pesan" -> "Kategori diupdate"))
          else NotFound(Json.obj("pesan" -> "Kategori tidak ditemukan"))
        }
      }
    )
  }

  def delete(id: Long): Action[AnyContent] = Action { request =>
    db.withConnection { implicit conn =>
      val affectedRows = SQL"UPDATE kategori SET is_delete = TRUE WHERE id = $id".executeUpdate()
      if (affectedRows > 0) Ok(Json.obj("pesan" -> "Kategori dihapus"))
      else NotFound(Json.obj("pesan" -> "Kategori tidak ditemukan"))
    }
  }
}