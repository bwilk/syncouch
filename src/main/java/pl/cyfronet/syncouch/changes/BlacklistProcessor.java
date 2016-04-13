package pl.cyfronet.syncouch.changes;

public class BlacklistProcessor implements Runnable {

	@Override
	public void run() {
		System.out.println("Starting blacklist processing");
		while (true) {
			try {
				if (Thread.interrupted()) {
					throw new InterruptedException();
			    }
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Stopping blacklist processing");
				return;
			}
		}
	}

}
