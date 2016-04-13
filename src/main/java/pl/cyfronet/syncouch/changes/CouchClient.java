package pl.cyfronet.syncouch.changes;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

public class CouchClient {

	private CouchDbClient sourceDbClient;
	private CouchDbClient targetDbClient;

	public CouchDbClient sourceDbClient() {
		if (sourceDbClient != null) {
			return sourceDbClient;
		}
		CouchDbProperties properties = new CouchDbProperties()
				.setDbName("test-base-source").setCreateDbIfNotExist(true)
				.setProtocol("http").setHost("localhost").setPort(5984)
				.setMaxConnections(100).setConnectionTimeout(0);
		sourceDbClient = new CouchDbClient(properties);
		return sourceDbClient;
	}
	
	public CouchDbClient targetDbClient() {
		if (targetDbClient != null) {
			return targetDbClient;
		}
		CouchDbProperties properties = new CouchDbProperties()
				.setDbName("test-base-target").setCreateDbIfNotExist(true)
				.setProtocol("http").setHost("localhost").setPort(5984)
				.setMaxConnections(100).setConnectionTimeout(0);
		targetDbClient  = new CouchDbClient(properties);
		return targetDbClient;
	}

}
