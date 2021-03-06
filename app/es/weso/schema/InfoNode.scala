package es.weso.schema
import es.weso.utils.ShowHTML
import es.weso.utils.ShowHTML._
import ShapeLabel._
import Explanation._
import es.weso.rdf.PrefixMap

case class InfoNode(
    hasShapes: Seq[(ShapeLabel,Explanation)], 
    hasNoShapes: Seq[(ShapeLabel,Explanation)]) {
  
/*  implicit val infoNodeShowHTML = new ShowHTML[InfoNode] {
    def toHTML(e: InfoNode) = toHTML
  } */

  def toHTML(pm: PrefixMap): String = {
    val sb = new StringBuilder
    sb ++= "<ul class=\"positiveShapes\">"
    for ((s,e) <- hasShapes) {
      sb ++= ("<li><span class=\"shape\"" + s.toHTML(pm) + "</span>" +
              "<span class=\"explanation\">" + e.toHTML(pm) + "</span></li>")
    }
    sb.append("</ul>")
    sb.append("<ul class=\"negativeShapes\">")
    for ((s,e) <- hasNoShapes) {
      sb ++= ("<li><span class=\"shape\"" + s.toHTML(pm) + "</span>" +
              "<span class=\"explanation\">" + e.toHTML(pm) + "</span></li>")
    }
    sb.append("</ul>")
    sb.toString
  }
  
}
