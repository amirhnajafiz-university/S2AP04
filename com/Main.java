package com.Model;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is our main class which gets the users
 * orders and will check them to see what should the
 * program do.
 *
 */
public class Main {

    // The list of the help
    public static String[] helpContents = {
            "\nEntering wrong inputs won't get you correct response \nPlease read the help so you can work better with app.",
            "The app orders must start with '-' or '--' with correct format",
            "The file names should not start with '-', and be more than 'Three (3)' characters , this could give you serious errors while using app",
            "Your request must start with a URL address : ",
            "  <url> ... Ex: www.google.com -i -f --method PUT ...",
            "  create <group_name> is for creating a new group, it must have a name and be more than 'Three (3)' characters",
            " -d --data is for entering multiply/form data\n    form data must be like > \"key=value&...\"",
            " -f is for following redirect after getting response",
            " -H --headers is for adding the headers \n    header must be like > \"key:value;...\"",
            " -h --help is for showing the help for application",
            " -i is for showing the headers of response",
            " -J --json is for entering a json file \n    json input must be like > {key:value,...}",
            "  list <group_name> for showing the requests (group name is optional)",
            "  fire <group_name> %number for sending a request (group name is optional)",
            " -M --method is for choosing the method type \n    post-put-delete-get-patch",
            " -O --output is for putting a response data into a file \n    you can enter the file name \n    the file name should not start with '-'",
            " -S --save <group_name> is for saving a request \n    (group name is optional / if no match then it will create the group) %% group name should not start with '-' %%" ,
            "    --upload is for putting a response data into a file \n    add the file name after your upload \n    the file name should not start with '-'",
            "if you are using orders like help or list or fire please use them in single line command \nthis can provide any problems to happen.",
            "If you want to you can use our special servers.",
            "Our servers can send the requests you want with more details.",
            "The following orders allow you to use ours or any other local servers : ",
            " --proxy --ip <ip_number> --port <port_number>",
            "   the following order can be put at the request line to send that request to any server you want.",
            " send <folder_name> %number ... --ip<ip_number> --port<port_number>",
            "   the following order will let you to send the different requests to a server just like fire order.",
            "Thanks for using our app :)"
    };

