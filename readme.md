> Putty로 SSH 접속

localhost:2222

> 실습 파일 다운로드
wget https://raw.githubusercontent.com/sagara00/spark_lecture/master/iris.txt
wget https://raw.githubusercontent.com/sagara00/spark_lecture/master/pages.txt

# 10장
## broadcast, accumulator 예제

아래 예제를 broadcast 적용 해보기

```scala

//1. collection으로부터 RDD 생성 후
//2. pws를 참조하여 key에 해당하는 값을 참조, map partition RDD 생성
//3. collect로 array[String] 생성
val pws = Map("Apache Spark" -> "http://spark.apache.org/", "Scala" -> "http://www.scala-lang.org/")

val websites = sc.parallelize(Seq("Apache Spark", "Scala")).map(pws).collect

//pws가 대량의 데이터일 경우 속도저하 유발함. 분산처리하는 모든 executor에 pws 네트워크전송 유발

```