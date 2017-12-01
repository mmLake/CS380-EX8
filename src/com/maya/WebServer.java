package com.maya;

import com.sun.security.ntlm.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    private static final int PORT = 8080;
    private static final String VALID_WEB_PATH = "www/hello.html";
    private static final String INVALID_WEB_PATH = "www/error.html";

    public static void main(String[] args) throws IOException {
        WebServer main = new WebServer();
        main.start();
    }

    private void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                // listening for connection
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Open socket");
                    BufferedReader brWebRootDir = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    OutputStreamWriter networkOutput = new OutputStreamWriter(socket.getOutputStream());


                    String address = socket.getInetAddress().getHostAddress();
                    System.out.printf("Client connected: %s%n", address);

                    brWebRootDir = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    networkOutput = new OutputStreamWriter(socket.getOutputStream());

                    //read from root web directory to direct page to hello.html or error.html
                    String line = brWebRootDir.readLine();
                    String websiteName = "www" + line.split(" ")[1];

                    if (new File(websiteName).exists()) {
                        File validFile = new File(VALID_WEB_PATH);
                        BufferedReader validFileBR = new BufferedReader(new FileReader(validFile));
                        networkOutput.write("HTTP/1.1 200 OK\n" + "Content-type: text/html\n" + "Content-length: 124\r\n\r\n");

                        String response;
                        while ((response = validFileBR.readLine()) != null) {
                            networkOutput.write(response + "\r\n");
                            System.out.println("RESPONSE: " + response);
                        }
//                    validFileBR.close();
                    } else {
                        File invalidFile = new File(INVALID_WEB_PATH);
                        BufferedReader invalidFileBR = new BufferedReader(new FileReader(invalidFile));
                        networkOutput.write("HTTP/1.1 404 Not Found\n" + "Content-type: text/html\n"
                                + "Content-length: 126\r\n\r\n");

                        String response;
                        while ((response = invalidFileBR.readLine()) != null) {
                            networkOutput.write(response + "\r\n");
                            System.out.println("RESPONSE: " + response);
                        }
//                    invalidFileBR.close();
                    }
                    brWebRootDir.close();
                } catch (IOException ioe) {
                    System.err.println(ioe);
                } catch (Exception e){
                    e.printStackTrace();
                }finally {
                    System.out.println("Finally");
                }
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
