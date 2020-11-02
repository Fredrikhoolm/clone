package no.kristiania.http;

import no.kristiania.Project.MemberDao;
import no.kristiania.Project.Member;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;


public class MemberOptionsController implements HttpController {
    private MemberDao memberDao;

    public MemberOptionsController(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
       /* HttpMessage httpMessage = new HttpMessage(getBody());
        response.write(clientSocket);*/
        String body = getBody();
        String boy = "<a href=\"index.html\">Return to front page</a>";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Connection: close\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" +
                body;
        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }

    public String getBody() throws SQLException {
        String body = "";
        for (Member member : memberDao.list()) {
            body += "<option value=" + member.getId() + ">" + member. getFirstName() + "</option";
        }

        return body;
    }
}
