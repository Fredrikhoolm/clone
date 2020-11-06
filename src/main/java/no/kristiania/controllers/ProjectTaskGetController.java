package no.kristiania.controllers;

import no.kristiania.Project.Task;
import no.kristiania.Project.TaskDao;

import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectTaskGetController implements HttpController {
    private TaskDao taskDao;

    public ProjectTaskGetController(TaskDao taskDao){

        this.taskDao = taskDao;
    }
    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        String body = "<ul>";
        for (Task task : taskDao.list()) {
            String name = task.getName();
            String status = task.getStatus();
            body += "<li>" + name + "</li>" +  "<dl>" + "Status: " + status + "</dl>";
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
