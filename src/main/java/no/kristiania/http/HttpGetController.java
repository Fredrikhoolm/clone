package no.kristiania.http;

/*
import no.kristiania.database.ProductCategory;
import no.kristiania.database.ProductCategoryDao;
*/

import no.kristiania.Project.Member;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class HttpGetController implements HttpController {
    private projectCategoryDao projectCategoryDao;

    public HttpGetController(projectCategoryDao projectCategoryDao){
        this.projectCategoryDao = projectCategoryDao;
    }
    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        String body = "<ul>";

        for (Member member : memberDao.list()) {

            String first_name = member.getFirstName();
            String last_Name = member.getLastName();
            String email = member.getEmail();

            body += "<li>" + first_name + " " + last_Name + ", " + email + "</li>";
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
