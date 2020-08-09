package com.Model;

//import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Open network class is a thread that gets a request
 * and then will connect to the url address to get the
 * response.
 *
 */
public class OpenNetwork implements Runnable{

    // The private fields
    private Request request;
    private URL urlAddress;
    private String fileName;
    private ServerResponse serverResponse; // The response

    private HttpURLConnection urlConnection;

    /**
     * The main constructor of the open network
     * class.
     *
     * @param request the user request
     * @param fileName the name of the file for output
     * @throws MalformedURLException for not getting the correct url address
     */
    public OpenNetwork(Request request, String fileName) throws MalformedURLException {

        this.request = request;

        this.urlAddress = request.getUrl();
        urlConnection = null;
        this.fileName = fileName;
    }

    /**
     * A setter method for setting the server response.
     *
     * @param serverResponse the new server response
     */
    public void setServerResponse(ServerResponse serverResponse) {
        this.serverResponse = serverResponse;
    }

    /**
     * This method will create the form data nad
     * will write it to the url connection output
     * stream.
     *
     * @param dataSet the set of the data
     * @param boundary inuse boundary in connection
     * @param bufferedOutputStream the url output stream
     * @throws IOException for problems in writing
     */
    public void bufferOutFormData(Map<String, String> dataSet, String boundary, BufferedOutputStream bufferedOutputStream) throws IOException {

        for (String key : dataSet.keySet()) {

            bufferedOutputStream.write(("--" + boundary + "\r\n").getBytes());

            if (key.contains("file")) {

                File file = new File(dataSet.get(key));
                bufferedOutputStream.write(("Content-Disposition: form-data; filename=\"" + file.getName() + "\"\r\nContent-Type: Auto\r\n\r\n").getBytes());

                try {

                    BufferedInputStream tempBufferedInputStream = new BufferedInputStream(new FileInputStream(file));

                    byte[] filesBytes = tempBufferedInputStream.readAllBytes();
                    bufferedOutputStream.write(filesBytes);
                    bufferedOutputStream.write("\r\n".getBytes());
                } catch (IOException e) {

                    e.printStackTrace();
                }
            } else {

                bufferedOutputStream.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n").getBytes());
                bufferedOutputStream.write((dataSet.get(key) + "\r\n").getBytes());
            }
        }

        bufferedOutputStream.write(("--" + boundary + "--\r\n").getBytes());
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

    /**
     * This method will create the form data
     * and will use other methods to write it.
     *
     */
    public void formData() {

        Map<String, String> formData = request.getFormData();

        if (formData.size() == 0)
            return;

        try {

            String boundary = System.currentTimeMillis() + "";

            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(urlConnection.getOutputStream());

            bufferOutFormData(formData, boundary, bufferedOutputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will update the binary file for
     * us based on the path we gave to it.
     *
     */
    public void uploadBinary() {

        if (request.getUploadPath() == null)
            return;

        try {

            File file = new File(request.getUploadPath());

            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/octet-stream");

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(file));

            bufferedOutputStream.write(fileInputStream.readAllBytes());
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * This method will create the json body
     * and will send it.
     *
     */
    public void createJson(){

        if (request.getJsonString() == null)
            return;

        String jsonBuilder = request.getJsonString();

        urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        urlConnection.setRequestProperty("Accept", "application/json");

        urlConnection.setDoOutput(true);

        try (OutputStream os = urlConnection.getOutputStream();) {

            byte[] input = jsonBuilder.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The run method of this thread that
     * opens a connection and will get the response data.
     *
     */
    @Override
    public void run() {

        try {

            if ("http".equals(urlAddress.getProtocol())) {

                urlConnection = (HttpURLConnection) urlAddress.openConnection(); // giving the connection instance casted
            } else if ("https".equals(urlAddress.getProtocol())) {

                urlConnection = (HttpsURLConnection) urlAddress.openConnection();
            } else {

                if (serverResponse != null) {
                    serverResponse.setResponseMassage("UNSUPPORTED PROTOCOL!");
                    serverResponse.setSuccessful(false);
                } else
                    System.err.println("UNSUPPORTED PROTOCOL!");
                return;
            }

            urlConnection.setRequestMethod(request.getMethodInUse()); // default is get

            formData();
            uploadBinary();
            createJson();

            // Sending the headers
            Map<String, String> headers = request.getHeaders();
            if (headers.size() != 0) {

                for (Map.Entry<String, String> keyValue : headers.entrySet()) {

                    urlConnection.setRequestProperty(keyValue.getKey(), keyValue.getValue());
                }
            }

            if (serverResponse != null) {
                serverResponse.setResponseCode(urlConnection.getResponseCode());
                serverResponse.setResponseMassage(urlConnection.getResponseMessage());
            }

            // Check the connection
            try {

                if (urlConnection.getResponseCode() / 100 != 2) {

                    if (serverResponse != null) {
                        serverResponse.setResponseMassage(urlConnection.getResponseMessage() + urlConnection.getResponseCode() +
                                "\n" + urlConnection.getHeaderField("Location") +
                                "\n" + urlConnection.getHeaderField("Set-Cookie")
                        );
                        serverResponse.setSuccessful(false);
                    } else {
                        System.out.println(urlConnection.getResponseMessage() + urlConnection.getResponseCode());
                        System.out.println(urlConnection.getHeaderField("Location"));
                        System.out.println(urlConnection.getHeaderField("Set-Cookie"));
                    }

                    // For following the redirect
                    if (request.isFollowRedirect()) {
                        String newUrl = urlConnection.getHeaderField("Location");

                        // get the cookie if need, for login
                        String cookies = urlConnection.getHeaderField("Set-Cookie");

                        // open the new connection again
                        urlConnection = (HttpURLConnection) new URL(newUrl).openConnection();
                        urlConnection.setRequestProperty("Cookie", cookies);
                        urlConnection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                        urlConnection.addRequestProperty("User-Agent", "Mozilla");
                        urlConnection.addRequestProperty("Referer", "google.com");

                        try {
                            if (urlConnection.getResponseCode() / 100 != 2) {
                                throw new IOException("404 not found");
                            }
                        } catch (IOException e) {
                            if (serverResponse != null) {
                                serverResponse.setSuccessful(false);
                                serverResponse.setResponseMassage(e.getMessage());
                            } else
                                System.out.println(e.getMessage());
                            return;
                        }

                    } else {
                        throw new UnknownHostException();
                    }
                }
            } catch (UnknownHostException e){

                if (serverResponse != null) {
                    serverResponse.setSuccessful(false);
                    serverResponse.setResponseMassage("No hosts recognize by " + urlAddress + "::" + urlConnection.getResponseCode() + urlConnection.getResponseMessage() +
                                    "\nCheck your connection and try again."
                            );
                } else {
                    System.out.println("No hosts recognize by " + urlAddress + "::" + urlConnection.getResponseCode() + urlConnection.getResponseMessage());
                    System.out.println("Check your connection and try again.");
                }
                return;
            }

        } catch (IOException e) {

            if (serverResponse != null) {
                serverResponse.setSuccessful(false);
                serverResponse.setResponseMassage("FAILED TO OPEN CONNECTION!" + e.getMessage());
            } else
                System.err.println("FAILED TO OPEN CONNECTION!" + e.getMessage());
            return;
        }

        if (serverResponse != null)
            serverResponse.setSuccessful(true);

        // For showing the response headers or not
        if (request.isShowHeaders()) {
            Map<String, List<String>> headers = urlConnection.getHeaderFields();
            if (serverResponse == null) {
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
            } else
                serverResponse.setHeaders(headers);
        } else if (serverResponse != null)
            serverResponse.setHeaders(null);

        // For saving the data into a file
        if (request.isSaveToFile()) {

            File file = new File(fileName);

            try {

                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());


                byte[] bytes = bufferedInputStream.readAllBytes();
                fileOutputStream.write(bytes);

//                if (urlConnection.getContentType().equals("json")) {
//
//                    JSONObject jsonObject = new JSONObject(bytes);
//                    printWriter.println(jsonObject);
//                } else {
//                    printWriter.write(new String(bytes));
//                }

                bufferedInputStream.close();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            try {

                BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());

                byte[] bytes = bufferedInputStream.readAllBytes();
                String string = new String(bytes);

//                if (urlConnection.getContentType().equals("json")) {
//
//                    JSONObject jsonObject = new JSONObject(bytes);
//                    string = new String(String.valueOf(jsonObject));
//                } else {
//                    string = new String(bytes);
//                }

                if (serverResponse != null)
                    serverResponse.setMassageBody(string);
                else
                    System.out.println(string);

                bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
