package no.kristiania.controllers;

import no.kristiania.Project.Status;
import no.kristiania.Project.Task;
import no.kristiania.Project.TaskDao;

import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectStatusGetController implements HttpController{
    private Status;

    public ProjectStatusGetController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        String body = "<ul>";
        for (Status status : statusDao.list()) {
            String status = status.getName();
            body += "<dl>" + "Status: " + status + "</dl>";
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
