import org.apache.spark.ml.feature._
import org.apache.spark.ml.clustering._
import org.apache.spark.ml.classification._
import org.apache.spark.ml.regression._
import org.apache.spark.sql.SQLContext
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.DataFrame


//programmatically defining schema 로 불러옴 (StructType() 직접작성)
var df = new SQLContext(sc).read.format("com.databricks.spark.csv").schema(StructType(Array(StructField("sepal_length",DoubleType,true),StructField("sepal_width",DoubleType,true),StructField("petal_length",DoubleType,true),StructField("petal_width",DoubleType,true),StructField("species",StringType,true)))).option("header", "true").option("delimiter", ",").load("iris.txt")

// species는 명목형이기 때문에, 수치로 바꿔줌
val StringIndexer_species = new StringIndexer().setInputCol("species").setOutputCol("si_species").setHandleInvalid("skip")

// vector assembler로 vector화
val VectorAssember_ = new VectorAssembler().setInputCols(Array("sepal_length","sepal_width","petal_length","petal_width","si_species")).setOutputCol("va_")

// P-norm 정규화 (1-> 유클리드, 2->멘해튼)
val Normalizer_ = new Normalizer().setInputCol("va_").setOutputCol("normedFeature").setP(2)


/* param descriptions

    initMode: The initialization algorithm. Supported options: 'random' and 'k-means||'. (default: k-means||)
    initSteps: The number of steps for k-means|| initialization mode. Must be > 0. (default: 5)
    k:The number of clusters to create (k). Must be > 1. Default: 2.
    maxIter:Set the maximum number of iterations. Default is 20.
    toler: Set the convergence tolerance of iterations. Default is 1.0E-4
    seed: random seed (default: -1689246527)

   default value

    initMode="k-means||"
        initMode={ k-means||, random }
    initSteps=5
    k=2
    maxIter=20
    toler=1.0E-4

 */

//k-means clustering 수행
val KMeans_ = new KMeans().setFeaturesCol("normedFeature").setPredictionCol("kmeansOutput").setInitMode("k-means||").setInitSteps(5).setK(3).setMaxIter(20).setSeed(1234).setTol(0.0001)

//pipeline 디자인 (명목형 수치화 -> 벡터화 -> 정규화 -> 클러스터링)
val pline = new Pipeline().setStages(Array(StringIndexer_species,VectorAssember_,Normalizer_,KMeans_))

//디자인한 파이프라인 실행 (트레이닝, 모델생성)
val MDL = pline.fit(df)

//데이터 적용 (일 안함, laziness)
val TEST_DF = MDL.transform(df)

// 모델 정보를 보기위해 모델 추출
val model = MDL.stages.toList.filter(_.isInstanceOf[KMeansModel]).head.asInstanceOf[KMeansModel]

//입력 된 param이 무엇이었는지
val params = model.extractParamMap.toString

//cluster 센터가 어디인지
val clusterCenters = model.clusterCenters.mkString(",")

//cluster로 
val clusterSizes = model.summary.clusterSizes.mkString(",")



//모두 출력
TEST_DF.show(TEST_DF.count.toInt)

//어떻게 벡터화, 정규화 되었나?
TEST_DF.limit(1).select("va_", "normedFeature").show(false)

//잘 클러스터링 되었나?
TEST_DF.select("species", "kmeansOutput").show(150)

//모든 과정들
TEST_DF.limit(1).show(false)



//모델 및 파이프라인 관리

//파이프라인 저장
pline.save("/Users/spark/k_means_iris_pipeline")

//덮어쓰기
pline.write.overwrite.save(("k_means_iris_pipeline"))

//불러오기
val pline = Pipeline.load("k_means_iris_pipeline")

//학습 된 모델 저장
MDL.save("k_means_iris_model")

//불러오기
val MDL = PipelineModel.load("k_means_iris_model")