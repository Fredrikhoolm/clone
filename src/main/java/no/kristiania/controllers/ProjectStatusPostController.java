package no.kristiania.controllers;

import no.kristiania.Project.Status;
import no.kristiania.Project.StatusDao;
import no.kristiania.Project.Task;
import no.kristiania.Project.TaskDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectStatusPostController implements HttpController {
    private StatusDao statusDao;

    public ProjectStatusPostController(StatusDao statusDao){

        this.statusDao = statusDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        Status status = new Status();
        status.setName(requestParameter.getParameter("name"));
        statusDao.insert(status);

        //String bod = "Okay";
        String body = "<a href=\"index.html\">Return to front page</a>";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Connection: close\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" +
                body;
        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }
}