package org.example;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

public class CompletedTasks extends HttpServlet {
    private AtomicInteger counter = new AtomicInteger();
    public static final String URL = "jdbc:mysql://localhost/ToDoList";
    public static final String USER = "root";
    public static final String PASSWORD = "";


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connect = DriverManager.getConnection(URL, USER, PASSWORD);) {
            connect.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            resp.setContentType("text/html");
            resp.setCharacterEncoding("utf-8");
            req.setCharacterEncoding("utf-8");
            PrintWriter writer = resp.getWriter();
            writer.println("<!DOCTYPE html>");
            writer.println("<head>");
            writer.println("<meta charset='utf-8'>");
            writer.println(" <title>ToDoList</title>");
            writer.println("<style>");
            writer.println("table { border : 1px solid black; border-collapse: collapse; text-align: center}");
            writer.println("td { border: 1px solid black;}");
            writer.println("td { height : 20px}");
            writer.println("</style>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("<p>Выполненные задания</p>");
            writer.println("<table width = '1000px'>");
            writer.println("<thead>");
            writer.println("<tr>");
            writer.println("<td>" + "Название задачи" + "</td>");
            writer.println("<td>" + "Описание задачи" + "</td>");
            writer.println("<td>" + "Категория задачи" + "</td>");
            writer.println("<td>" + "Срок выполнения" + "</td>");
            writer.println("<td>" + "Важность" + "</td>");
            writer.println("</thead>");
            Statement stmt = connect.createStatement();
            writer.println("<tbody>");
            ResultSet resultSet = stmt.executeQuery("select * from completedtasks order by id desc;");
            while (resultSet.next()) {
                writer.println("<tr>");
                writer.println("<td>" + resultSet.getString("TaskName") + "</td>");
                writer.println("<td>" + resultSet.getString("TaskDescription") + "</td>");
                writer.println("<td>" + resultSet.getString("TaskCategory") + "</td>");
                writer.println("<td>" + resultSet.getString("PeriodOfExecution") + "</td>");
                writer.println("<td>" + resultSet.getString("Importance") + "</td>");
                writer.println("</tr>");
            }
            writer.println("</tbody>");
            writer.println("</table>");
            writer.println(" </body>");
            writer.println("</html>");
        } catch (Exception e) {

        }
    }
}
