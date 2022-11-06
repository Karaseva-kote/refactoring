package ru.akirakozov.sd.refactoring;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class Utils {
	private static final Logger log = Logger.getLogger(Utils.class);
	private static final int port = 8082;
	private static final String requestPrefix = String.format("http://localhost:%d/", port);
	private static final String dbUrl = "jdbc:sqlite:test.db";

	static void doActionDB(ThrowingConsumer<Statement> consumer) throws Exception {
		try (Connection c = DriverManager.getConnection(dbUrl)) {
			try (Statement statement = c.createStatement()) {
				consumer.accept(statement);
			}
		}
	}

	static void prepareDB(String ... sql) throws Exception {
		doActionDB(statement -> statement.executeUpdate(
				"CREATE TABLE IF NOT EXISTS PRODUCT" +
						"(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
						" NAME           TEXT    NOT NULL, " +
						" PRICE          INT     NOT NULL)"));
		doActionDB(statement -> statement.executeUpdate("DELETE FROM PRODUCT"));
		for (String s : sql) {
			doActionDB(statement -> statement.execute(s));
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
		doActionDB(statement -> {
			ResultSet result = statement.executeQuery("SELECT Name, Price FROM PRODUCT");
			List<Map.Entry<String, Integer>> actualDBStatement = getMapFromResultSet(result);
			assertThat(actualDBStatement, is(expectedDBStatement));
		});
	}

	private static List<Map.Entry<String, Integer>> getMapFromResultSet(ResultSet result) throws SQLException {
		List<Map.Entry<String, Integer>> list = new ArrayList<>();
		while (result.next()) {
			String name = result.getString("Name");
			Integer price = result.getInt("Price");
			list.add(Map.entry(name, price));
		}
		return list;
	}

	@FunctionalInterface
	public interface ThrowingConsumer<T> extends Consumer<T> {

		@Override
		default void accept(final T elem) {
			try {
				acceptThrows(elem);
			} catch (final Exception e) {
				log.error("Exception (" + e + ") -> RuntimeException");
				throw new RuntimeException(e);
			}
		}

		void acceptThrows(T elem) throws Exception;
	}
}
