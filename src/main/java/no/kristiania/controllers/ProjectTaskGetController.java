package no.kristiania.controllers;

import no.kristiania.Project.Member;
import no.kristiania.Project.Task;
import no.kristiania.Project.TaskDao;

import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class ProjectTaskGetController implements HttpController {
    private TaskDao taskDao;

    public ProjectTaskGetController(TaskDao taskDao){

        this.taskDao = taskDao;
    }
    @Override
    public void handle(HttpMessage request, Socket clientSocket, String requestTarget, int questionPos) throws IOException, SQLException {
        Integer statusId = null;
        if (questionPos != -1) {
            statusId = Integer.valueOf(new QueryString(requestTarget.substring(questionPos+1))
                    .getParameter("taskStatus"));
        }
        List<Task> tasks = statusId == null ? taskDao.list() : taskDao.queryTaskByStatusId(statusId);

        String body = "<ul>";
        for (Task task : tasks) {
            String taskName = task.getName();

            body += "<li>" + taskName + "</li>";
        }

        body += "</ul>";

        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;
        clientSocket.getOutputStream().write(response.getBytes());

    }
}
