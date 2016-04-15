package pl.cyfronet.syncouch.changes;

import java.util.Map;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.google.gson.JsonObject;

public class JoltChangeHandler implements DocChangeHandler {

	private final Logger logger = LoggerFactory
			.getLogger(JoltChangeHandler.class);

	private CouchDbClient targetDbClient;
	private Chainr chainr;

	public JoltChangeHandler(CouchDbClient targetDbClient) {
		this.targetDbClient = targetDbClient;
		this.chainr = Chainr.fromSpec(JsonUtils
				.classpathToList("/jolt-schemas/production/spec.json"));
	}

	@Override
	public void handleDocChange(String docId, JsonObject doc) throws DocChangesHandlerException {
		try {
			logger.debug("doc {} to be handled {}", docId, doc);
			Map<String, Object> transformed = transform(docId, doc);
			JsonObject targetDoc = targetDoc(docId);
			if (targetDoc != null) {
				String targetRev = targetDoc.get("_rev").getAsString();
				transformed.put("_rev", targetRev);
				targetDbClient.update(transformed);
				logger.debug("doc {} succesfully updated'", docId);
			} else {
				targetDbClient.save(transformed);
				logger.debug("doc {} succesfully inserted'", docId);
			}
		} catch (Exception e) {
			throw new DocChangesHandlerException(e);
		}
	}

	@Override
	public void handleDocDelete(String docId) throws DocChangesHandlerException {
		try {
			JsonObject targetDoc = targetDoc(docId);
			if (targetDoc != null) {
				String targetRev = targetDoc.get("_rev").getAsString();
				targetDbClient.remove(docId, targetRev);
				logger.debug("doc {} succesfully deleted'", docId);
			}
		} catch (Exception e) {
			throw new DocChangesHandlerException(e);
		}

	}

	private Map<String, Object> transform(String docId, JsonObject doc) {
		Map<String, Object> sourceMap = JsonUtils.jsonToMap(doc.toString());
		logger.debug("doc {} as map '{}'", docId, sourceMap);

		Object transformed = chainr.transform(sourceMap);
		String transformedStrRep = "{}";
		if (transformed != null) {
			transformedStrRep = JsonUtils.toJsonString(transformed);
		}

		Map<String, Object> targetMap = JsonUtils.jsonToMap(transformedStrRep);
		targetMap.remove("_rev"); // remove revision
		targetMap.remove("_id"); // remove id
		targetMap.put("_id", docId); // put original id

		logger.debug("doc {} transformed as map {}", docId, targetMap);
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
