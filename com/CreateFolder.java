package com.Model;

import java.io.File;

/**
 * This class will create the folders for grouping
 * the requests.
 *
 */
public class CreateFolder implements Runnable {

    private static final String REQUESTS_PATH = "./Requests/"; // The requests path
    private final String FOLDER;

    /**
     * The main constructor of the class.
     *
     * @param folder the name of the folder
     */
    public CreateFolder (String folder) {
        FOLDER = folder;
    }

    /**
     * This method will check for the folder name
     * and if doesn't exists it will make it.
     *
     */
    @Override
    public void run() {
        boolean create = new File(REQUESTS_PATH + FOLDER).exists();
        boolean file;
        if (!create)
            file = new File(REQUESTS_PATH + FOLDER).mkdir();
    }
}
