package pl.cyfronet.syncouch.transform;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.google.gson.JsonObject;

public class JoltTransformation {
	
	private final Logger logger = LoggerFactory.getLogger(JoltTransformation.class);
	
	private Chainr chainr;
	
	public JoltTransformation() {
		this.chainr = Chainr.fromSpec(JsonUtils
				.classpathToList("/jolt-schemas/production/spec.json"));
	}

	public Map<String, Object> transform(String docId, JsonObject doc) {
		logger.debug("doc {} as json object '{}'", docId, doc);
		
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

}
