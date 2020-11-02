package no.kristiania.controllers;

import no.kristiania.Project.MemberDao;
import no.kristiania.Project.Member;
import no.kristiania.http.HttpMessage;

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
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    public String getBody() throws SQLException {
        String body = "";
        for (Member member : memberDao.list()) {
            body += "<option value=" + member.getId() + ">" + member.getFirstName() + "</option";
        }

        return body;
    }
}
