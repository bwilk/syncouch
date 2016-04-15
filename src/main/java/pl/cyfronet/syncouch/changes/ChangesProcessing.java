package pl.cyfronet.syncouch.changes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangesProcessing {
	
	private final Logger logger = LoggerFactory.getLogger(ChangesProcessing.class);
	
	private ExecutorService executor;

	public void start() {
		if (executor == null) {
			logger.info("Starting changes processing");
			Blacklist blacklist = new MemoryBlacklist(); 
			CouchClientFactory clientFactory = new CouchClientFactory();
			executor = Executors.newFixedThreadPool(2);
			executor.submit(new WhitelistProcessor(blacklist, clientFactory));
			executor.submit(new BlacklistProcessor(blacklist, clientFactory));
		}
	}
	
	public void stop() throws InterruptedException {
		if (executor != null) {
			logger.info("Stopping changes processing");
			executor.shutdownNow();
			executor.awaitTermination(15, TimeUnit.SECONDS);
			executor = null;
		}
	}
	
}
