package ru.akirakozov.sd.refactoring;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GetProductsServletTest {
	private static final ServletContextHandler context = getContext();

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static ServletContextHandler getContext() {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.addServlet(new ServletHolder(new GetProductsServlet()), "/get-products");
		return context;
	}

	@Test
	public void getEmptyProductList() throws Exception {
		Utils.prepareDB();

		List<String> requests = List.of("get-products");
		List<String> response = List.of(
				"<html><body>\r\n" +
				"</body></html>\r\n"
		);
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of();

		Utils.doTest(context, requests, response, expectedDBStatement);
	}

	@Test
	public void getNonEmptyProductList() throws Exception {
		Utils.prepareDB("INSERT INTO PRODUCT (Name, Price) " +
				"VALUES (\"iphone13\", 50590)," +
				"(\"iphone12\", 37890)," +
				"(\"iphone14\", 58490);");

		List<String> requests = List.of("get-products");
		List<String> response = List.of(
				"<html><body>\r\n" +
				"iphone13\t50590</br>\r\n" +
				"iphone12\t37890</br>\r\n" +
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
	public void badRequest() throws Exception {
		Utils.prepareDB();

		List<String> requests = List.of("get-product");
		List<String> response = List.of("");
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of();

		exception.expect(IOException.class);
		Utils.doTest(context, requests, response, expectedDBStatement);
	}
}
