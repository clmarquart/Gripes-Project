package net.sf

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

class TestFilter implements Filter {
	FilterConfig filterConfig = null;
	
	void init(FilterConfig filterConfig) throws ServletException {
      	this.filterConfig = filterConfig;
	}

	void destroy() {
		this.filterConfig = null;
	}

	void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
   		if (filterConfig == null)
        	return;

		println "FILTER: " + filterConfig.getInitParameter("param")
   }
}