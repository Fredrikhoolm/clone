package no.kristiania.controllers;

import no.kristiania.Project.Status;
import no.kristiania.Project.StatusDao;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectStatusOptionsController implements HttpController{
    private StatusDao statusDao;

    public ProjectStatusOptionsController(StatusDao statusDao) {

        this.statusDao = statusDao;
    }
    @Override
    public void handle(HttpMessage request, Socket clientSocket, String requestTarget, int questionPos) throws IOException, SQLException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }
    public String getBody() throws SQLException {
        String body = "";
        for (Status status : statusDao.list()) {
            body += "<option value=" + status.getId() + ">" + status.getName() +  "</option>";
        }
        return body;
    }
}
