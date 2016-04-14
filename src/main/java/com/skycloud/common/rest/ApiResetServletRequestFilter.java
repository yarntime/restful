/**
 *  
 * Function Description
 *
 * @author: zhangwei
 * @date:  Apr 14, 2016
 * @version: 1.0
 */
package com.skycloud.common.rest;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ApiResetServletRequestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(new ApiHttpServletRequest((HttpServletRequest) request), response);
    }

    @Override
    public void destroy() {
        
    }

}
