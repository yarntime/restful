/**
 *  
 * Function Description
 *
 * @author: zhangwei
 * @date:  Apr 13, 2016
 * @version: 1.0
 */
package com.skycloud.common.exception;

public class RestException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -5281075613619601375L;

    public RestException() {
        super();
    }
    
    public RestException(String msg) {
        super(msg);
    }
}
