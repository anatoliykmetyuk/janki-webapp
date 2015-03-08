package janki.webapp

import org.scalatra._
import scalate.ScalateSupport

import jorders.Constants._
import Fields._
import jtext._
import jorders._
import jorders.output._
import org.apache.commons.io.output._

class JAnkiServlet extends JankiWebappStack {

  before() {
    contentType = "text/html"
  }

  get("/") {
    ssp("/processText")
  }

  post("/generated.zip") {
    contentType = "application/octet-stream"

    val text       = params("input")
    val radicals   = params("radicals") == "on"
    val kanji      = params("kanji") == "on"
    val vocabulary = params("vocabulary") == "on"
    val media      = params("media") == "on"
    
    val order = collection.mutable.Map[String, Any]()
    if (radicals  ) order += RADICALS   -> s"$I $M $E $Di" 
    if (kanji     ) order += KANJI      -> s"$I $M $R $E $Di"
    if (vocabulary) order += VOCABULARY -> s"$I $M $R $E"
    if (media     ) order += MEDIA      -> ""

    val payload = TextTokenizer(text).filter(_.name != null)

    val processed = ProcessOrder.processEntities(payload, order.toMap)

    val byteStream = new ByteArrayOutputStream()
    ZipOutput.writeResultToStream(processed, byteStream)

    val resp: Array[Byte] = byteStream.toByteArray
    resp
  }
  
}
