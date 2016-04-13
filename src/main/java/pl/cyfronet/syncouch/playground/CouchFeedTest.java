package pl.cyfronet.syncouch.playground;

import org.lightcouch.Changes;
import org.lightcouch.ChangesResult;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbInfo;

import com.google.gson.JsonObject;

public class CouchFeedTest {
	
	public static void main(String[] args) {

		// get latest update seq
		CouchDbClient dbClient = CouchClient.dbClient();

		CouchDbInfo dbInfo = dbClient.context().info();
		String since = dbInfo.getUpdateSeq();

		// feed type continuous
		Changes changes = dbClient.changes().includeDocs(true).since(since)
				.heartBeat(30000).continuousChanges();

		// live access to continuous feeds
		while (changes.hasNext()) {
			ChangesResult.Row feed = changes.next();

			String docId = feed.getId();
			JsonObject doc = feed.getDoc();

			System.out.println(">>> feed: " + doc);
			
			// changes.stop(); // stop the running feed
		}

	}
}
