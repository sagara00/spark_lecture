//모든 단어의 평균 길이
val words = sc.textFile("README.md").flatMap(line => line.split(' '))

//words.map(word => (word, word.length)).reduceByKey((key,value) => key + value).map(x => x._2).reduce((x,y)=>x+y).toDouble / words.count
words.map(word => word.length).reduce((x,y)=>x+y).toDouble / words.count

//accumulator 사용
import org.apache.spark.Accumulator

def addTotals (word:String, words:Accumulator[Int], letters:Accumulator[Double]):Unit ={
  words += 1
  letters += word.length
}

val totalWords = sc.accumulator(0)
val totalLetters = sc.accumulator(0.0)

//accumulator update는 map 대신 foreach를 사용해야함. 정확성을 위해. action(즉시 실행되는)으로 side effect 유발함수는 foreach 사용
words.foreach(word => addTotals(word, totalWords, totalLetters))

println("Average word length: ", totalLetters.value/totalWords.value)


//accumulator example

val accum = sc.longAccumulator("My Accumulator")
sc.parallelize(Array(1, 2, 3, 4)).foreach(x => accum.add(x))
accum.value


