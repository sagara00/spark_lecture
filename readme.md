> Putty로 SSH 접속

localhost:2222

> 실습 파일 다운로드
wget https://raw.githubusercontent.com/sagara00/spark_lecture/master/iris.txt
wget https://raw.githubusercontent.com/sagara00/spark_lecture/master/pages.txt

# 10장
## broadcast, accumulator 예제
### (실습문제1) 아래 코드에 broadcast 적용 해보기

```scala

//1. collection으로부터 RDD 생성 후
//2. pws를 참조하여 key에 해당하는 값을 참조, map partition RDD 생성
//3. collect로 array[String] 생성
val pws = Map("Apache Spark" -> "http://spark.apache.org/", "Scala" -> "http://www.scala-lang.org/")

val websites = sc.parallelize(Seq("Apache Spark", "Scala")).map(pws).collect

//pws가 대량의 데이터일 경우 속도저하 유발함. 분산처리하는 모든 executor에 pws 네트워크전송 유발

```

## (실습문제2) 모든 단어의 평균 길이 구하기

```scala
val words = sc.textFile("/Users/Sagara/spark/README.md").flatMap(line => line.split(' '))
```

# 11장
## DataFrame 실습
https://github.com/sagara00/spark_lecture/blob/master/spark_practice11.scala

# 12장
## 1. 반복알고리즘 실습
아래 page rank 코드에서 TODO 부분 구현해보기

```scala
val iters = 10
val lines = spark.read.textFile("/Users/sagara/pages.txt").rdd

// \\s는 white space (regular expression)
// 파일이 source -> destination 구조. parts를 pair로 묶어 link에 입력
//중복제거, source로 group by, cache로 메모리에 영속화

val links = lines.map{ s =>
  val parts = s.split("\\s+")
  (parts(0), parts(1))
}.distinct().groupByKey().cache()

//compactBuffer (scala의 arrayBuffer, java의 AyList)
links.collect.foreach(println)

//links.mapValues(f) 는 pair RDD구조에서, links.map {case (k, v) => (k, f(v))}의 축약
var ranks = links.mapValues(v => 1.0)

ranks.collect.foreach(println)

//반복 알고리즘 동작
for (i <- 1 to iters) {
//rank를 이웃으로 분배
  val contribs = links.join(ranks).values.flatMap{ case (urls, rank) =>
    //TODO: 구현1 기여도 계산
    val size = urls.size
    urls.map()
  }
  //TODO: 구현2. 새로운 rank 계산
  ranks = 

  //출력
  println("iters:"+i)
  ranks.collect().foreach(tup => println(tup._1 + " has rank: " + tup._2 + "."))
  println("\n")

}
```

## 2. 