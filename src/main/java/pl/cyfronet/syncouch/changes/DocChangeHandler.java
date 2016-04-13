package pl.cyfronet.syncouch.changes;

import java.util.Map;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.google.gson.JsonObject;

public class DocChangeHandler {

	private CouchDbClient targetDbClient;
	private Chainr chainr;  
	
	public DocChangeHandler(CouchDbClient targetDbClient) {
		this.targetDbClient = targetDbClient;
		this.chainr = Chainr.fromSpec(JsonUtils.classpathToList("/jolt-schemas/production/spec.json"));
	}

	public void handleDocChange(String docId, JsonObject doc) {
		System.out.println(">>> got doc: " + doc);
		Map<String, Object> transformed = transform(docId, doc);
		JsonObject targetDoc = targetDoc(docId);
		if (targetDoc != null) {
			String targetRev = targetDoc.get("_rev").getAsString();
			transformed.put("_rev", targetRev);
			targetDbClient.update(transformed);
		} else {
			targetDbClient.save(transformed);
		}
	}
	
	public void handleDocDelete(String docId) {
		JsonObject targetDoc = targetDoc(docId);
		String targetRev = targetDoc.get("_rev").getAsString();
		if (targetDoc != null) {
			targetDbClient.remove(docId, targetRev);
		}
	}
	
	private Map<String, Object> transform(String docId, JsonObject doc) {
		Map<String, Object> sourceMap = JsonUtils.jsonToMap(doc.toString());
		System.out.println(">>> got jsonToMap: " + sourceMap);
		Object transformed = chainr.transform(sourceMap);
		Map<String, Object> targetMap = JsonUtils.jsonToMap(JsonUtils.toJsonString(transformed));
		targetMap.remove("_rev"); // remove revision
		targetMap.remove("_id"); // remove id
		targetMap.put("_id", docId);
		System.out.println(">>> transformed doc: " + targetMap);
		return targetMap;
	}

	private JsonObject targetDoc(String docId) {
		try {
			return targetDbClient.find(JsonObject.class, docId);
		} catch (NoDocumentException e) {
			return null;
		}
	}

}
