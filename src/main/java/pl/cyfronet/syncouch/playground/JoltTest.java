package pl.cyfronet.syncouch.playground;

import java.io.IOException;
import java.util.List;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;

public class JoltTest {

	public static void main(String[] args) throws IOException {

		List chainrSpecJSON = JsonUtils
				.classpathToList("/jolt-schemas/sample/spec.json");

		Chainr chainr = Chainr.fromSpec(chainrSpecJSON);

		Object inputJSON = JsonUtils
				.classpathToMap("/jolt-schemas/sample/input.json");

		CouchDbClient dbClient = CouchClient.dbClient();
		dbClient.save(inputJSON);
	
		Object transformedOutput = chainr.transform(inputJSON);
		System.out.println(JsonUtils.toJsonString(transformedOutput));

		dbClient.save(transformedOutput);

	}
}