package org.example;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ToDoList extends HttpServlet {
    public static final String URL = "jdbc:mysql://localhost/ToDoList";
    public static final String USER = "root";
    public static final String PASSWORD = "";
    private String taskDescription;
    private String taskCategory;
    private String periodExecution;
    private String importance;
    private int importanceInt = 0;
    private String taskName;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connect = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = connect.createStatement()) {
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
            writer.println("<form id='form' method='post' accept-charset='utf-8'>");
            writer.println("<label>Название задачи</label><br>");
            writer.println("<input type='text' name='TaskName' size='50' required><br>");
            writer.println("<label>Описание задачи</label><br>");
            writer.println("<input type='text' name='TaskDescription' size='50'><br>");
            writer.println("<label>Категория задачи</label><br>");
            writer.println("<input type='text' name='TaskCategory' size='50'><br>");
            writer.println("<label>Срок выполнения(Год-Месяц-День)</label><br>");
            writer.println("<input type='text' name='PeriodExecution' size='50'><br>");
            writer.println("<label>Важность (от 1 до 10)</label><br>");
            writer.println("<input type='text' name='Importance' size='50'><br>");
            writer.println("<p></p>");
            writer.println("<input id='submitButton' type='submit'/>");
            writer.println("</form>");
            writer.println("<p></p>");
            writer.println("<table width = '1000px'>");
            writer.println("<thead>");
            writer.println("<tr>");
            writer.println("<td>" + "Название задачи" + "</td>");
            writer.println("<td>" + "Описание задачи" + "</td>");
            writer.println("<td>" + "Категория задачи" + "</td>");
            writer.println("<td>" + "Срок выполнения" + "</td>");
            writer.println("<td>" + "Важность" + "</td>");
            writer.println("</thead");
            String buttonValue = req.getParameter("button");
            if (buttonValue != null) {
                ResultSet buttVal = stmt.executeQuery("select * from uncompletedtasks;");
                while (buttVal.next()) {
                    String value = buttVal.getString("id");
                    if (buttonValue.equals(value)) {
                        String name = buttVal.getString("TaskName");
                        String description = buttVal.getString("TaskDescription");
                        String category = buttVal.getString("TaskCategory");
                        String execution = buttVal.getString("PeriodOfExecution");
                        String importan = buttVal.getString("Importance");
                        int imp = Integer.parseInt(importan);
                        Statement stmt2 = connect.createStatement();
                        connect.setAutoCommit(false);
                        try {
                            stmt2.executeUpdate("insert into completedtasks(TaskName, TaskDescription, TaskCategory, PeriodOfExecution, Importance) value('" + name + "', '" + description + "','" + category + "', '" + execution + "', '" + imp + "');");
                            connect.commit();
                        } catch (Exception e) {
                            connect.rollback();
                        }
                        connect.setAutoCommit(true);
                        Statement stmt1 = connect.createStatement();
                        connect.setAutoCommit(false);
                        try {
                            stmt1.executeUpdate("delete from uncompletedtasks where id = '" + value + "';");
                            connect.commit();
                        } catch (Exception e) {
                            connect.rollback();
                        }
                        connect.setAutoCommit(true);
                    }
                }
            }

            taskDescription = req.getParameter("TaskDescription");
            taskCategory = req.getParameter("TaskCategory");
            periodExecution = req.getParameter("PeriodExecution");
            importance = req.getParameter("Importance");
            taskName = req.getParameter("TaskName");
            if(taskName == null){
                printTaskTable(writer, connect);
            }
            if (taskName != null) {
                if (importance != null) {
                    try {
                        importanceInt = Integer.parseInt(importance);
                    } catch (Exception e) {

                    }
                }
                connect.setAutoCommit(false);
                if (periodExecution.equals("")) {
                    periodExecution = "2020-01-01";
                }
                if(importance.equals("")){
                    importanceInt = 1;
                }
                try {
                    stmt.executeUpdate("insert into uncompletedtasks(TaskName, TaskDescription, TaskCategory, PeriodOfExecution, Importance) value('" + taskName + "', '" + taskDescription + "','" + taskCategory + "', '" + periodExecution + "', '" + importanceInt + "');");
                    connect.commit();
                    taskName = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    connect.rollback();
                }
                connect.setAutoCommit(true);
                printTaskTable(writer, connect);
            }

            writer.println("</tbody>");
            writer.println("</table>");
            writer.println("<script>");
            writer.println("let submitForm = function(e){if (e.target.id=== 'submitButton'){ document.forms['form'].submit(); console.log('Ok');}};");
            writer.println(" let clearForm = function(e){if (e.target.id=== 'submitButton'){document.forms['form'].reset(); console.log('Ok!!!');}};");
            writer.println(" let clearButton = function(e){if (e.target.id=== 'butt'){document.forms['button'].reset(); console.log('Ok!!!');}};");
            writer.println("let clearAll =  function (e){document.forms['form'].reset(); document.forms['button'].reset(); }");
            writer.println("document.body.addEventListener('click', submitForm);");
            writer.println("window.addEventListener('load', clearForm )");
            writer.println("window.addEventListener('load', clearButton )");
            writer.println("window.addEventListener('beforeunload',clearAll )");
            writer.println("</script>");
            writer.println("<a href='/"+getServletContext().getContextPath() +"done'>Выполненные задания</a>");
            writer.println(" </body>");
            writer.println("</html>");

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    public void printTaskTable(PrintWriter writer, Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        writer.println("<tbody>");
        ResultSet resultSet = stmt.executeQuery("select * from uncompletedtasks;");
        while (resultSet.next()) {
            writer.println("<tr>");
            writer.println("<td>" + resultSet.getString("TaskName") + "</td>");
            writer.println("<td>" + resultSet.getString("TaskDescription") + "</td>");
            writer.println("<td>" + resultSet.getString("TaskCategory") + "</td>");
            writer.println("<td>" + resultSet.getString("PeriodOfExecution") + "</td>");
            writer.println("<td>" + resultSet.getString("Importance") + "</td>");
            writer.println("<td>" + "<form id='button'><button id='butt' type='submit' name='button' value = '" + resultSet.getString("id") + "'>Выполнить</button></form>" + "</td>");
            writer.println("</tr>");
        }
    }
}
