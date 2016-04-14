/**
 *  
 * Function Description
 *
 * @author: zhangwei
 * @date:  Apr 14, 2016
 * @version: 1.0
 */
package com.skycloud.common.rest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ApiHttpServletRequest extends HttpServletRequestWrapper {

    private Map<String, String> pathParamMap = new HashMap<String, String>();
    /**
     * @param request
     */
    public ApiHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @SuppressWarnings("rawtypes")
    public Map getPathParamMap() {
        return this.pathParamMap;
    }
    
    public String getPathParam(String key) {
        return pathParamMap.get(key);
    }
}
