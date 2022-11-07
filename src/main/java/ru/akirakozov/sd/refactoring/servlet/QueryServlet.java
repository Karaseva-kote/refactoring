package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.ProductDao;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String command = request.getParameter("command");

		if ("max".equals(command)) {
			try {
				Map.Entry<String, Integer> result = ProductDao.findMax();

				response.getWriter().println("<html><body>");
				response.getWriter().println("<h1>Product with max price: </h1>");

				if (result != null) {
					String name = result.getKey();
					int price = result.getValue();
					response.getWriter().println(name + "\t" + price + "</br>");
				}

				response.getWriter().println("</body></html>");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if ("min".equals(command)) {
			try {
				Map.Entry<String, Integer> result = ProductDao.findMin();
				response.getWriter().println("<html><body>");
				response.getWriter().println("<h1>Product with min price: </h1>");

				if (result != null) {
					String name = result.getKey();
					int price = result.getValue();
					response.getWriter().println(name + "\t" + price + "</br>");
				}

				response.getWriter().println("</body></html>");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if ("sum".equals(command)) {
			try {
				int sum = ProductDao.findSum();
				response.getWriter().println("<html><body>");
				response.getWriter().println("Summary price: ");

				response.getWriter().println(sum);

				response.getWriter().println("</body></html>");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if ("count".equals(command)) {
			try {
				int count = ProductDao.findCount();
				response.getWriter().println("<html><body>");
				response.getWriter().println("Number of products: ");

				response.getWriter().println(count);

				response.getWriter().println("</body></html>");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			response.getWriter().println("Unknown command: " + command);
		}

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
	}

}
