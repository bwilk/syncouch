package pl.cyfronet.syncouch.changes;

import java.util.Map;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.cyfronet.syncouch.transform.JoltTransformation;

import com.google.gson.JsonObject;

public class JoltChangeHandler implements DocChangeHandler {

	private final Logger logger = LoggerFactory
			.getLogger(JoltChangeHandler.class);

	private CouchDbClient targetDbClient;
	private JoltTransformation jolt;

	public JoltChangeHandler(CouchDbClient targetDbClient) {
		this.targetDbClient = targetDbClient;
		this.jolt = new JoltTransformation();
	}

	@Override
	public void handleDocChange(String docId, JsonObject doc) throws DocChangesHandlerException {
		try {
			Map<String, Object> transformed = jolt.transform(docId, doc);
			JsonObject targetDoc = targetDoc(docId);
			if (targetDoc != null) {
				String targetRev = targetDoc.get("_rev").getAsString();
				transformed.put("_rev", targetRev);
				targetDbClient.update(transformed);
				logger.debug("doc {} updated", docId);
			} else {
				targetDbClient.save(transformed);
				logger.debug("doc {} inserted'", docId);
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
				logger.debug("doc {} deleted'", docId);
			}
		} catch (Exception e) {
			throw new DocChangesHandlerException(e);
		}

	}

	private JsonObject targetDoc(String docId) {
		try {
			return targetDbClient.find(JsonObject.class, docId);
		} catch (NoDocumentException e) {
			return null;
		}
	}

}
