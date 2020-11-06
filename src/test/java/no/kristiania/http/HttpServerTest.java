package no.kristiania.http;

//Import from database project folder

import no.kristiania.Project.Member;
import no.kristiania.Project.Task;
import no.kristiania.Project.MemberDao;
import no.kristiania.Project.TaskDao;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Date;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpServerTest {

    private JdbcDataSource dataSource;
   // private HttpServer server;

    @BeforeEach
    void setUp() throws IOException {
        dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
        //server = new HttpServer(0, dataSource);
    }

    @Test
    void shouldReturnSuccessfulStatusCode() throws IOException {
        HttpServer server = new HttpServer(10001, dataSource);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnUnsuccessfulStatusCode() throws IOException {
        HttpServer server = new HttpServer(10002, dataSource);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?status=404");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReturnContentLength() throws IOException {
        HttpServer server = new HttpServer(10003, dataSource);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?body=HelloWorld");
        assertEquals("10", client.getResponseHeader("Content-Length"));
    }

    @Test
    void shouldReturnResponseBody() throws IOException {
        HttpServer server = new HttpServer(10004, dataSource);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?body=HelloWorld");
        assertEquals("HelloWorld", client.getResponseBody());
    }

    @Test
    void shouldReturnFileFromDisk() throws IOException {
        HttpServer server = new HttpServer(10005, dataSource);
        File contentRoot = new File("target/test-classes");
        String fileContent = "Hello World " + new Date();
        Files.writeString(new File(contentRoot, "test.txt").toPath(), fileContent);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/test.txt");
        assertEquals(fileContent, client.getResponseBody());
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReturnCorrectContentType() throws IOException {
        HttpServer server = new HttpServer(10006, dataSource);
        File contentRoot = new File("target/test-classes");
        Files.writeString(new File(contentRoot, "index.html").toPath(), "<h2>Hello World</h2>");
        HttpClient client = new HttpClient("localhost", server.getPort(), "/index.html");
        assertEquals("text/html", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReturn404IfFileNotFound() throws IOException {
        HttpServer server = new HttpServer(10007, dataSource);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/notFound.txt");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldPostNewMember() throws IOException, SQLException {
        HttpServer server = new HttpServer(10008, dataSource);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/members", "POST", "first_name=Christian");
        assertEquals(200, client.getStatusCode());
        assertThat(server.getMembers())
                .extracting(member -> member.getFirstName())
                .contains("Christian");
    }
    @Test
    void shouldReturnExistingMembers() throws IOException, SQLException {
        HttpServer server = new HttpServer(10009, dataSource);
        MemberDao memberDao = new MemberDao(dataSource);
        Member member = new Member();
        member.setFirstName("Christian");
        member.setLastName("Lie");
        member.setEmail("chris@egms.no");
        memberDao.insert(member);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/members");
        assertThat(client.getResponseBody()).contains("<li>" + "Christian Lie, chris@egms.no</li>");
    }
    @Test
    void shouldFilterMembersByTask() throws IOException, SQLException{
        HttpServer server = new HttpServer(10010, dataSource);
        MemberDao memberDao = new MemberDao(dataSource);
        Member member = new Member();
        member.setFirstName("Christian");
        member.setLastName("Lie");
        member.setEmail("chris@egms.no");
        memberDao.insert(member);

        Member testStudent = new Member();
        testStudent.setFirstName("testStudent");
        testStudent.setLastName("kristiania");
        testStudent.setEmail("egms@kristiania.no");
        memberDao.insert(testStudent);

        TaskDao taskDao = new TaskDao(dataSource);
        Task done = new Task();
        done.setName("done");
        taskDao.insert(done);

        testStudent.setTaskId(done.getId());
        memberDao.update(testStudent);

        HttpClient client = new HttpClient("localhost", server.getPort(), "/members?taskId=" + done.getId());
        assertThat(client.getResponseBody()).contains("<li>testStudent kristiania, egms@kristiania.no</li>");
    }

    @Test
    void shouldPostNewProject() throws IOException, SQLException {
        HttpServer server = new HttpServer(10011, dataSource);
       // String requestBody = "taskName= JAVA";
        HttpClient postClient = new HttpClient("localhost", server.getPort(), "/newTask", "POST", "taskName=JAVA&taskStatus=DONE");
        assertEquals(200, postClient.getStatusCode());
        HttpClient getClient = new HttpClient("localhost", server.getPort(), "/newTasks");
        assertThat(getClient.getResponseBody()).contains("<li>" + "JAVA" + "</li>" +  "<dl>" + "Status:" + "DONE</dl>");
    }

}
