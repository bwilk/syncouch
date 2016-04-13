package pl.cyfronet.syncouch.playground;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.NoDocumentException;
import org.lightcouch.Response;

import com.google.gson.JsonObject;

public class CouchClient {

	public static CouchDbClient dbClient() {
		CouchDbProperties properties = new CouchDbProperties()
				.setDbName("test-base-source").setCreateDbIfNotExist(true)
				.setProtocol("http").setHost("localhost").setPort(5984)
				.setMaxConnections(100).setConnectionTimeout(0);

		CouchDbClient dbClient = new CouchDbClient(properties);
		return dbClient;
	}

	public static void main(String[] args) {

		Foo foo = new Foo(); // Plain Java Object

		//Response response = dbClient().save(foo);
		try {
			JsonObject json = dbClient().find(JsonObject.class, "2222");	
			System.out.println(json);
		} catch (NoDocumentException e) {
			
		}

		
	}
}
