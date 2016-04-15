package pl.cyfronet.syncouch.changes;

import java.util.Map;

public interface Blacklist {

	boolean contains(String docId);

	void update(String docId, String seq);

	void docHandled(String docId);

	Map<String, Integer> docsToBeHandled();

}
