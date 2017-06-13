val iters = 10
val lines = spark.read.textFile("pages.txt").rdd

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
    val size = urls.size
    urls.map(url => (url, rank / size))
  }
  //새로운 rank 계산
  ranks = contribs.reduceByKey(_ + _).mapValues(0.15 + 0.85 * _)

  //출력
  println("iters:"+i)
  ranks.collect().foreach(tup => println(tup._1 + " has rank: " + tup._2 + "."))
  println("\n")

}