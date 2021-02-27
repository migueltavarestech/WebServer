package org.academiadecodigo.bootcamp55.WebServer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ServerCrash {

    public static void main(String[] args) throws IOException {
        ServerCrash serverCrash = new ServerCrash();

        for(int i=0; i<5000; i++) {
            serverCrash.startConnection();
        }
    }

    private void startConnection() {
        try {
            URL localhost = new URL("http://localhost:8000/");
            URLConnection connection = localhost.openConnection();
            connection.connect();
            System.out.println("New connection made");
        } catch (MalformedURLException ex) {
            System.out.println("MalformedURLException");
        } catch (IOException ex) {
            System.out.println("IO Exception");
        }
    }
}
