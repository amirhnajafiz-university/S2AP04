package com.Model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This class is a response we send to the
 * client to get the answer of the request.
 *
 */
public class ServerResponse implements Serializable {

    // The private fields
    private int responseCode;
    private String responseMassage;
    private String massageBody;
    private Map<String, List<String>> headers;
    private boolean isSuccessful;

    /**
     * A method for setting the headers of the response.
     *
     * @param headers the set of the headers
     */
    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    /**
     * A setter method for setting the massage body
     * of the response.
     *
     * @param massageBody the massage body
     */
    public void setMassageBody(String massageBody) {
        this.massageBody = massageBody;
    }

    /**
     * A setter method for setting the response code.
     *
     * @param responseCode the response code
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * A setter method for setting the response
     * massage.
     *
     * @param responseMassage the response massage
     */
    public void setResponseMassage(String responseMassage) {
        this.responseMassage = responseMassage;
    }

    /**
     * A setter method for setting the status
     * of the sending.
     *
     * @param successful the status
     */
    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    /**
     * A getter method for getting the status
     * of the sending.
     *
     * @return  successful the status
     */
    public boolean isSuccessful() {
        return isSuccessful;
    }

    /**
     * A method for getting the headers of the response.
     *
     * @return  headers the set of the headers
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * A getter method for getting the massage body
     * of the response.
     *
     * @return  massageBody the massage body
     */
    public String getMassageBody() {
        return massageBody;
    }

    /**
     * A getter method for getting the response
     * massage.
     *
     * @return  responseMassage the response massage
     */
    public String getResponseMassage() {
        return responseMassage;
    }
}
