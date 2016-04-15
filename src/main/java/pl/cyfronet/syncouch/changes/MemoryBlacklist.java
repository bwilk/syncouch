package pl.cyfronet.syncouch.changes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryBlacklist implements Blacklist {

	private final Logger logger = LoggerFactory.getLogger(MemoryBlacklist.class);
	
	Map<String, BlacklistEntry> map = new ConcurrentHashMap<String, BlacklistEntry>();
	
	@Override
	public boolean contains(String docId) {
		return map.containsKey(docId);
	}

	@Override
	public void update(String docId, String seq) {
		int seqInt = Integer.parseInt(seq);
		synchronized (map) {
			BlacklistEntry blacklistEntry = map.get(docId);
			if (blacklistEntry == null) {
				map.put(docId, blacklistEntry = new BlacklistEntry(docId, seqInt, seqInt));
				logger.debug("doc {} added - firstSeq, {}, lastSeq: {} ", docId, blacklistEntry.firstSeq, blacklistEntry.lastSeq);
			} else {
				blacklistEntry.lastSeq = seqInt;
				logger.debug("doc {} updated - firstSeq, {}, lastSeq: {} ",docId, blacklistEntry.firstSeq, blacklistEntry.lastSeq);
			}
		}
	}
	
	@Override
	public void docHandled(String docId) {
		synchronized (map) {
			BlacklistEntry blacklistEntry = map.get(docId);
			if (blacklistEntry != null) {
				blacklistEntry.firstSeq += 1; 
				if (blacklistEntry.firstSeq > blacklistEntry.lastSeq) {
					map.remove(docId);
					logger.debug("doc {} removed - firstSeq, {}, lastSeq: {} ",docId, blacklistEntry.firstSeq, blacklistEntry.lastSeq);
				} else {
					logger.debug("doc {} handled - firstSeq, {}, lastSeq: {} ",docId, blacklistEntry.firstSeq, blacklistEntry.lastSeq);
				}
			}
		}
	}

	@Override
	public Map<String, Integer> docsToBeHandled() {
		synchronized (map) {
			HashMap<String, Integer> toBeHandled = new HashMap<String, Integer>();
			for (BlacklistEntry entry: map.values()) {
				toBeHandled.put(entry.docId, entry.firstSeq);
			}
			return toBeHandled;
		}
	}

}
