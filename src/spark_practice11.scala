
import org.apache.spark.sql.SQLContext
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.sql.SparkSession

//val spark = SparkSession.builder().appName("Spark ML example").config("spark.some.config.option", "some-value").getOrCreate()
//val sc = spark.sparkContext

val sqlContext = new org.apache.spark.sql.SQLContext(sc)

// csv파일에 header유무 확인, delimiter 확인
// Input file loading..
val df = sqlContext.read.format("com.databricks.spark.csv").option("header", "true").option("inferSchema", "true").load("/Users/spark/iris.txt")

// df. 탭
df.

// schema 정보 및 data확인
df.printSchema

// 컬럼 개수 확인
df.schema.length

// 데이터 내용 흝어보기
df.show

import org.apache.spark.storage.StorageLevel

//메모리 영속화
df.persist(StorageLevel.MEMORY_ONLY)

//영속화 해제
df.unpersist()
//컬럼명 리턴
df.columns
//타입 array 리턴
df.dtypes
//디버그정보 출력
df.explain

//Row object의 array 반환
df.collect

//10개 출력하기
df.take(10).foreach(println)
df.take(10).show //오류 

// dataframe을 리턴하는 것은 limit
df.limit(10).show

//아래는 같음
df.take(1)
df.first

//sepal length의 평균
df.select(avg($"sepal_length")).show
//val mean = df.select(avg($"sepal_length")).first.getDouble(0)

//df에서 rdd생성
val rdd = df.rdd

//sepal length의 평균 (RDD)
rdd.map(x=> (x.getDouble(0))).reduce((x,y)=>x+y) / rdd.count

//종(species)의 중복 제거
df.select("species").distinct.show

//정렬
df.sort("sepal_width").show
df.orderBy("sepal_width").show  //orderBy는 sort의 alias임
//def orderBy(sortCol: String, sortCols: String*): Dataset[T] = sort(sortCol, sortCols : _*)

//내림차순 정렬
df.sort($"sepal_width".desc).show


// petal_width가 0.5 이상인 것
df.where("petal_width > 0.5").show

//통계정보 보기 (숫자타입)
df.select("sepal_width").describe().collect().mkString(",")

//통계정보 보기 (문자 타입)
df.groupBy("species").count.sort(desc("count")).collect().mkString(",")

//sql문 작성
// setosa종의 sepal 길이, petal 길이 출력
df.registerTempTable("iris") //테이블 생성
spark.sql("""select sepal_length, petal_length, species from iris where species = "setosa" """).show

//rdd에서 df 생성 스키마+rdd = dataframe
val schema = df.schema
val df2 = spark.createDataFrame(rdd, schema)



