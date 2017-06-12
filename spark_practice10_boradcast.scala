    import org.apache.spark.sql.SparkSession

    // default 4096, 너무 크면 병렬처리를 못해서 성능저하, 너무 작으면 block manager로 인해 성능저하 유발
    val blockSize ="4096"

    //broadcast block size 설정해서 새로운 spark session 생성
    val spark2 = SparkSession.builder().appName("Broadcast Test").config("spark.broadcast.blockSize", blockSize).getOrCreate()
    val sc2 = spark2.sparkContext

    //100만개 element를 갖는 array 생성
    val arr1 = (0 until 1000000).toArray

    for (i <- 0 until 3) {
      println("Iteration " + i)
      println("===========")
      val startTime = System.nanoTime
      val barr1 = sc2.broadcast(arr1)
      val observedSizes = sc2.parallelize(1 to 10, 2).map(_ => barr1.value.length)
      // Collect the small RDD so we can print the observed sizes locally.
      observedSizes.collect().foreach(i => println(i))
      println("Iteration %d took %.0f milliseconds".format(i, (System.nanoTime - startTime) / 1E6))
      println("\n")
    }

    sc2.stop
    spark2.stop
