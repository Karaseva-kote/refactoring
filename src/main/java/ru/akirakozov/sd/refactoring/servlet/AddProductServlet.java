package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.HttpWriter;
import ru.akirakozov.sd.refactoring.ProductDao;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author akirakozov
 */
public class AddProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        long price = Long.parseLong(request.getParameter("price"));

        try {
            ProductDao.insertProduct(name, price);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        HttpWriter.doAddProductResponse(response.getWriter());
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
