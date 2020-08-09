package com.Model;

/**
 * This class will read the user inputs
 * and will check if everything is correct.
 * From the url and headers to json part and others.
 *
 */
public class PassControlOfficer {

    // The request we check
    private Request request;

    String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH"}; // The list of the methods

    /**
     * The main constructor of the class.
     *
     * @param request the request we want to check
     */
    public PassControlOfficer (Request request){
        this.request = request;
    }

    /**
     * The method for checking the inputs.
     *
     * @return valid or not
     */
    public boolean fullCheck(){

        if (request.getUrl() == null)
            return checkResponse(11);

        if (request.getFolder() != null)
            if (request.getFolder().length() < 3 || request.getFolder().startsWith("-"))
                return checkResponse(21);

        boolean flag = true;
        for ( String i : methods )
            if (request.getMethodInUse().equals(i)) {
                flag = false;
                break;
            }

        if (flag)
            return checkResponse(31);

        if (!request.getMethodInUse().equals("GET")) {

            if (!(request.isHasFormData() || request.isHasJson() || request.isHasUploadFile()))
                return checkResponse(41);

            if ( (request.isHasFormData() && request.isHasJson()) || (request.isHasJson() && request.isHasUploadFile()) || (request.isHasUploadFile() && request.isHasFormData()) )
                return checkResponse(42);

        }

        String headerLine = request.getHeadersLineInput();

        if (headerLine != null) {
            int result1 = headerLine.split(";", -1).length;
            int result2 = headerLine.split(":", -1).length - 1;

            if (result1 != result2 && result2 != 1)
                return checkResponse(51);
        }

        String dataLine = request.getFormDataLineInput();

        if (dataLine != null) {
            int result1 = dataLine.split("&", -1).length;
            int result2 = dataLine.split("=", -1).length - 1;

            if (result1 != result2 && result2 != 1)
                return checkResponse(52);
        }

        return true;
    }

    /**
     * The result method for showing the errors types.
     *
     * @param code the code of the error
     * @return the false statement
     */
    private boolean checkResponse(int code) {

        switch (code) {
            case 11:
                System.out.println("EE > No url for this request");
                break;
            case 21:
                System.out.println("EE > Not a valid folder name");
                break;
            case 31:
                System.out.println("EE > Not supported method");
                break;
            case 41:
                System.out.println("EE > With this method you must have a body type");
                break;
            case 42:
                System.out.println("EE > You can only chose one body type");
                break;
            case 51:
                System.out.println("EE > Headers are not valid format");
                break;
            case 52:
                System.out.println("EE > Form / Data is not valid format");
                break;
        }

        return false;
    }
}
