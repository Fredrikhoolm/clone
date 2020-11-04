package no.kristiania.controllers;

import no.kristiania.Project.Member;
import no.kristiania.Project.MemberDao;
import no.kristiania.controllers.HttpController;

import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateProjectController implements HttpController {
    private final MemberDao memberDao;

    public UpdateProjectController(MemberDao memberDao) {

        this.memberDao = memberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = handle(request);
        response.write(clientSocket);
    }
    public HttpMessage handle(HttpMessage request) throws SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        Integer memberId = Integer.valueOf(requestParameter.getParameter("memberId"));
        Integer taskId = Integer.valueOf(requestParameter.getParameter("taskId"));
        Member member = memberDao.retrieve(memberId);
        member.setTaskId(taskId);

        memberDao.update(member);

        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/index.html");
        return redirect;
    }
}
