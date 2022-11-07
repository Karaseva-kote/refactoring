package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.HttpWriter;
import ru.akirakozov.sd.refactoring.ProductDao;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			List<Map.Entry<String, Integer>> result = ProductDao.getProducts();
			HttpWriter.doGetProductResponse(response.getWriter(), result);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
