import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.feature.PCA
import org.apache.spark.{SparkContext,SparkConf}
import org.apache.spark.SparkContext._

import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

object PCAApp {
  def main(args: Array[String]) {
    if (args.length < 2) {
      println("usage: <input> <dimensions>")
      System.exit(0)
    }
    val conf = new SparkConf
    conf.setAppName("Spark PCA Example")
    val sc = new SparkContext(conf)
    
    val input = args(0)
    val dimensions = args(1).toInt

    // Load and parse the data
    // val parsedData = sc.textFile(input)
    println("START load")
    var start = System.currentTimeMillis();
    val data = MLUtils.loadLabeledPoints(sc, input).cache()
    val loadTime = (System.currentTimeMillis() - start).toDouble / 1000.0

    // build model
    println("START training")
    start = System.currentTimeMillis();
    val pca = new PCA(dimensions).fit(data.map(_.features))
    val trainingTime = (System.currentTimeMillis() - start).toDouble / 1000.0

    println("START test")
    start = System.currentTimeMillis();
    val training_pca = data.map(p => p.copy(features = pca.transform(p.features)))
    val numData = training_pca.count();
    val testTime = (System.currentTimeMillis() - start).toDouble / 1000.0

    println(compact(render(Map("loadTime" -> loadTime, "trainingTime" -> trainingTime, "testTime" -> testTime))))
    println("Number of Data = " + numData)
    sc.stop()
  }
}