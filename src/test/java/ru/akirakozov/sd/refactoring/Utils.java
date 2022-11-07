package ru.akirakozov.sd.refactoring;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class Utils {
	private static final int port = 8082;
	private static final String requestPrefix = String.format("http://localhost:%d/", port);

	static void prepareDB(String ... sql) throws Exception {
		ProductDao.createDB();
		ProductDao.clearDB();
		for (String s : sql) {
			ProductDao.executeQuery(s);
		}
	}

	static void doTest(
			ServletContextHandler context,
			List<String> requests,
			List<String> expectedResponse,
			List<Map.Entry<String, Integer>> expectedDBStatement) throws Exception {
		Server server = new Server(port);
		server.setHandler(context);
		server.start();

		try {
			for (int i = 0; i < requests.size(); i++) {
				assertEquals(expectedResponse.get(i), getResponse(requests.get(i)));
			}
			checkDB(expectedDBStatement);
		} finally {
			server.stop();
		}
	}

	private static String getResponse(String request) throws Exception {
		URL url = new URL(requestPrefix + request);
		return new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8);
	}

	private static void checkDB(List<Map.Entry<String, Integer>> expectedDBStatement) throws Exception {
		List<Map.Entry<String, Integer>> actualDBStatement = ProductDao.getProducts();
		assertThat(actualDBStatement, is(expectedDBStatement));
	}
}
