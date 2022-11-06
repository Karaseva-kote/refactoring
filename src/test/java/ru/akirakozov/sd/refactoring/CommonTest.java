package ru.akirakozov.sd.refactoring;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;

import java.util.List;
import java.util.Map;

public class CommonTest {
	private static final ServletContextHandler context = getContext();

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static ServletContextHandler getContext() {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.addServlet(new ServletHolder(new AddProductServlet()), "/add-product");
		context.addServlet(new ServletHolder(new GetProductsServlet()),"/get-products");
		context.addServlet(new ServletHolder(new QueryServlet()),"/query");
		return context;
	}

	@Test
	public void commonTest() throws Exception {
		Utils.prepareDB();

		List<String> requests = List.of(
				"get-products",
				"add-product?name=iphone13&price=50590",
				"add-product?name=iphone14&price=58490",
				"get-products",
				"query?command=max",
				"add-product?name=iphone12&price=37890",
				"query?command=min",
				"query?command=sum",
				"query?command=count"
		);
		List<String> response = List.of(
				"<html><body>\r\n" +
						"</body></html>\r\n",
				"OK\r\n",
				"OK\r\n",
				"<html><body>\r\n" +
						"iphone13\t50590</br>\r\n" +
						"iphone14\t58490</br>\r\n" +
						"</body></html>\r\n",
				"<html><body>\r\n" +
						"<h1>Product with max price: </h1>\r\n" +
						"iphone14\t58490</br>\r\n" +
						"</body></html>\r\n",
				"OK\r\n",
				"<html><body>\r\n" +
						"<h1>Product with min price: </h1>\r\n" +
						"iphone12\t37890</br>\r\n" +
						"</body></html>\r\n",
				"<html><body>\r\n" +
						"Summary price: \r\n" +
						"146970\r\n" +
						"</body></html>\r\n",
				"<html><body>\r\n" +
						"Number of products: \r\n" +
						"3\r\n" +
						"</body></html>\r\n"
		);
		List<Map.Entry<String, Integer>> expectedDBStatement = List.of(
				Map.entry("iphone13", 50590),
				Map.entry("iphone14", 58490),
				Map.entry("iphone12", 37890)
		);

		Utils.doTest(context, requests, response, expectedDBStatement);
	}
}
