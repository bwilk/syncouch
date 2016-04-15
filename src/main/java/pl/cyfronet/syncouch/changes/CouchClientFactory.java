package pl.cyfronet.syncouch.changes;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

public class CouchClientFactory {

	public CouchDbClient sourceDbClient() {
		CouchDbProperties properties = new CouchDbProperties()
				.setDbName("test-base-source").setCreateDbIfNotExist(false)
				.setProtocol("http").setHost("localhost").setPort(5984)
				.setMaxConnections(100).setConnectionTimeout(0);
		return new CouchDbClient(properties);
	}
	
	public CouchDbClient targetDbClient() {
		CouchDbProperties properties = new CouchDbProperties()
				.setDbName("test-base-target").setCreateDbIfNotExist(false)
				.setProtocol("http").setHost("localhost").setPort(5984)
				.setMaxConnections(100).setConnectionTimeout(0);
		return new CouchDbClient(properties);
	}

}
