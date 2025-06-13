package controllers

import javax.inject._
import play.api.mvc._
import play.api.db.Database
import anorm._

@Singleton
class HomeController @Inject()(db: Database, val controllerComponents: ControllerComponents) extends BaseController {
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def cekKoneksiDb() = Action {
    try {
      db.withConnection { implicit c =>
        // menjalankan query sederhana untuk test
        SQL("SELECT 1").execute()
      }
      Ok("Koneksi ke database PostgreSQL berhasil")
    } catch {
      case e: Exception => InternalServerError(s"KONEKSI GAGAL: ${e.getMessage}")
    }
  }
}