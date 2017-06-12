//모든 단어의 평균 길이
val words = sc.textFile("/Users/sagara/spark/README.md").flatMap(line => line.split(' '))
words.map(word => (word, word.length)).reduceByKey((key,value) => key + value).map(x => x._2).reduce((x,y)=>x+y).toDouble / words.count


//accumulator 사용
import org.apache.spark.Accumulator

def addTotals (word:String, words:Accumulator[Int], letters:Accumulator[Double]):Unit ={
  words += 1
  letters += word.length
}

val totalWords = sc.accumulator(0)
val totalLetters = sc.accumulator(0.0)

words.foreach(word => addTotals(word, totalWords, totalLetters))

println("Average word length: ", totalLetters.value/totalWords.value)