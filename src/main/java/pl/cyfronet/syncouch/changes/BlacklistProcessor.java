package pl.cyfronet.syncouch.changes;

import java.util.List;
import java.util.Map;

import org.lightcouch.ChangesResult.Row;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class BlacklistProcessor implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(BlacklistProcessor.class);
	
	private static final int STEP_DELAY = 10000;

	private CouchClientFactory connectionManager;
	private Blacklist blacklist;

	public BlacklistProcessor(Blacklist blacklist,
			CouchClientFactory connectionManager) {
		this.blacklist = blacklist;
		this.connectionManager = connectionManager;
	}

	@Override
	public void run() {
		logger.debug("Blacklist processor started");
		try {
			DocChangeHandler documentHandler = new JoltChangeHandler(connectionManager.targetDbClient());
			CouchDbClient dbClient = connectionManager.sourceDbClient();
			
			while (!Thread.interrupted()) {
				Map<String, Integer> docsToBeHandled = blacklist.docsToBeHandled();
				logger.debug("blacklist processing in progress - blacklist: {}", docsToBeHandled.keySet());
				for (String docId : docsToBeHandled.keySet()) {
					Integer seq = docsToBeHandled.get(docId);
					logger.debug("doc {} change {} - attempting to handle again", docId, seq);
					try {
						String filter = String.format("_doc_ids&doc_ids=[\"%s\"]", docId);
						String since = Integer.toString(seq - 1); 
						/*
						 * The above is strange but couchdb works like that
						 */
						List<Row> results = dbClient.changes()
								.includeDocs(true).since(since).timeout(1000)
								.heartBeat(30000).limit(1).filter(filter)
								.getChanges().getResults();
						Row row = results.get(0);
						JsonObject doc = row.getDoc();
						try {
							if (row.isDeleted()) {
								documentHandler.handleDocDelete(docId);
							} else {
								documentHandler.handleDocChange(docId, doc);
							}
							blacklist.docHandled(docId);
							logger.debug("doc {} handled {}", docId, doc);
						} catch (DocChangesHandlerException e) {
							logger.debug("doc {} cannot be handled", docId, e);
						}
					} catch (Exception e) {
						logger.warn("doc {} cannot be handled - unexpected error", docId, e);
					}
				}
				logger.debug("blacklist processing finished, waiting {}s "
						+ "- blacklist: {}", STEP_DELAY / 1000, blacklist.docsToBeHandled().keySet());
				try {
					Thread.sleep(STEP_DELAY);
				} catch (InterruptedException e) {
					// Thread.interrupted will be checked anyway by the loop condition
				}
			}
			logger.debug("Blacklist processor finished successfully");
		} catch (Exception e) {
			logger.error("Unexpected exception - blacklist processor is dead", e);
		}
	}

}
