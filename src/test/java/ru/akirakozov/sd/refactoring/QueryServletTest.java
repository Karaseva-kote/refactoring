package ru.akirakozov.sd.refactoring;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class QueryServletTest {
	private static final ServletContextHandler context = getContext();

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static ServletContextHandler getContext() {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.addServlet(new ServletHolder(new QueryServlet()),"/query");
		return context;
	}

	@Test
	public void queryMaxOfEmptyDB() throws Exception {
		Utils.prepareDB();

		List<String> requests = List.of("query?command=max");
		List<String> response = List.of(
				"<html><body>\r\n" +
				"<h1>Product with max price: </h1>\r\n" +
				"</body></html>\r\n"
		);
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of();

		Utils.doTest(context, requests, response, expectedDBStatement);
	}

	@Test
	public void queryMax() throws Exception {
		Utils.prepareDB("INSERT INTO PRODUCT (Name, Price) " +
				"VALUES (\"iphone13\", 50590)," +
				"(\"iphone12\", 37890)," +
				"(\"iphone14\", 58490);");


		List<String> requests = List.of("query?command=max");
		List<String> response = List.of(
				"<html><body>\r\n" +
				"<h1>Product with max price: </h1>\r\n" +
				"iphone14\t58490</br>\r\n" +
				"</body></html>\r\n"
		);
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of(
				Map.entry("iphone13", 50590),
				Map.entry("iphone12", 37890),
				Map.entry("iphone14", 58490)
		);

		Utils.doTest(context, requests, response, expectedDBStatement);
	}

	@Test
	public void queryMinOfEmptyDB() throws Exception {
		Utils.prepareDB();

		List<String> requests = List.of("query?command=min");
		List<String> response = List.of(
				"<html><body>\r\n" +
				"<h1>Product with min price: </h1>\r\n" +
				"</body></html>\r\n"
		);
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of();

		Utils.doTest(context, requests, response, expectedDBStatement);
	}

	@Test
	public void queryMin() throws Exception {
		Utils.prepareDB("INSERT INTO PRODUCT (Name, Price) " +
				"VALUES (\"iphone13\", 50590)," +
				"(\"iphone12\", 37890)," +
				"(\"iphone14\", 58490);");


		List<String> requests = List.of("query?command=min");
		List<String> response = List.of(
				"<html><body>\r\n" +
				"<h1>Product with min price: </h1>\r\n" +
				"iphone12\t37890</br>\r\n" +
				"</body></html>\r\n"
		);
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of(
				Map.entry("iphone13", 50590),
				Map.entry("iphone12", 37890),
				Map.entry("iphone14", 58490)
		);

		Utils.doTest(context, requests, response, expectedDBStatement);
	}

	@Test
	public void querySumOfEmptyDB() throws Exception {
		Utils.prepareDB();

		List<String> requests = List.of("query?command=sum");
		List<String> response = List.of(
				"<html><body>\r\n" +
				"Summary price: \r\n" +
				"0\r\n" +
				"</body></html>\r\n"
		);
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of();

		Utils.doTest(context, requests, response, expectedDBStatement);
	}

	@Test
	public void querySum() throws Exception {
		Utils.prepareDB("INSERT INTO PRODUCT (Name, Price) " +
				"VALUES (\"iphone13\", 50590)," +
				"(\"iphone12\", 37890)," +
				"(\"iphone14\", 58490);");


		List<String> requests = List.of("query?command=sum");
		List<String> response = List.of(
				"<html><body>\r\n" +
				"Summary price: \r\n" +
				"146970\r\n" +
				"</body></html>\r\n"
		);
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of(
				Map.entry("iphone13", 50590),
				Map.entry("iphone12", 37890),
				Map.entry("iphone14", 58490)
		);

		Utils.doTest(context, requests, response, expectedDBStatement);
	}

	@Test
	public void queryCountOfEmptyDB() throws Exception {
		Utils.prepareDB();

		List<String> requests = List.of("query?command=count");
		List<String> response = List.of(
				"<html><body>\r\n" +
				"Number of products: \r\n" +
				"0\r\n" +
				"</body></html>\r\n"
		);
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of();

		Utils.doTest(context, requests, response, expectedDBStatement);
	}

	@Test
	public void queryCount() throws Exception {
		Utils.prepareDB("INSERT INTO PRODUCT (Name, Price) " +
				"VALUES (\"iphone13\", 50590)," +
				"(\"iphone13\", 50590)," +
				"(\"iphone14\", 58490);");


		List<String> requests = List.of("query?command=count");
		List<String> response = List.of(
				"<html><body>\r\n" +
				"Number of products: \r\n" +
				"3\r\n" +
				"</body></html>\r\n"
		);
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of(
				Map.entry("iphone13", 50590),
				Map.entry("iphone13", 50590),
				Map.entry("iphone14", 58490)
		);

		Utils.doTest(context, requests, response, expectedDBStatement);
	}

	@Test
	public void unknownCommand() throws Exception {
		Utils.prepareDB();

		List<String> requests = List.of(
				"query?command=filter",
				"query?count",
				"query?comand=count"
		);
		List<String> response = List.of(
				"Unknown command: filter\r\n",
				"Unknown command: null\r\n",
				"Unknown command: null\r\n"
		);
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of();

		Utils.doTest(context, requests, response, expectedDBStatement);
	}

	@Test
	public void badRequest() throws Exception {
		Utils.prepareDB();

		List<String> requests = List.of("qeury?command=count");
		List<String> response = List.of("");
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of();

		exception.expect(IOException.class);
		Utils.doTest(context, requests, response, expectedDBStatement);
	}
}
