package pl.cyfronet.syncouch.playground;

import pl.cyfronet.syncouch.changes.ChangesProcessing;

public class ChangesProcessingTest {

	public static void main(String[] args) throws InterruptedException {
		ChangesProcessing changesProcessing = new ChangesProcessing();
		changesProcessing.start();
	}
	
}
