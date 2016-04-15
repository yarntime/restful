/**
 *  
 * Function Description
 *
 * @author: zhangwei
 * @date:  Apr 12, 2016
 * @version: 1.0
 */
package com.skycloud.common.rest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMethod;

import com.skycloud.common.utils.PathTrie;
import com.skycloud.common.utils.RestUtils;

@Resource(name="HandlerFactory")
public class HandlerFactory {

    private final static PathTrie<RestHandler> getHandlers = new PathTrie<RestHandler>(RestUtils.REST_DECODER);
    private final static PathTrie<RestHandler> postHandlers = new PathTrie<RestHandler>(RestUtils.REST_DECODER);
    private final static PathTrie<RestHandler> putHandlers = new PathTrie<RestHandler>(RestUtils.REST_DECODER);
    private final static PathTrie<RestHandler> deleteHandlers = new PathTrie<RestHandler>(RestUtils.REST_DECODER);
    private final static PathTrie<RestHandler> headHandlers = new PathTrie<RestHandler>(RestUtils.REST_DECODER);
    private final static PathTrie<RestHandler> optionsHandlers = new PathTrie<RestHandler>(RestUtils.REST_DECODER);
    
    public static synchronized void registerHandler(RequestMethod method, String path, RestHandler handler) {
        switch (method) {
            case GET:
                getHandlers.insert(path, handler);
                break;
            case DELETE:
                deleteHandlers.insert(path, handler);
                break;
            case POST:
                postHandlers.insert(path, handler);
                break;
            case PUT:
                putHandlers.insert(path, handler);
                break;
            case OPTIONS:
                optionsHandlers.insert(path, handler);
                break;
            case HEAD:
                headHandlers.insert(path, handler);
                break;
            default:
                throw new IllegalArgumentException("Can't handle [" + method + "] for path [" + path + "]");
        }
    }
    
    @SuppressWarnings("unchecked")
    public static synchronized RestHandler getHandler(ApiHttpServletRequest request) {
        String path = getPath(request);
        RequestMethod method = RequestMethod.valueOf(request.getMethod());
        
        switch (method) {
            case GET:
                return getHandlers.retrieve(path, request.getPathParamMap());
            case DELETE:
                return deleteHandlers.retrieve(path, request.getPathParamMap());
            case POST:
                return postHandlers.retrieve(path, request.getPathParamMap());
            case PUT:
                return putHandlers.retrieve(path, request.getPathParamMap());
            case OPTIONS:
                return optionsHandlers.retrieve(path, request.getPathParamMap());
            case HEAD:
                return headHandlers.retrieve(path, request.getPathParamMap());
            default:
                throw new IllegalArgumentException("Can't handle [" + method + "] for path [" + path + "]");
        }
    }
    
    private static String getPath(HttpServletRequest request) {
        return request.getPathInfo();
    }
}
