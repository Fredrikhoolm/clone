package no.kristiania.controllers;

import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public interface HttpController {
    void handle(HttpMessage request, Socket clientSocket, String requestTarget, int questionPos) throws IOException, SQLException;

}
