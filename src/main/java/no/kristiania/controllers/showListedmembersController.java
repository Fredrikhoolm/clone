package no.kristiania.controllers;


import no.kristiania.Project.Member;
import no.kristiania.Project.MemberDao;
import no.kristiania.Project.Task;
import no.kristiania.Project.TaskDao;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class showListedmembersController implements HttpController {
    private MemberDao memberDao;
    private TaskDao taskDao;

    public showListedmembersController(MemberDao memberDao, TaskDao taskDao) {
        this.memberDao = memberDao;
        this.taskDao = taskDao;
    }


    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {

        String body = "<ul>";

        Member member = new Member();
        Task task = new Task();
        String name = member.getFirstName();
        String taskName = task.getName();
        String status = task.getStatus();
            body += "<li>" + name + "</li>" +  "<dl>" + "ProjectName: " + taskName + "Status:" + status +  "</dl>";


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
