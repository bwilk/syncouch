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
		logger.debug("Whitelist processor started");
		try {
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
				String seq = feed.getSeq();
				logger.debug("doc {} change {} encountered", docId, seq);
				if (blacklist.contains(docId)) {
					logger.debug("doc {} is on the blacklist - updating seq to {}", docId, seq);
					blacklist.update(docId, seq); 
				} else {
					JsonObject doc = feed.getDoc();
					try {
						DocChangeHandler documentHandler = new JoltChangesHandler(connectionManager.targetDbClient());
						if (feed.isDeleted()) {
							documentHandler.handleDocDelete(docId); 
						} else {
							documentHandler.handleDocChange(docId, doc);
						}
						logger.debug("doc {} change {} handled ", docId, seq);
					} catch (Exception e) {
						logger.debug("doc {} change {} cannot be handled - updating blacklist", docId, seq, e);
						blacklist.update(docId, feed.getSeq()); 
					} 
				}
			}
			logger.debug("Whitelist processor finished successfully");
		} catch (Exception e) {
			logger.debug("Unexpected error - whitelist processor is dead", e);
		}
	}
	
}