package no.kristiania.http;

import no.kristiania.Project.TaskDao;
import no.kristiania.Project.Task;

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
    /*public String getBody() throws SQLException {
        String body = "";
        for (ProductCategory category : categoryDao.list()) {
            body += "<option value=" + category.getId() + ">" + category.getName() + "</option>";
        }
        return body;
    }*/
}
