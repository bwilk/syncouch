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
	private Blacklist blacklist;
	
	private Integer sinceMarker;
	
	public WhitelistProcessor(Blacklist blacklist, CouchClientFactory connectionManager) {
		this.blacklist = blacklist;
		this.connectionManager = connectionManager;
		this.sinceMarker = 1;
	}
	
	@Override
	public void run() {
		try {
			logger.debug("Whitelist processor started");

			DocChangeHandler documentHandler = new FakeChangesHandler(connectionManager.targetDbClient());		
			CouchDbClient dbClient = connectionManager.sourceDbClient();
			CouchDbInfo dbInfo = dbClient.context().info();
			
			Integer since = (sinceMarker == null ? Integer.parseInt(dbInfo.getUpdateSeq()) : sinceMarker) - 1;
			/*
			 * The above is strange but couchdb works like that
			 */
			Changes changes = dbClient.changes().includeDocs(true).since(since.toString())
					.timeout(1000).heartBeat(30000).continuousChanges();
			
			while (!Thread.interrupted() && changes.hasNext()) {
				ChangesResult.Row feed = changes.next();
				String docId = feed.getId();
				if (blacklist.contains(docId)) {
					logger.debug("doc {} change encountered but it is present on blacklist - updating seq", docId);
					blacklist.update(docId, feed.getSeq()); 
				} else {
					JsonObject doc = feed.getDoc();
					try {
						if (feed.isDeleted()) {
							documentHandler.handleDocDelete(docId); 
						} else {
							documentHandler.handleDocChange(docId, doc);
						}
					} catch (DocChangesHandlerException e) {
						logger.debug("doc {} not handled properly - will be added to blacklist", docId);
						blacklist.update(docId, feed.getSeq()); 
					}
				}
			}
			logger.debug("Whitelist processor finished");
		} catch (Exception e) {
			logger.debug("Unexpected exception - whitelist processor is dead {}", e);
		}
	}
	
}