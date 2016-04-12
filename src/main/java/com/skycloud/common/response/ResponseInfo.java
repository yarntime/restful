/**
 * 
 * response info
 *
 * @author: franklin
 * @date: Mar 24, 2016
 * @version: 1.0
 */
package com.skycloud.common.response;

import java.io.Serializable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.skycloud.common.constants.Constants;

public class ResponseInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private ResponseCode code;

    private String message;

    private Object result;

    private String ticket;

    private String version = Constants.VERSION;

    private static JsonParser jsonParser = new JsonParser();

    public ResponseCode getCode() {
        return this.code;
    }

    public void setCode(ResponseCode code) {
        this.code = code;
    }

    /**
     * return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * return the result
     */
    public Object getResult() {
        return result;
    }

    /**
     * param result the result to set
     */
    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * param version the result to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * return the ticket
     */
    public String getTicket() {
        return ticket;
    }

    /**
     * param ticket the result to set
     */
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String toJsonString() {

        JsonObject jsonStr = new JsonObject();

        jsonStr.add("result", jsonParser.parse(this.getResult().toString()));
        jsonStr.addProperty("version", Constants.VERSION);
        jsonStr.addProperty("code", this.getCode().toString());

        if (this.getMessage() != null) {
            jsonStr.addProperty("message", this.getMessage());
        }

        return jsonStr.toString();
    }
}
