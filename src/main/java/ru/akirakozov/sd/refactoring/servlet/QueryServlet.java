package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.HttpWriter;
import ru.akirakozov.sd.refactoring.ProductDao;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {
	enum Command {
		max, min, sum, count, unknown
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String parameterCommand = request.getParameter("command");
		Command command = Command.unknown;

		if (Arrays.stream(Command.values()).anyMatch(c -> c.toString().equals(parameterCommand))){
			command = Command.valueOf(parameterCommand);
		}

		try {
			switch (command) {
				case max -> HttpWriter.doMaxProductResponse(response.getWriter(), ProductDao.findMax());
				case min -> HttpWriter.doMinProductResponse(response.getWriter(), ProductDao.findMin());
				case sum -> HttpWriter.doSumProductResponse(response.getWriter(), ProductDao.findSum());
				case count -> HttpWriter.doCountProductResponse(response.getWriter(), ProductDao.findCount());
				case unknown -> response.getWriter().println("Unknown command: " + parameterCommand);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
