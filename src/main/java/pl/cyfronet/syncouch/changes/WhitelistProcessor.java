package pl.cyfronet.syncouch.changes;

import org.lightcouch.Changes;
import org.lightcouch.ChangesResult;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbInfo;

import com.google.gson.JsonObject;

public class WhitelistProcessor implements Runnable {

	private CouchClient connectionManager;
	private DocChangeHandler documentHandler;
	
	public WhitelistProcessor() {
		connectionManager = new CouchClient();
	}
	
	@Override
	public void run() {
		System.out.println("Starting whitelist processing");
		documentHandler = new DocChangeHandler(connectionManager.targetDbClient());
		
		CouchDbClient dbClient = connectionManager.sourceDbClient();
		CouchDbInfo dbInfo = dbClient.context().info();
		
		String since = dbInfo.getUpdateSeq();
		Changes changes = dbClient.changes().includeDocs(true).since(since)
				.timeout(1000).heartBeat(30000).continuousChanges();
		while (changes.hasNext()) {
			ChangesResult.Row feed = changes.next();
			String docId = feed.getId();
			JsonObject doc = feed.getDoc();
			try {
				if (feed.isDeleted()) {
					documentHandler.handleDocDelete(docId); 
				} else {
					documentHandler.handleDocChange(docId, doc);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (Thread.interrupted()) {
				shutdown();
		        return;
		    }
		}
		shutdown();
	}
	
	private void shutdown() {
		System.out.println("Stopping whitelist processing");
	}
	
}