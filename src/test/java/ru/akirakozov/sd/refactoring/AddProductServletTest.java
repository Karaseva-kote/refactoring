package ru.akirakozov.sd.refactoring;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AddProductServletTest {
	private static final ServletContextHandler context = getContext();

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static ServletContextHandler getContext() {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.addServlet(new ServletHolder(new AddProductServlet()), "/add-product");
		return context;
	}

	@Test
	public void addOneProduct() throws Exception {
		Utils.prepareDB();

		List<String> requests = List.of("add-product?name=iphone13&price=50590");
		List<String> response = List.of("OK\r\n");
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of(Map.entry("iphone13", 50590));

		Utils.doTest(context, requests, response, expectedDBStatement);
	}

	@Test
	public void addMultipleProducts() throws Exception {
		Utils.prepareDB();

		List<String> requests = List.of(
				"add-product?name=iphone13&price=50590",
				"add-product?name=iphone12&price=37890",
				"add-product?name=iphone14&price=58490"
		);
		List<String> response = List.of(
				"OK\r\n",
				"OK\r\n",
				"OK\r\n"
		);
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of(
				Map.entry("iphone13", 50590),
				Map.entry("iphone12", 37890),
				Map.entry("iphone14", 58490)
		);

		Utils.doTest(context, requests, response, expectedDBStatement);
	}

	@Test
	public void addSameProducts() throws Exception {
		Utils.prepareDB();

		List<String> requests = List.of(
				"add-product?name=iphone13&price=50590",
				"add-product?name=iphone13&price=50590",
				"add-product?name=iphone13&price=00000"
		);
		List<String> response = List.of(
				"OK\r\n",
				"OK\r\n",
				"OK\r\n"
		);
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of(
				Map.entry("iphone13", 50590),
				Map.entry("iphone13", 50590),
				Map.entry("iphone13", 0)
		);

		Utils.doTest(context, requests, response, expectedDBStatement);
	}

	@Test
	public void badRequest() throws Exception {
		Utils.prepareDB();

		List<String> requests = List.of(
				"add?name=iphone13&price=50590"
		);
		List<String> response = List.of("");
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of();

		exception.expect(IOException.class);
		Utils.doTest(context, requests, response, expectedDBStatement);
	}
}
