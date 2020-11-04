package no.kristiania.controllers;


import no.kristiania.Project.TaskDao;
import no.kristiania.Project.Task;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectTaskOptionsController implements HttpController{
    private TaskDao taskDao;

    public ProjectTaskOptionsController(TaskDao taskDao) {

        this.taskDao = taskDao;
    }
    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }
    public String getBody() throws SQLException {
        String body = "";
        for (Task task : taskDao.list()) {
            body += "<option value=" + task.getId() + ">" + task.getName() + task.getStatus() + "</option>";
        }
        return body;
    }
}
