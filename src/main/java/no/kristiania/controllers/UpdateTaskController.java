package no.kristiania.controllers;

import no.kristiania.Project.Member;
import no.kristiania.Project.Task;
import no.kristiania.Project.TaskDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateTaskController implements HttpController {

    private final TaskDao taskDao;

    public UpdateTaskController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = handle(request);
        response.write(clientSocket);
    }

    public HttpMessage handle(HttpMessage request) throws SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        Integer taskId = Integer.valueOf(requestParameter.getParameter("taskId"));
        String status = requestParameter.getParameter("taskStatus");
        Task task = taskDao.retrieve(taskId);
        task.setStatus(status);

        taskDao.update(task);

        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/index.html");
        return redirect;
    }
}
