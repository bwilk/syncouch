package pl.cyfronet.syncouch.changes;

import org.lightcouch.Changes;
import org.lightcouch.ChangesResult;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class WhitelistProcessor implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(WhitelistProcessor.class);
	
	private CouchClientFactory connectionManager;
	private DocChangeHandler documentHandler;
	
	private String sinceMarker;
	
	public WhitelistProcessor() {
		connectionManager = new CouchClientFactory();
		sinceMarker = Integer.toString(0);
	}
	
	@Override
	public void run() {
		logger.debug("Whitelist processor started");
		
		documentHandler = new DocChangeHandler(connectionManager.targetDbClient());		
		
		CouchDbClient dbClient = connectionManager.sourceDbClient();
		CouchDbInfo dbInfo = dbClient.context().info();
		
		String since = sinceMarker == null ? dbInfo.getUpdateSeq() : sinceMarker;
		
		Changes changes = dbClient.changes().includeDocs(true).since(since)
				.timeout(1000).heartBeat(30000).continuousChanges();
		
		while (!Thread.interrupted() && changes.hasNext()) {
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
		}

		logger.debug("Whitelist processor finished");
	}
	
}