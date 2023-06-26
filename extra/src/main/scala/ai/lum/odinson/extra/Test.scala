package export

import ai.lum.common.ConfigFactory
import com.typesafe.config.{ Config, ConfigValueFactory }
import ai.lum.common.ConfigUtils._
import engine._
import engine.QueryExceptions._
import org.clulab.processors.clu.CluProcessor
import org.clulab.processors.corenlp.CoreNLPProcessor
import org.clulab.processors.fastnlp.FastNLPProcessor
import org.clulab.processors.{ Document, Processor }
import java.io._
import ai.lum.odinson.ExtractorEngine
import ai.lum.odinson.extra.utils.{ ExtraFileUtils, ProcessorsUtils }
import ai.lum.odinson.extra.utils.ProcessorsUtils.getProcessor
import com.github.tototoshi.csv._

object Test extends App {
    var config = ConfigFactory.load()
  config = config.withValue(
          "odinson.indexDir",
          ConfigValueFactory.fromAnyRef("/usr/xtmp/rg315/wikipedia_indices_splitted/0/")
        ).withValue(
          "odinson.extra.processorType",
          ConfigValueFactory.fromAnyRef("FastNLPProcessor")
        )
  val extractorEngine = ExtractorEngine.fromConfig()
  val processorType = config.apply[String]("odinson.extra.processorType")
  val displayField = config.apply[String]("odinson.displayField")
  val proc: Processor = getProcessor(processorType)
  proc.annotate("warm up")
    val querySentence = "Cristiano $Ronaldo, as a forward for Italian club :ANS, is also the captain of the Portugal national team."
    val query: Query = new Query(querySentence, debug=true)
    query.preProcess(proc)
    query.search(proc, extractorEngine)

    val (resultText, resultDoc, resultCaptures, capWordsBuilder) =
        query.generateResult(-1, extractorEngine, proc, displayField)
  

}
