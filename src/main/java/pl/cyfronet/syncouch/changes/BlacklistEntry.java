package pl.cyfronet.syncouch.changes;

public class BlacklistEntry {

	String docId; 
	Integer firstSeq; 
	Integer lastSeq;
	
	public BlacklistEntry(String docId, Integer handleSince, Integer lastChange) {
		this.docId = docId;
		this.firstSeq = handleSince;
		this.lastSeq = lastChange;
	} 
	
}
