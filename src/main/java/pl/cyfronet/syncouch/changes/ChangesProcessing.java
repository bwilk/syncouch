package pl.cyfronet.syncouch.changes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChangesProcessing {
	
	private ExecutorService executor;

	public void start() {
		if (executor == null) {
			System.out.println("Starting the world");
			executor = Executors.newFixedThreadPool(2);
			executor.submit(new WhitelistProcessor());
			executor.submit(new BlacklistProcessor());
		}
	}
	
	public void stop() throws InterruptedException {
		if (executor != null) {
			System.out.println("Stopping the world");
			executor.shutdownNow();
			executor.awaitTermination(15, TimeUnit.SECONDS);
			executor = null;
		}
	}
	
}
