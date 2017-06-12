    import org.apache.spark.sql.SparkSession

    val blockSize ="4096"

    val spark2 = SparkSession.builder().appName("Broadcast Test").config("spark.broadcast.blockSize", blockSize).getOrCreate()
    val sc2 = spark2.sparkContext

    val slices = 2
    val num = 1000000

    val arr1 = (0 until num).toArray

    for (i <- 0 until 3) {
      println("Iteration " + i)
      println("===========")
      val startTime = System.nanoTime
      val barr1 = sc2.broadcast(arr1)
      val observedSizes = sc2.parallelize(1 to 10, slices).map(_ => barr1.value.length)
      // Collect the small RDD so we can print the observed sizes locally.
      observedSizes.collect().foreach(i => println(i))
      println("Iteration %d took %.0f milliseconds".format(i, (System.nanoTime - startTime) / 1E6))
      println("\n")
    }

    sc2.stop
    spark2.stop




    val blockSize ="4096"

    val spark2 = SparkSession.builder().appName("Broadcast Test").config("spark.broadcast.blockSize", blockSize).getOrCreate()
    val sc2 = spark2.sparkContext

    val slices = 2
    val num = 1000000

    val arr1 = (0 until num).toArray

    for (i <- 0 until 3) {
      println("Iteration " + i)
      println("===========")
      val startTime = System.nanoTime
      //val barr1 = sc2.broadcast(arr1)
      val observedSizes = sc2.parallelize(1 to 10, slices).map(_ => arr1.length)
      // Collect the small RDD so we can print the observed sizes locally.
      observedSizes.collect().foreach(i => println(i))
      println("Iteration %d took %.0f milliseconds".format(i, (System.nanoTime - startTime) / 1E6))
      println("\n")
    }
    
    sc2.stop
    spark2.stop