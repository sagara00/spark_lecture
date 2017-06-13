
import org.apache.spark.sql.SQLContext
import org.apache.spark.ml.feature.Normalizer
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier, DecisionTreeClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer, VectorAssembler}
import org.apache.spark.ml.classification.{GBTClassificationModel, GBTClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.tuning.{ParamGridBuilder, CrossValidator, TrainValidationSplit}
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.DecisionTreeClassificationModel
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer, VectorAssembler}
import org.apache.spark.sql.SparkSession

//val spark = SparkSession.builder().appName("Spark ML example").config("spark.some.config.option", "some-value").getOrCreate()
//val sc = spark.sparkContext

val sqlContext = new org.apache.spark.sql.SQLContext(sc)

// csv파일에 header유무 확인, delimiter 확인
// Input file loading..
val df = sqlContext.read.format("com.databricks.spark.csv").option("header", "true").option("inferSchema", "true").load("/Users/spark/iris.txt")

// schema 정보 및 data확인
df.printSchema

// 컬럼 개수 확인
df.schema.length

// 데이터 내용 흝어보기
df.show


// Training 과 Test 셋으로 Split Data (7:3비율)
val Array(trainingData, testData) = df.randomSplit(Array(0.7, 0.3))


// input columns로 feature 생성
val assembler = new VectorAssembler().setInputCols(Array("sepal_length", "sepal_width", "petal_length", "petal_width")).setOutputCol("features")

// LabelIndexer로 Label컬럼을 LabelIndex로 변경
val labelIndexer = new StringIndexer().setInputCol("species").setOutputCol("indexedLabel")

// FeatureIndexer
val featureIndexer = new VectorIndexer().setInputCol("features").setOutputCol("indexedFeatures").setMaxCategories(4)

/* param descriptions

    maxCategory : Threshold for the number of values a categorical feature can take. If a feature is found to have > maxCategories values, then it is declared continuous. Must be greater than or equal to 2.
    impurity : Criterion used for information gain calculation. Supported: "entropy" and "gini". (default = gini)
    maxBins : Maximum number of bins used for discretizing continuous features and for choosing how to split on features at each node.
    maxDepth : Maximum depth of the tree (>= 0).
    seed : Param for random seed.
    automodel : choose whether to use autoML
    terminateAccu : terminate automl when accuracy gain more than this value.


   default value

    maxCategory=20
    impurity="gini"
        impurity={gini,entropy}
    maxBins=32
    maxDepth=5
    seed = 1234L
*/
val algorithm = new DecisionTreeClassifier().setLabelCol("indexedLabel").setFeaturesCol("indexedFeatures") //.setImpurity("entropy").setMaxBins(32)
//val algorithm = new RandomForestClassifier().setLabelCol("indexedLabel").setFeaturesCol("indexedFeatures").setNumTrees(10)


// Label Converter
val labelConverter = new IndexToString().setInputCol("prediction").setOutputCol("predictedLabel").setLabels(labelIndexer.fit(trainingData).labels)

// Pipeline 만들기
val pipeline = new Pipeline().setStages(Array(assembler, labelIndexer, featureIndexer, algorithm, labelConverter))

// Training 
val model = pipeline.fit(trainingData)

// Test
val result = model.transform(testData)

// 결과 보기
result.printSchema
result.show


// Evaluation
val evaluator = new MulticlassClassificationEvaluator().setLabelCol("indexedLabel").setPredictionCol("prediction").setMetricName("accuracy")
val accuracy = evaluator.evaluate(result)

val evaluator = new MulticlassClassificationEvaluator().setLabelCol("indexedLabel").setPredictionCol("prediction").setMetricName("f1")
val f1 = evaluator.evaluate(result)

val evaluator = new MulticlassClassificationEvaluator().setLabelCol("indexedLabel").setPredictionCol("prediction").setMetricName("weightedPrecision")
val weightedPrecision = evaluator.evaluate(result)

val evaluator = new MulticlassClassificationEvaluator().setLabelCol("indexedLabel").setPredictionCol("prediction").setMetricName("weightedRecall")
val weightedRecall = evaluator.evaluate(result)

println("accu:"+accuracy+" f1:"+f1+" wPrecision:"+weightedPrecision+" wRecall:"+weightedRecall)

//DTC model 뽑아내기
val mdl = model.stages.toList.filter(_.isInstanceOf[DecisionTreeClassificationModel]).head.asInstanceOf[DecisionTreeClassificationModel]

//입력 feature 중요도 보기
mdl.featureImportances

//깊이, 노드수, 입력 feature 수
mdl.depth 
mdl.numNodes
mdl.numFeatures

//세팅 정보 보기
mdl.extractParamMap

//트리모양 보기
mdl.toDebugString
