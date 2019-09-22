# Index Aggregation
Objective:
	Create a web service to
	 I.  Publish tickers, and 
	 II. Receive stats of tickers for the last 60 seconds.

Components:
	I. Publish Tickers

		a. TickController - Receives ticks, pushes to a blocking queue and returns desired response immediately.

		b. BlockQueueDispatcher - Polls from the queue and submits to a custom SequencedThreadPoolExecutor

		c. SequencedThreadPoolExecutor - Execute requests in parallel, however if a same instrument id's tick is received, that is processed in sequential on the same thread. Submits the task to TickProcessor.

		d. TickProcessor - Puts the ticks to a cache which maintains the statistics of all tickers, and each ticker separately.

		e. TickerDataBuilder - Calculates and build statistics for tickers.

		f. CacheCleanScheduler - A scheduled job to clean the cache every second, and update the statistics in the cache.

	II. Receive statistics
		
		g. StatisticsController - Receive requests to fetch statistics for one or all.
		
		h. TickerDataService - Receives control from StatisticsController to fetch the corresponding results from the cache, and transform to return response as intended.
		
	III. Helper classes:
		Necessary POJOs created and used.
    

How to run:
	a. Compile and package executin the following command from the project folder in the command prompte:
		mvn package
	b. Corresponding executable jar(index-aggregation-0.0.1-SNAPSHOT.jar) would be created in target folder of project. Execute following command:
		target/java -jar index-aggregation-0.0.1-SNAPSHOT.jar
	
Submit requests as intended to localhost:8080.
	
For API  help refer to : http://localhost:8080/swagger-ui.html
