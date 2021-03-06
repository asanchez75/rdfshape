package es.weso.schema
import es.weso.rdf._
import es.weso.rdf.nodes._
import org.scalactic._
import es.weso.shex.{ Schema => ShExSchema, Label => ShExLabel, ShEx => ShEx2, _}
import es.weso.shex.converter.RDF2Schema
import util._
import es.weso.typing._
import es.weso.rdf.validator.ValidationResult

case class ShEx3(schema: ShExSchema) extends Schema {
  override def name = "ShEx"
  
  override def formats = SchemaFormat.formatNames
  
  override def toHTML(format: String): String = {
    "<pre>" + schema.serialize(format) + "</pre>"
  }
  
  override def validate(rdf: RDFReader) : Result = {
    val matcher = ShExMatcher(schema,rdf)
    val r = matcher.validate
    validationResult2Result(r)
  }
  
  override def validateNodeShape(node: IRI, shape: String, rdf: RDFReader) : Result = {
    val matcher = ShExMatcher(schema,rdf)
    val pm = schema.pm
    val maybeLabel = pm.qname(shape).map(lbl => ShExLabel.mkLabel(lbl)).flatten
    maybeLabel match {
      case Some(lbl) => {
        val r = matcher.match_node_label(node)(lbl)
        validationResult2Result(r)
      }
      case None => Result.errStr(s"Cannot make label from shape $shape")
    }
  }
  
  override def validateNodeAllShapes(node: IRI, rdf: RDFReader) : Result = {
    val matcher = ShExMatcher(schema,rdf)
    val r = matcher.match_node_AllLabels(node)
    validationResult2Result(r)
  }
  
  override def validateAllNodesAllShapes(rdf: RDFReader) : Result = {
    val matcher = ShExMatcher(schema,rdf)
    val r = matcher.matchAllNodes_AllLabels
    validationResult2Result(r)
  }
  
  def hasSolutions(rs: Seq[Map[RDFNode,(Seq[ShExLabel],Seq[ShExLabel])]]): Boolean = {
    if (rs.size == 0) false
    else if (rs.size == 1 && rs.head.isEmpty) false
    else true
  }
  
  def validationResult2Result(result: ValidationResult[RDFNode,ShExLabel,Throwable]): Result = {
    val isValid = result.isValid
    val (msg,solutions,errors): (String,Seq[Solution],Seq[ErrorInfo]) = {
      result.extract match {
        case Success(rs) => {
          if (hasSolutions(rs))
            ("Solutions found", rs.map(cnvSol(_)), Seq())
          else
            ("No Results", Seq(), Seq(ErrorInfo("No results")))
        }
        case Failure(e) => (s"Error $e.getMessage", Seq(), Seq(ErrorInfo(e.getMessage)))
      }
    }
    val r = Result(isValid, msg, solutions, errors)
    println(s"validationresult2Result result: $result, r: $r")
    r
  }
  
  def cnvSol(rs: Map[RDFNode, (Seq[ShExLabel], Seq[ShExLabel])]): Solution = {
    Solution(rs.mapValues(cnvShapes(_)))
  }
  
  def cnvShapes(pair: (Seq[ShExLabel], Seq[ShExLabel])): InfoNode = {
    val (shapes,noShapes) = pair
    InfoNode(shapes.map(mkLabelExplanation(_)),noShapes.map(mkLabelExplanation(_)))
  }
  
  def mkLabelExplanation(lbl: ShExLabel): (ShapeLabel,Explanation) = {
    (ShapeLabel(lbl.toString),Explanation(""))
  }
  
  override def fromString(cs: CharSequence, format: String, base: Option[String]): Try[Schema] = {
    ShEx3.fromString(cs,format,base)
  }
  
  override def fromRDF(rdf: RDFReader): Try[Schema] = {
    for {
      (schema,pm) <- RDF2Schema.rdf2Schema(rdf)
    } yield ShEx3(schema)
  }
  
  override def serialize(format: String): Try[String] = {
    if (formats.contains(format))
      Success(schema.serialize(format))
    else
      Failure(throw new Exception(s"Format $format not supported to serialize $name. Supported formats=$formats")) 
  }
  
  override def empty: Schema = ShEx3.empty

  override def shapes: List[String] = {
    val pm = schema.pm
    schema.labels.map(_.show(pm))
  }
  
  override def pm: PrefixMap = schema.pm

}

object ShEx3 {
  def empty: ShEx3 = ShEx3(schema = ShExSchema.empty)
  
  def fromString(cs: CharSequence, format: String, base: Option[String]): Try[ShEx3] = { 
    ShExSchema.fromString(cs,format,base).map(p => ShEx3(p._1))
  }
  
}
