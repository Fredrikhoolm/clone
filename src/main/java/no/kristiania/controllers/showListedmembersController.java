package no.kristiania.controllers;

import no.kristiania.Project.Member;
import no.kristiania.Project.MemberDao;
import no.kristiania.Project.Task;
import no.kristiania.Project.TaskDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.controllers.MemberOptionsController;
import no.kristiania.controllers.ProjectTaskOptionsController;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class showListedmembersController implements HttpController{
    private MemberDao memberDao;
    private TaskDao taskDao;


    public showListedmembersController(MemberDao memberDao, TaskDao taskDao) {
        this.memberDao = memberDao;
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }
        public String getBody() throws SQLException {
            String body = "<ul>";
            for (Member member : memberDao.list()) {
                for (Task task : taskDao.list()) {
                    String Membername = member.getFirstName();
                    String taskName = task.getName();
                    String taskStatus = task.getStatus();
                    body += "<li>" + Membername + "</li>" + "<li>" + taskName + "</li>" + "<dl>" + "Status:" + taskStatus + "</dl>";
                }
                // body += "<li>" + Membername + "</li>" + "<li>" + taskName + "</li>" +  "<dl>" + "Status:" + taskStatus + "</dl>";
                body += "</ul>";

            }
            return body;
        }

    }

