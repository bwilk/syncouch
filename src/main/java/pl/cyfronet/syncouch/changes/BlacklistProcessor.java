package pl.cyfronet.syncouch.changes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlacklistProcessor implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(BlacklistProcessor.class);
	
	@Override
	public void run() {
		logger.debug("Blacklist processor started");
		while (true) {
			try {
				if (Thread.interrupted()) {
					throw new InterruptedException();
			    }
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.debug("Blacklist processor finished");
				return;
			}
		}
	}

}
