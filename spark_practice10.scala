export SPARK_MAJOR_VERSION=2
spark-shell

// import org.apache.spark.sql.SparkSession 
// val spark = SparkSession.builder()
//                         .appName("Spark example")
//                         .config("spark.some.config.option", "some-value")
//                         .getOrCreate()

// val sc = spark.sparkContext


//간단한 broadcast variable 생성
val broadcastVar = sc.broadcast(Array(1, 2, 3))
broadcastVar.value.foreach(println)


//참고할 site정보 Map 생성
val pws = Map("Apache Spark" -> "http://spark.apache.org/", "Scala" -> "http://www.scala-lang.org/")

//1. collection으로부터 RDD 생성 후
//2. pws를 참조하여 key에 해당하는 값을 참조, map partition RDD 생성
//3. collect로 array[String] 생성
val websites = sc.parallelize(Seq("Apache Spark", "Scala")).map(pws).collect

//pws가 대량의 데이터일 경우 속도저하 유발함. 분산처리하는 모든 executor에 pws 네트워크전송 유발

//site정보 broadcast 변수 생성
val pwsB = sc.broadcast(pws)
// 동일한 처리. 단, .value로 접근 해야함
val websites = sc.parallelize(Seq("Apache Spark", "Scala")).map(pwsB.value).collect

//각 worker node 단위로 전송




//accumulator

val accum = sc.accumulator(0, "Accumulator Example")
sc.parallelize(Array(1, 2, 3)).foreach(x => accum += x)
println(accum.value)

val accum = sc.longAccumulator("My Accumulator")
sc.parallelize(Array(1, 2, 3, 4)).foreach(x => accum.add(x))


//kryo serializer
// import org.apache.spark.SparkConf
// import org.apache.spark.SparkContext
// val conf = new SparkConf().set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
// val sc = new SparkContext(conf)