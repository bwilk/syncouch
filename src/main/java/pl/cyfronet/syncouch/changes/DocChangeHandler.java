package pl.cyfronet.syncouch.changes;

import com.google.gson.JsonObject;

public interface DocChangeHandler {

	public void handleDocChange(String docId, JsonObject doc) throws DocChangesHandlerException;
	
	public void handleDocDelete(String docId) throws DocChangesHandlerException;
	
}
