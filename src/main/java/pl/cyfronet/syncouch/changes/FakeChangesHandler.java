package pl.cyfronet.syncouch.changes;

import org.lightcouch.CouchDbClient;

import com.google.gson.JsonObject;

public class FakeChangesHandler extends JoltChangeHandler implements DocChangeHandler {

	public FakeChangesHandler(CouchDbClient targetDbClient) {
		super(targetDbClient);
	}

	@Override
	public void handleDocChange(String docId, JsonObject doc) throws DocChangesHandlerException {
		String fakeDocFailureId = "59d739379836c00cf4f845a921008850";
		if (docId.equals(fakeDocFailureId)) {
			throw new DocChangesHandlerException("Fake error on doc id '" + docId + "'");
		}
		super.handleDocChange(docId, doc);
	}


}
