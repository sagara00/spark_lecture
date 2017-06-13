# 준비
> Putty로 SSH 접속

localhost:2222

> Spark 계정으로 접속
```shell
su - spark
```
> spark 2.0 버전으로 실행
```shell
export SPARK_MAJOR_VERSION=2
./spark-shell

```

> 실습 파일 다운로드
```shell
wget https://raw.githubusercontent.com/sagara00/spark_lecture/master/iris.txt
wget https://raw.githubusercontent.com/sagara00/spark_lecture/master/pages.txt
```
# 10장
## broadcast, accumulator 예제
> 아래 코드에 broadcast 적용 해보기

```scala

//1. collection으로부터 RDD 생성 후
//2. pws를 참조하여 key에 해당하는 값을 참조, map partition RDD 생성
//3. collect로 array[String] 생성
val pws = Map("Apache Spark" -> "http://spark.apache.org/", "Scala" -> "http://www.scala-lang.org/")

val websites = sc.parallelize(Seq("Apache Spark", "Scala")).map(pws).collect

//pws가 대량의 데이터일 경우 속도저하 유발함. 분산처리하는 모든 executor에 pws 네트워크전송 유발

```

> 모든 단어의 평균 길이 구하기

```scala
val words = sc.textFile("/Users/Sagara/spark/README.md").flatMap(line => line.split(' '))
//Double = 5.73015873015873
```
> accumulator 로 구하기
```scala
import org.apache.spark.Accumulator
//TODO:
```

# 11장
## DataFrame 실습

> iris data 조작하기

[실습코드](https://github.com/sagara00/spark_lecture/blob/master/spark_practice11.scala)

# 12장
## 1. 반복알고리즘 실습
> 아래 page rank 코드에서 TODO 부분 구현해보기

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

## 2. classification 실습

데이터명 : IRIS (아이리스, 붗꽃 데이터)
레코드수 : 150개 
필드개수 : 5개 

![iris](http://trustmeiamadeveloper.com/content/images/2016/08/iris_types.png)


데이터설명 : 아이리스(붓꽃) 데이터에 대한 데이터이다. 꽃잎의 각 부분의 너비와 길이등을 측정한 데이터이며 150개의 레코드로 구성되어 있다. 아이리스 꽃은 아래의 그림과 같다. 프랑스의 국화라고 한다. 
필드의 이해 : 1번째부터 4번째의 4개의 필드는 입력 변수로 사용되고, 맨 아래의 Species 속성이 목표(종속) 변수로 사용된다.

- Sepal Length   꽃받침의 길이 정보이다.
- Sepal Width    꽃받침의 너비 정보이다.
- Petal Length   꽃잎의 길이 정보이다.
- Petal Width    꽃잎의 너비 정보이다.  
- Species    꽃의 종류 정보이다.  setosa / versicolor / virginica 의 3종류로 구분된다.

[실습코드](https://github.com/sagara00/spark_lecture/blob/master/spark_practice12_classification.scala)

## 3. clustering 실습
iris dataset 이용
입력 feature 정규화를 위해 transformer를 거쳐서 k-means 로 clustering

[실습코드](https://github.com/sagara00/spark_lecture/blob/master/spark_practice12_clustering.scala)
