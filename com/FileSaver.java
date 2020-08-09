package com.Model;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The FileSaver class is a thread that will
 * save a request ( inside a group or not ).
 *
 */
public class FileSaver implements Runnable {

    private static final String REQUESTS_PATH = "./Requests/"; // The requests path
    private Request request; // The request that wants to save

    /**
     * Main constructor of the save class.
     *
     * @param request the request we want to save
     */
    public FileSaver(Request request){

        boolean folder = new File(REQUESTS_PATH).mkdir(); // Opening the folder

        this.request = request;
    }

    /**
     * The run method to save the request inside a
     * file.
     *
     */
    @Override
    public void run() {

        StringBuilder path = new StringBuilder();
        String folder = "";

        path.append(REQUESTS_PATH);

        if (request.getFolder() != null) {
            boolean file = new File(REQUESTS_PATH + request.getFolder()).mkdir();
            path.append(request.getFolder());
            path.append("/");
        }

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMYYYY$$HHmmss");

        path.append(simpleDateFormat.format(date));
        path.append(".bin");

        File file = new File(path.toString());

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(request);

            objectOutputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
