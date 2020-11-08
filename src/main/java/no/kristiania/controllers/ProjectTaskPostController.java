package no.kristiania.controllers;

import no.kristiania.Project.*;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectTaskPostController implements HttpController {
    private TaskDao taskDao;

    public ProjectTaskPostController(TaskDao taskDao){

        this.taskDao = taskDao;
    }


    @Override
    public void handle(HttpMessage request, Socket clientSocket, String requestTarget, int questionPos) throws IOException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        Task tasks = new Task();
        tasks.setName(requestParameter.getParameter("taskName"));
        taskDao.insert(tasks);

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