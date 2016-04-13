/**
 * 
 * Base Controller contains some common methods
 *
 * @author: zhangwei
 * @date: Mar 24, 2016
 * @version: 1.0
 */
package com.skycloud.common.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.skycloud.common.constants.Constants;
import com.skycloud.common.response.ResponseCode;
import com.skycloud.common.response.ResponseInfo;


@Controller
@RequestMapping(value = "/v1/**")
public class RestController {

    private static Logger LOGGER = Logger.getLogger(RestController.class);

    private static Gson gson = new Gson();

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public void getResources(HttpServletRequest request, HttpServletResponse response) {
        ResponseInfo responseInfo = new ResponseInfo();
        try {
            RestHandler handler = HandlerFactory.getHandler(request);
            Object result = handler.handleRequest(request);
            responseInfo.setResult(result);
            responseInfo.setCode(ResponseCode.SUCCESS);
        } catch (Exception e) {
            LOGGER.error("failed to handle request cause by " + e.getMessage());
            responseInfo.setMessage(e.getMessage());
            responseInfo.setCode(ResponseCode.FAILURE);
        }
        writeResponse(response, responseInfo, 200);
    }
    
    public void writeResponse(HttpServletResponse resp, Object responseObject, int responseCode) {
        try {
            String responseText = gson.toJson(responseObject);
            resp.setStatus(responseCode);
            if (responseText == null || responseText.isEmpty()) {
                resp.setContentType(Constants.HTML_CONTENTTYPR);
            } else {
                resp.setContentType(Constants.JSON_CONTENTTYPE);
            }
            resp.getWriter().print(responseText);
            resp.getWriter().flush();
        } catch (IOException ioex) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("exception writing response: " + ioex);
            }
        } catch (Exception ex) {
            if (!(ex instanceof IllegalStateException)) {
                LOGGER.error("unknown exception writing api response", ex);
            }
        }
    }
}
