package com.Model;

import java.io.*;
import java.util.ArrayList;

/**
 * This class is a runnable that gets the users
 * inputs and the folder name and will add the
 * requests that need to be run to the list.
 *
 */
public class FileLoader implements Runnable{

    private static final String REQUESTS_PATH = "./Requests/"; // The requests path
    private final String FOLDER;

    // The private sets
    private ArrayList<Request> requests;
    private ArrayList<Integer> indexes;

    /**
     * The main constructor of this class.
     *
     * @param requests the set of requests
     * @param folder the name of the folder
     * @param indexes the list of the indexes
     */
    public FileLoader(ArrayList<Request> requests , String folder, ArrayList<Integer> indexes){
        this.requests = requests;
        this.indexes = indexes;
        FOLDER = folder;
    }

    /**
     * An override of run to load the requests.
     *
     */
    @Override
    public void run() {

        File[] files;

        if (FOLDER == null)
            files = new File(REQUESTS_PATH).listFiles();
        else
            files = new File(REQUESTS_PATH + FOLDER).listFiles();

        if (files == null)
            return;

        if (files.length == 0)
            return;

        int iterate = 0; // Need this for iterating

        for (File file : files) {

            if (file.getName().contains(".bin")) {
                if(indexes.contains(iterate + 1)) {
                    try (FileInputStream fileInputStream = new FileInputStream(file);
                         ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    ) {

                        Request request = (Request) objectInputStream.readObject(); // Reading the request
                        requests.add(request);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                iterate++;
            }
        }
    }
}
