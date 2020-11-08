package no.kristiania.controllers;

import no.kristiania.Project.Status;
import no.kristiania.Project.StatusDao;


import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectStatusGetController implements HttpController{
    private StatusDao statusDao;

    public ProjectStatusGetController(StatusDao statusDao) {

        this.statusDao = statusDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket, String requestTarget, int questionPos) throws IOException, SQLException {
        String body = "<ul>";
        for (Status status : statusDao.list()) {
            String name = status.getName();
            body += "<dl>" + "Status:" + name + "</dl>";
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
