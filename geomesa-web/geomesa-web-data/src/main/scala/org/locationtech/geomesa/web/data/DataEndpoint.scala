package org.locationtech.geomesa.web.data

import com.typesafe.scalalogging.slf4j.Logging
import org.locationtech.geomesa.tools.DataStoreHelper
import org.locationtech.geomesa.tools.commands.GeoMesaParams
import org.locationtech.geomesa.tools.commands.RemoveSchemaCommand.RemoveSchemaParams
import org.locationtech.geomesa.web.core.GeoMesaScalatraServlet
import org.locationtech.geomesa.web.scalatra.PkiAuthenticationSupport
import org.scalatra.Ok
import org.scalatra.servlet.FileUploadSupport

import scala.util.Try

class DataEndpoint extends GeoMesaScalatraServlet with Logging {

  override val root: String = "data"

  private[this] def setGMParams(gmp: GeoMesaParams) = {
    gmp.user         = params("user")
    gmp.password     = params("password")
    gmp.instance     = params("instance")
    gmp.zookeepers   = params("zookeepers")
    gmp.auths        = params.get("auths").orNull
    gmp.visibilities = params.get("visibilities").orNull
    gmp.catalog      = params("catalog")
    gmp.useMock      = params.get("useMock").exists(_.toBoolean)
  }
  private[this] def getDS(gmp: GeoMesaParams) =
    new DataStoreHelper(gmp).getExistingStore()

  delete("/:catalog/:feature") {
    val fn = params("feature")
    Try {
      val rsp = new RemoveSchemaParams
      setGMParams(rsp)
      rsp.featureName = fn
      deleteFeature(rsp)
    }.recover {
      case e: Throwable => logger.warn(s"Error deleting feature $fn", e)
    }
    Ok()
  }

  post("/:catalog/:feature/delete") {
    val fn = params("feature")
    Try {
      val rsp = new RemoveSchemaParams
      setGMParams(rsp)
      rsp.featureName = fn
      deleteFeature(rsp)
    }.recover {
      case e: Throwable => logger.warn(s"Error deleting feature $fn", e)
    }
    Ok()
  }

  def deleteFeature(rsp: RemoveSchemaParams) {
    getDS(rsp).removeSchema(rsp.featureName)
  }
}
