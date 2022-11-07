package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.ProductDao;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			List<Map.Entry<String, Integer>> result = ProductDao.getProducts();

			response.getWriter().println("<html><body>");

			for (Map.Entry<String, Integer> entry : result) {
				String name = entry.getKey();
				int price = entry.getValue();
				response.getWriter().println(name + "\t" + price + "</br>");
			}

			response.getWriter().println("</body></html>");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
