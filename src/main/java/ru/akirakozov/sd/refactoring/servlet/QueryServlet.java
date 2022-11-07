package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.HttpWriter;
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
				HttpWriter.doMaxProductResponse(response.getWriter(), result);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if ("min".equals(command)) {
			try {
				Map.Entry<String, Integer> result = ProductDao.findMin();
				HttpWriter.doMinProductResponse(response.getWriter(), result);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if ("sum".equals(command)) {
			try {
				int sum = ProductDao.findSum();
				HttpWriter.doSumProductResponse(response.getWriter(), sum);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if ("count".equals(command)) {
			try {
				int count = ProductDao.findCount();
				HttpWriter.doCountProductResponse(response.getWriter(), count);
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
