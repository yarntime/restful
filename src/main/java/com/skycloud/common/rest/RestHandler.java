/**
 *  
 * Function Description
 *
 * @author: zhangwei
 * @date:  Apr 12, 2016
 * @version: 1.0
 */
package com.skycloud.common.rest;

public interface RestHandler {

    Object handleRequest(ApiHttpServletRequest request);
}
