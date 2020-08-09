package com.Model;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

/**
 * This class is the user single request
 * which can be read or be write by the user.
 * It keeps the url address and the headers and ... everything
 * that a request needs to be send.
 *
 */
public class Request implements Serializable {

    // Our private fields
    private URL url;
    private String folder;

    private Hashtable<String, String> headers;
    private Hashtable<String, String> formData;

    private String headersLineInput;
    private String formDataLineInput;

    private String uploadPath;
    private String savePath;
    private String jsonString;
    private String methodInUse = "GET";

    private boolean showHeaders;
    private boolean followRedirect;
    private boolean saveToFile;

    private boolean hasFormData = false;
    private boolean hasJson = false;
    private boolean hasUploadFile = false;

    /**
     * The constructor of the request class.
     *
     */
    public Request(){

        headers = new Hashtable<>();
        formData = new Hashtable<>();
    }

    /**
     * This method will set the request url address.
     *
     * @param urlAddress the address that request is going to send at
     * @throws MalformedURLException for wrong format urls
     */
    public void setUrl (String urlAddress) throws MalformedURLException {
        url = new URL(urlAddress);
    }

    /**
     * This is for defining the method is use for
     * our request.
     *
     * @param method the type of send
     */
    public void setMethodInUse (String method){
        methodInUse = method;
    }

    /**
     * A method for setting the json string of the request.
     *
     * @param jsonString the string we put for json part
     */
    public void setJsonString (String jsonString) {
        this.jsonString = jsonString;
        hasJson = true;
    }

    /**
     * This is for grouping the request.
     *
     * @param folder the name of the group
     */
    public void setFolder (String folder){
        this.folder = folder;
    }

    /**
     * This is for setting the upload file path.
     *
     * @param path the abstract path of the file
     */
    public void setUploadPath (String path){
        uploadPath = path;
        hasUploadFile = true;
    }

    /**
     * This method will save the response file inside
     * this path.
     *
     * @param savePath the file path
     */
    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    /**
     * This method will set the situation to
     * save the response into a file or not.
     *
     */
    public void setSaveToFile(){
        saveToFile = true;
    }

    /**
     * This method will create the headers set.
     *
     * @param headersInput the line of headers
     */
    public void setHeaders (String headersInput) throws ArrayIndexOutOfBoundsException{

        headersLineInput = headersInput;

        String[] headersSet = headersInput.split(";");

        for (String keyValueSet : headersSet){

            String[] keyValue = keyValueSet.split(":");

            headers.put(keyValue[0], keyValue[1]);
        }
    }

    /**
     * This method will create the form data set.
     *
     * @param formDataInput the line of data
     */
    public void setFormDataList (String formDataInput) throws ArrayIndexOutOfBoundsException{

        formDataLineInput = formDataInput;

        String[] dataSet = formDataInput.split("&");

        for (String keyValueSet : dataSet){

            String[] keyValue = keyValueSet.split("=");

            formData.put(keyValue[0], keyValue[1]);
        }

        hasFormData = true;
    }

    /**
     * This method will set the redirecting on.
     *
     */
    public void setFollowRedirect (){
        followRedirect = true;
    }

    /**
     * This method will set the situation of showing
     * the response headers to true.
     *
     */
    public void setShowHeaders (){
        showHeaders = true;
    }

    /**
     * A getter method for getting the url address.
     *
     * @return the url address
     */
    public URL getUrl() {
        return url;
    }

    /**
     * This method will return the set of headers.
     *
     * @return the list of headers
     */
    public Hashtable<String, String> getHeaders() {
        return headers;
    }

    /**
     * This method will return the set of data.
     *
     * @return the data set
     */
    public Hashtable<String, String> getFormData() {
        return formData;
    }

    /**
     * This method will return the method type.
     *
     * @return the method type
     */
    public String getMethodInUse() {
        return methodInUse;
    }

    /**
     * This method will return the path
     * of the uploaded file.
     *
     * @return the path
     */
    public String getUploadPath() {
        return uploadPath;
    }

    /**
     * The situation of following the redirect.
     *
     * @return the status
     */
    public boolean isFollowRedirect() {
        return followRedirect;
    }

    /**
     * The situation of showing the response headers.
     *
     * @return the status
     */
    public boolean isShowHeaders() {
        return showHeaders;
    }

    /**
     * This method returns the folder name of
     * the request.
     *
     * @return the name of the folder
     */
    public String getFolder() {
        return folder;
    }

    /**
     * This method will return the path
     * of the output response.
     *
     * @return the file address
     */
    public String getSavePath() {
        return savePath;
    }

    /**
     * This method will return the status
     * of saving the response into a file.
     *
     * @return the status
     */
    public boolean isSaveToFile() {
        return saveToFile;
    }

    /**
     * A method for getting the json string.
     *
     * @return the json string
     */
    public String getJsonString() {
        return jsonString;
    }

    /**
     * A getter method for getting the status
     * about this request form data.
     *
     * @return has or not
     */
    public boolean isHasFormData() {
        return hasFormData;
    }

    /**
     * A getter method for getting the status
     * about this request json string.
     *
     * @return has or not
     */
    public boolean isHasJson() {
        return hasJson;
    }

    /**
     * A getter method for getting the status
     * about this request uploaded file.
     *
     * @return has or not
     */
    public boolean isHasUploadFile() {
        return hasUploadFile;
    }

    /**
     * A getter for getting the users input headers.
     *
     * @return the line of headers
     */
    public String getHeadersLineInput() {
        return headersLineInput;
    }

    /**
     * A getter method for getting the users input
     * data form.
     *
     * @return the line of data/form
     */
    public String getFormDataLineInput() {
        return formDataLineInput;
    }

    /**
     * An override for showing the requests.
     *
     * @return the request into a string
     */
    @Override
    public String toString() {
        return  "url: " + url +
                "| method: " + methodInUse +
                "| headers: " + headers +
                "| form/data: " + formData +
                "| json: '" + jsonString + '\'' +
                "| upload: '" + uploadPath + '\'' +
                "| output in: '" + savePath + '\'' +
                "| follow redirect: " + isFollowRedirect() +
                "| show response headers: " + isShowHeaders()
                ;
    }
}
