package no.kristiania.http;
/*
import no.kristiania.database.ProductCategory;
import no.kristiania.database.ProductCategoryDao;
*/

import no.kristiania.Project.Member;

import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class HttpPostController implements HttpController {
   private projectCategoryDao projectCategoryDao;

   public HttpPostController(projectCategoryDao projectCategoryDao){
       this.projectCategoryDao = projectCategoryDao;
   }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        Member member = new Member();
        URLDecoder.decode(String.valueOf(member.getEmail()), StandardCharsets.UTF_8.toString());
        member.setFirstName(requestParameter.getParameter("first_name"));
        member.setLastName(requestParameter.getParameter("last_name"));
        member.setEmail(requestParameter.getParameter("email"));
        memberDao.insert(member);
        String respone = "Okay";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;
        clientSocket.getOutputStream().write(response.getBytes());
    }
}