    /**
     * Our main method for executing the application.
     *
     * @param strings the line command of the user
     */
    public static void main(String[] strings) {


        // For no input
        if (strings.length == 0)
            return;

        // Check for the help in orders
        for (String i : strings) {
            if (i.equals("--help") || i.equals("-h")) {

                showHelp();
                return;
            }
        }

        ArrayList<Request> requests = new ArrayList<>(); // The list of requests to chose
        Request request = new Request(); // Creating a request
        // The following fields are for server
        boolean useProxy = false;
        String ip = null;
        int port = 0;

        int i; // index counter
        String url; // Request url

        // Check for the list and fire
        if (strings[0].equals("list")) {

            i = 0;
            String folder = null;

            if (i + 1 != strings.length) {
                folder = strings[i + 1];
            }

            Thread thread = new Thread(new FileUtils(folder)); // A thread for showing the files
            thread.start();

            try {
                thread.join(); // Waiting for the thread to be done
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;

        } else if (strings[0].equals("fire")) {

            i = 0;
            String folder = null;

            if (i + 1 == strings.length)
                return;

            // Choosing the file
            if (strings[i + 1].length() > 2) {
                folder = strings[i + 1];
                i++;
            }
            i++; // Need for controlling index

            ArrayList<Integer> indexs = new ArrayList<>();

            // Getting the indexes
            for (; i < strings.length; i++)
                indexs.add(Integer.valueOf(strings[i]));


            Thread thread = new Thread(new FileLoader(requests, folder, indexs)); // Creating a thread for getting the requests
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (strings[0].equals("create")) {

            i = 0;
            String folder = null;

            if (i + 1 != strings.length) {
                folder = strings[i + 1];

                if (folder.length() < 3) {
                    System.out.println("EE >> Name must be at least 3 characters"); // Checking the name limit
                    return;
                } else if (folder.startsWith("-")) {
                    System.out.println("EE >> Name cannot start with '-'"); // Checking the name format
                    return;
                }

            } else {
                System.out.println("EE >> Need a folder name");
                return;
            }

            Thread thread = new Thread(new CreateFolder(folder));
            thread.start();

            return;
        } else if (strings[0].equals("send")) {

            // If we want to send to a local server
            i = 0;
            String folder = null;
            useProxy = true;

            if (i + 1 == strings.length)
                return;

            // Choosing the file
            if (strings[i + 1].length() > 2) {
                folder = strings[i + 1];
                i++;
            }
            i++; // Need for controlling index

            ArrayList<Integer> indexs = new ArrayList<>();

            // Getting the indexes
            while (!strings[i].startsWith("-")) {
                indexs.add(Integer.valueOf(strings[i]));
                i++;
            }

            Thread thread = new Thread(new FileLoader(requests, folder, indexs)); // Creating a thread for getting the requests
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

            url = strings[0];
            i = 1;

            if (url.startsWith("-")) {
                System.out.println("EE >> Starting must be neither 'URL address' or a 'valid order', enter --help to get the orders");
                return;
            } else {

                try {
                    request.setUrl(url);
                } catch (MalformedURLException e) {
                    try {
                        request.setUrl("http://" + url);
                    } catch (MalformedURLException ex) {
                        System.out.println("Unknown host, check your connection and url address and try again. >> " + e.getMessage());
                    }
                }
            }
        }

        // Now we check the rest of the orders
        for (; i < strings.length; i++){

            if (strings[i].equals("-M") || strings[i].equals("--method")) {
                // The method in using
                if(i + 1 == strings.length) {
                    System.out.println("EE >> Please chose your method");
                    return;
                }

                request.setMethodInUse(strings[i + 1]);
                i++;

            } else if (strings[i].equals("-H") || strings[i].equals("--headers")){
                // Getting the headers
                if (i + 1 == strings.length) {
                    System.out.println("EE >> Please enter your headers");
                    return;
                }

                try {
                    request.setHeaders(strings[i + 1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("EE > Headers are not valid format");
                    return;
                }
                i++;

            } else if (strings[i].equals("--output") || strings[i].equals("-O")){
                // Set the saving
                request.setSaveToFile();

                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmm");

                // Creating the name
                if(i + 1 == strings.length)
                    request.setSavePath("output_[" + simpleDateFormat.format(date) + "]");
                else {
                    if (!strings[i + 1].contains("-")) {
                        request.setSavePath(strings[i + 1]);
                        i++; // For controlling the index
                    }
                    else
                        request.setSavePath("output_[" + simpleDateFormat.format(date) + "]");
                }

            } else if (strings[i].equals("-d") || strings[i].equals("--data")) {
                // Getting the form data
                if (i + 1 == strings.length) {
                    System.out.println("EE >> Please enter multiply/form data");
                    return;
                }

                try {
                    request.setFormDataList(strings[i + 1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("EE > Form / Data is not valid format");
                    return;
                }
                i++;

            } else if (strings[i].equals("-J") || strings[i].equals("--json")) {
                // Getting the json string input
                if (i + 1 == strings.length) {
                    System.out.println("EE >> Please enter your JSON");
                    return;
                }

                request.setJsonString(strings[i + 1]);
                i++;

            } else if (strings[i].equals("--upload")) {
                // Getting the upload file path
                if (i + 1 == strings.length) {
                    System.out.println("EE >> Please upload the file path");
                    return;
                }

                request.setUploadPath(strings[i + 1]);
                i++;

            } else if (strings[i].equals("--proxy"))
                useProxy = true;
            else if (strings[i].equals("--ip")) {

                ip = strings[i + 1];
                i++;
            } else if (strings[i].equals("--port")) {

                port = Integer.parseInt(strings[i + 1]);
                i++;
            } else if (strings[i].equals("-i"))
                request.setShowHeaders();
            else if (strings[i].equals("-f"))
                request.setFollowRedirect();
            else if (strings[i].equals("--save") || strings[i].equals("-S") || strings[i].equals("create")) {
                // Passing from save
                if (i + 1 != strings.length) {

                    if (!strings[i + 1].startsWith("-") && strings[i + 1].length() > 2)
                        i++;
                }
            } else if (!strings[i].equals("send")){
                // Showing the error
                System.out.println("Not a valid format or a valid input order.");
                return;
            }
        }

        if ( !strings[0].equals("fire") && !strings[0].equals("send")) {

            PassControlOfficer passControlOfficer = new PassControlOfficer(request);

            if (passControlOfficer.fullCheck()) {

                requests.add(request); // Adding the request and go

                // This is for saving
                for (int j = 0; j < strings.length; j++) {
                    if (strings[j].equals("--save") || strings[j].equals("-S")) {

                        if (j + 1 != strings.length) {

                            if (!strings[j + 1].startsWith("-") && strings[j + 1].length() > 2) {
                                request.setFolder(strings[j + 1]);
                                j++;
                            }

                        }

                        Thread thread = new Thread(new FileSaver(request)); // First we save it
                        thread.start();

                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        // Now we execute the requests
        if (requests.size() != 0 && !useProxy)
            makeConnection(requests);
        else if (requests.size() != 0)
            usingProxy(requests, ip, port);
    }

    /**
     * This method will show the help menu
     * in cmd page.
     *
     */
    public static void showHelp(){

        System.out.println(">> Welcome to J-URL. <<");

        for(String i : helpContents)
            System.out.println(i);
    }

    /**
     * This method will create the requests threads and
     * will execute them.
     *
     * @param requests the list of the requests to send
     */
    public static void makeConnection(ArrayList<Request> requests){

        ExecutorService executorService = Executors.newCachedThreadPool(); // The request service

        for (Request request : requests) {

            try {

                if (request.getMethodInUse().equals("PATCH"))
                    allowMethods();

                executorService.execute(new OpenNetwork(request, request.getSavePath()));
            } catch (MalformedURLException e) {

                System.out.println("Error 404 : page not found > " + e.getMessage());
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(requests.size() * 2000); // Time for all requests
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
    }

    /**
     * A method to send the requests to the local server that we located before.
     *
     * @param requests the requests we want to send
     * @param ip the ip of the server
     * @param port the port of the server
     */
    public static void usingProxy (ArrayList<Request> requests, String ip, int port) {

        ExecutorService executorService = Executors.newCachedThreadPool();

        for (Request i : requests) {
            try {

                if (i.getMethodInUse().equals("PATCH"))
                    allowMethods();

                Socket clientSocket = new Socket(ip, port); // Sending the requests

                executorService.execute(new ConnectToServer(clientSocket, i));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        executorService.shutdown();
    }

    /**
     * This class is a runnable to send the request
     * to the server and give back the response.
     *
     */
    private static class ConnectToServer implements Runnable {

        // The private fields
        private Socket client;
        private Request request;

        /**
         * The main constructor of the class.
         *
         * @param client the client socket
         * @param request the request of the client
         */
        public ConnectToServer (Socket client, Request request) {
            this.client = client;
            this.request = request;
        }

        @Override
        public void run() {

            try (
                    InputStream inputStream = client.getInputStream();
                    OutputStream outputStream = client.getOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            ) {

                objectOutputStream.writeObject(request);

                ServerResponse serverResponse = (ServerResponse) objectInputStream.readObject();

                if ( !serverResponse.isSuccessful() ) {
                    System.out.println(serverResponse.getResponseMassage());
                    return;
                }

                if (request.isShowHeaders()) {
                    Map<String, List<String>> headers = serverResponse.getHeaders();
                    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {

                        List<String> list = entry.getValue();

                        System.out.print(entry.getKey() + "{");
                        for (int i = 0; i < list.size(); i++) {

                            System.out.print(list.get(i));
                            if (i + 1 != list.size()) {

                                System.out.print(":");
                            }
                        }
                        System.out.print("}\n");
                    }
                }

                if (!request.isSaveToFile())
                    System.out.println(serverResponse.getMassageBody());

            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static VarHandle Modifiers; // This method and var handler are for patch method
    private static void allowMethods(){
        // This is the setup for patch method
        System.out.println("Ignore following warnings, they showed up cause we are changing some basic variables.");

        try {

            var lookUp = MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup());
            Modifiers = lookUp.findVarHandle(Field.class, "modifiers", int.class);

        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();

        }
        try {

            Field methodField = HttpURLConnection.class.getDeclaredField("methods");
            methodField.setAccessible(true);
            int mods = methodField.getModifiers();

            if (Modifier.isFinal(mods)) {
                Modifiers.set(methodField, mods & ~Modifier.FINAL);
            }

            String[] oldMethods = (String[])methodField.get(null);

            Set<String> methodsSet = new LinkedHashSet<String>(Arrays.asList(oldMethods));
            methodsSet.addAll(Collections.singletonList("PATCH"));
            String[] newMethods = methodsSet.toArray(new String[0]);
            methodField.set(null, newMethods);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
