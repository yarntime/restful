/**
 *  
 * Function Description
 *
 * @author: zhangwei
 * @date:  Apr 13, 2016
 * @version: 1.0
 */
package com.skycloud.common.handler;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import com.skycloud.common.rest.ApiHttpServletRequest;
import com.skycloud.common.rest.HandlerFactory;
import com.skycloud.common.rest.RestHandler;

@Component
public class HealthHandler implements RestHandler {

    @PostConstruct
    public void registePath() {
        HandlerFactory.registerHandler(RequestMethod.GET, "v1/health", this);
        HandlerFactory.registerHandler(RequestMethod.POST, "v1/health", this);
        HandlerFactory.registerHandler(RequestMethod.GET, "v1/{namespace}/health", this);
    }

    @Override
    public Object handleRequest(ApiHttpServletRequest request) {
        return "I'm OK, namespace is " + request.getPathParam("namespace");
    }
}
