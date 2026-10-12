package servlets;

import exceptions.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.ResultRecord;
import storages.ResultDao;
import utils.AreaChecker;
import utils.Validator;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet("/area-checker")
public class AreaCheckerServlet extends HttpServlet {

    private final ResultDao resultDao = new ResultDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            long startedAt = System.nanoTime();

            String rawX = getParam(request,"x");
            String rawY = getParam(request, "y");
            String rawR = getParam(request, "r");
            String source = getParam(request, "source");
            String timeZone = getParam(request, "timeZone");

            Validator.validate(rawX, rawY, rawR, source, timeZone);

            double x = parseDouble(rawX);
            double y = parseDouble(rawY);
            double r = parseDouble(rawR);
            boolean hit = AreaChecker.check(x, y, r);

            double executionTime = (System.nanoTime() - startedAt) / 1_000_000.0;
            String timeString = getTimeFormat(timeZone);

            ResultRecord result = new ResultRecord(
                    x, y, r, hit, timeString, executionTime
            );

            resultDao.addResult(getServletContext(), result);

            request.setAttribute("result", result);
            request.getRequestDispatcher("/result.jsp").forward(request, response);

        } catch (ValidationException e) {
            request.setAttribute("errorMsg", e.getMessage());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }


    private String getParam(HttpServletRequest request, String parameter) {
        String param = request.getParameter(parameter);
        if (param == null || param.trim().isEmpty()) {
            throw new ValidationException("Параметр " + parameter + " не найден или пуст");
        }
        return param;
    }

    private static double parseDouble(String value) {
        return Double.parseDouble(value.trim().replace(',', '.'));
    }

    private static String getTimeFormat(String timeZone) {
        ZoneId zoneId = ZoneId.of(timeZone);

        ZonedDateTime clientTime = ZonedDateTime.now(zoneId);

        return clientTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }


}
