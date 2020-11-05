package no.kristiania.http;

//import databases
import no.kristiania.Project.Member;
import no.kristiania.Project.MemberDao;
import no.kristiania.Project.Task;
import no.kristiania.Project.TaskDao;

import no.kristiania.controllers.*;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

// NOTE: RED FIELDS(ERRORS) ARE LINKED TO A DATABASE, BUT WE DO NOT HAVE A DATABASE YET!

public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private Map<String, HttpController> controllers;
    private int port;
    private MemberDao memberDao;

    public HttpServer(int port, DataSource dataSource) throws IOException {
        this.port = port;
        memberDao = new MemberDao(dataSource);
        TaskDao taskDao = new TaskDao(dataSource);
        controllers = Map.of(
                "/newTask", new ProjectTaskPostController(taskDao),
                "/newTasks", new ProjectTaskGetController(taskDao),
                "/taskOptions", new ProjectTaskOptionsController(taskDao),
                "/memberOptions", new MemberOptionsController(memberDao),
                "/updateTask", new UpdateProjectController(memberDao),
                "/editTask", new UpdateTaskController(taskDao),
                "/projectwithmembers", new showListedmembersController(memberDao, taskDao)
        );

        ServerSocket serverSocket = new ServerSocket(port);
        logger.info("Server started on port {}", serverSocket.getLocalPort());

        new Thread(() -> {
            while (true) {
                try(Socket clientSocket = serverSocket.accept()) {
                     handleRequest(clientSocket);
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public int getPort() {

        return port;
    }

    private void handleRequest(Socket clientSocket) throws IOException, SQLException {
        HttpMessage request = new HttpMessage(clientSocket);
        String requestLine = request.getStartLine();
        System.out.println("REQUEST " + requestLine);

        String requestMethod = requestLine.split(" ")[0];
        String requestTarget = requestLine.split(" ")[1];
        String statusCode = "200";
        String body = "<a href=\"index.html\">Return to front page</a>";

        int questionPos = requestTarget.indexOf('?');
        String requestPath = questionPos != -1 ? requestTarget.substring(0, questionPos) : requestTarget;

        if (requestMethod.equals("POST")) {
            if (requestPath.equals("/members")) {
                handlePostMembers(clientSocket, request, body);
            } else {
                getController(requestPath).handle(request, clientSocket);
            }
        } else {
            if (requestPath.equals("/echo")) {
                handleEchoRequest(clientSocket, requestTarget, questionPos);
            } else if (requestPath.equals("/members")) {
                handleGetMembers(clientSocket);
            } else {
                HttpController controller = controllers.get(requestPath);
                if (controller != null) {
                    controller.handle(request, clientSocket);
                } else {
                    handleFileRequest(clientSocket, requestPath);
                }
            }
        }
    }

    private  HttpController getController(String requestPath) {

        return controllers.get(requestPath);
    }

    private void handlePostMembers(Socket clientSocket, HttpMessage request, String body) throws SQLException, IOException {
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
    private void handleFileRequest(Socket clientSocket, String requestPath) throws IOException {

        try (InputStream inputStream = getClass().getResourceAsStream(requestPath)) {
            if (inputStream == null) {
                String body = requestPath + " does not exist";

                String response = "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: " + body.length() + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n" +
                        body;

                clientSocket.getOutputStream().write(response.getBytes());
                return;
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            inputStream.transferTo(buffer);

            String contentType = "text/plain";
            if (requestPath.endsWith(".html")) {
                contentType = "text/html";
            }
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + buffer.toByteArray().length + "\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";
            clientSocket.getOutputStream().write(response.getBytes());
            clientSocket.getOutputStream().write(buffer.toByteArray());
        }

    }
    private void handleGetMembers (Socket clientSocket) throws IOException, SQLException {
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
    private void handleEchoRequest (Socket clientSocket, String requestTarget, int questionPos) throws IOException {
        String statusCode = "200";
        String body = "Hello <strong>World</strong>!";
        if (questionPos != -1) {
            QueryString queryString = new QueryString(requestTarget.substring(questionPos + 1));
            if (queryString.getParameter("status") != null) {
                statusCode = queryString.getParameter("status");
            }
            if (queryString.getParameter("body") != null) {
                body = queryString.getParameter("body");
            }
        }
        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                body;
        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader("pgr203.properties")) {
            properties.load(fileReader);
        }
        PGSimpleDataSource dataSource = new PGSimpleDataSource();

        dataSource.setUrl(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));
        Flyway.configure()
                .dataSource(dataSource)
                .load()
                .migrate();

        HttpServer server = new HttpServer(8080, dataSource);
        logger.info("Using database {}", dataSource.getURL());
        logger.info("Started on http://localhost:{}/index.html", 8080);
    }


    public List<Member> getMembers() throws SQLException{
        return memberDao.list();
    }

}