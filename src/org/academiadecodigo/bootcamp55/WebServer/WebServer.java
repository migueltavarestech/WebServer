package org.academiadecodigo.bootcamp55.WebServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {

    private static final int PORT_NUMBER = 8000;

    public static void main(String[] args) {
        WebServer webServer = new WebServer();
        webServer.init();
    }

    private void init() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
            System.out.println("Now Listening on port " + PORT_NUMBER);
            ExecutorService fixedPool = Executors.newFixedThreadPool(4);
            while(serverSocket.isBound()) {
                Socket clientSocket = serverSocket.accept();
                Connection connection = new Connection(clientSocket, serverSocket);
                fixedPool.submit(connection);
            }
        } catch (IOException ex) {
            System.out.println("IO Exception starting up server");
        }
    }

    private class Connection implements Runnable {

        private Socket clientSocket;
        private ServerSocket serverSocket;

        private Connection(Socket clientSocket, ServerSocket serverSocket){
            this.clientSocket = clientSocket;
            this.serverSocket = serverSocket;
        }

        private void handleRequests () {
            try {
                while (clientSocket.isConnected()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());

                    String line = in.readLine();
                    String[] splitLine = line.split(" ");
                    String request = splitLine[1];

                    sendHTML(dataOut, request);

                    in.close();
                    dataOut.close();
                    clientSocket = serverSocket.accept();
                }
            } catch (IOException ex) {
                System.out.println("IO Exception handling request");
            }
        }

        private void sendHTML (DataOutputStream dataOut, String request) {
            try {
                if (request.equals("/")) {
                    Path indexPath = Paths.get("www/index.html");
                    byte[] indexFile = Files.readAllBytes(indexPath);

                    String header = "HTTP/1.0 200 Document Follows\r\nContent-Type: text/html; charset=UTF-8\r\n Content-Length: " + indexFile.length + " \r\n\r\n";
                    dataOut.writeBytes(header);
                    dataOut.write(indexFile);
                } else {
                    String fileString = "www" + request;
                    Path filePath = Paths.get(fileString);
                    byte[] file = Files.readAllBytes(filePath);

                    String header = "HTTP/1.0 200 Document Follows\r\nContent-Type: text/html; charset=UTF-8\r\n Content-Length: " + file.length + " \r\n\r\n";
                    dataOut.writeBytes(header);
                    dataOut.write(file, 0, file.length);
                }
            } catch (IOException ex) {
                try {
                    Path notFoundPath = Paths.get("www/404.html");
                    byte[] notFoundFile = Files.readAllBytes(notFoundPath);
                    // change to buffer of 1024, in FileInputStream, numBytes, while loop

                    String header = "HTTP/1.0 404 Not Found\r\nContent-Type: text/html; charset=UTF-8\r\n Content-Length: " + notFoundFile.length + " \r\n\r\n";
                    dataOut.writeBytes(header);
                    dataOut.write(notFoundFile, 0, notFoundFile.length);
                } catch (IOException ex2) {
                    System.out.println("IOException handling 404 page");
                }
            }
        }

        @Override
        public void run() {
            handleRequests();
        }
    }
}
