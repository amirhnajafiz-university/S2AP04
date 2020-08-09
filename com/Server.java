package com.Model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is our local server to get the
 * client request and send it and then give
 * back the response to the client.
 *
 */
public class Server {

    public static void main(String[] args) {

        // We used a multi thread server
        ExecutorService executorService = Executors.newCachedThreadPool();
        System.out.println("Server is on ..."); // Just to inform

        try {
            // Opening the server and go for it
            ServerSocket serverSocket = new ServerSocket(1050);
            Date date;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss - DD/MM/YYYY");
            int number = 1001;

            while (true) {
                // Waiting for a client
                Socket client = serverSocket.accept();
                date = new Date();
                System.out.println("Server accepted new client " + number + " ( " + simpleDateFormat.format(date) + " )");

                executorService.execute(new ConnectToServer(client, number));
                number++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
    }

    /**
     * This class is a runnable
     * to send the request and create the
     * response.
     *
     */
    private static class ConnectToServer implements Runnable {

        // The private fields
        private Socket socket;
        private Date date;
        private int id;
        private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss - DD/MM/YYYY");

        /**
         * The main constructor of the class.
         *
         * @param socket the client socket
         * @param id the client id
         */
        public ConnectToServer (Socket socket, int id) {
            this.socket = socket;
            this.id = id;
        }

        @Override
        public void run() {

            try (
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            ){

                ServerResponse serverResponse = new ServerResponse(); // Creating a response

                Request request = (Request) objectInputStream.readObject(); // Reading the request

                OpenNetwork openNetwork = new OpenNetwork(request, request.getSavePath()); // Opening a runnable
                openNetwork.setServerResponse(serverResponse);

                Thread open = new Thread(openNetwork); // Make the connection
                open.start();

                try {
                    open.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                objectOutputStream.writeObject(serverResponse);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                date = new Date();
                System.out.println("Server is done with the client " + id + " ( " + simpleDateFormat.format(date) + " )");
            }
        }
    }
}
