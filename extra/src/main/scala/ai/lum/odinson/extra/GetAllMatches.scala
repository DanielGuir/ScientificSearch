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

object GetAllMatches extends App {
  var config = ConfigFactory.load()
  config = config.withValue(
          "odinson.indexDir",
          ConfigValueFactory.fromAnyRef(args(1))
        )
  val extractorEngine = ExtractorEngine.fromConfig(config)
  val processorType = config.apply[String]("odinson.extra.processorType")
  val displayField = config.apply[String]("odinson.displayField")
  val proc: Processor = getProcessor(processorType)
  proc.annotate("warm up")
  val reader = CSVReader.open(new File(args(0)))
  val data = reader.allWithHeaders()
  val save_path = "/usr/xtmp/rg315/retrieve_results_odinson_batched/"
  var count = 0
  for (row <- data){
    count += 1
    try{
      getQueryMatches(row("generated_query"), s"${save_path}/${args(2)}/${row("query_index")}.csv")
     }
     catch {
       case _: Throwable => println(s"Failed to process query ${row("query_index")}: ${row("generated_query")}")
     }
    if (count % 100 == 0){
      println(s"Processed $count Queries")
    }
  }
  
  def getQueryMatches(querySentence: String, outputFile: String){
    val query: Query = new Query(querySentence, debug=false)
    query.preProcess(proc)
    query.search(proc, extractorEngine)

    val (resultText, resultDoc, resultCaptures, capWordsBuilder) =
      query.generateResult(-1, extractorEngine, proc, displayField)
    val file = new File(outputFile);
    file.getParentFile().mkdirs(); // Will create parent directories if not exists
    file.createNewFile();
    val pw = new PrintWriter(file);
    if (resultText.length > 0) {
      pw.write(s""""Raw Text",""")
      for (i <- capWordsBuilder) {
        pw.write(s""""$i",""")
      }
      pw.write("\n")
    }
    for (i <- 0 until resultText.length) {
      pw.write(s""""${resultText(i).replaceAll("\"", "\"\"")}",""")
      for (j <- resultCaptures(i)) {
        pw.write(s""""${j.replaceAll("\"", "\"\"")}",""")
      }
      pw.write("\n")
    }
    pw.close()
  }

}
