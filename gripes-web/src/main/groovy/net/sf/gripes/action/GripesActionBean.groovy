package net.sf.gripes.action

import net.sf.gripes.interceptor.Gripes
import net.sf.gripes.util.GripesActionBeanContext

import net.sourceforge.stripes.action.ActionBean
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.ForwardResolution
import net.sourceforge.stripes.action.RedirectResolution
import net.sourceforge.stripes.action.StreamingResolution
import net.sourceforge.stripes.action.ActionBeanContext

import javax.servlet.http.HttpServletRequest;

abstract class GripesActionBean implements ActionBean {	
	GripesActionBeanContext context

	static {
		
	}
	
	Resolution forward(String page, Closure c) {
		c.call(Gripes.getRequest())
		
		new ForwardResolution("/WEB-INF/jsp/"+this.class.simpleName.replace("ActionBean","").toLowerCase()+"/"+page+".jsp")
	}

	Resolution forward(String page) {
		new ForwardResolution("/WEB-INF/jsp/"+this.class.simpleName.replace("ActionBean","").toLowerCase()+"/"+page+".jsp")
	}
	
	Resolution forward(String page, String extension) {
		new ForwardResolution("/WEB-INF/${extension}/${this.class.simpleName.replace('ActionBean','')}/${page}.${extension}")
	}
	
	Resolution forward(File page) {
		new ForwardResolution(page.canonicalPath.replaceAll(getContext().servletContext.getRealPath("/"),''))
	}
	
	Resolution redirect(String url) {
		new RedirectResolution(url)
	}
	
	Resolution redirect(bean, event) {
		new RedirectResolution(bean, event)
	}
	
	Resolution stream(String html) {
		new StreamingResolution("text/html", html)
	}
	
	Resolution notFound() {
		context.response.status = 404
		new ForwardResolution("/WEB-INF/errors/404.jsp")
	}

	Resolution notAuthorized() {
		context.response.status = 401
		new ForwardResolution("/WEB-INF/errors/401.jsp")
	}
	
	void setContext(ActionBeanContext context) { 
		this.context = (GripesActionBeanContext) context
	}	
	
	GripesActionBeanContext getContext() { 
		context
	}
	
    String getLastUrl() {
        HttpServletRequest req = getContext().request
		String lastUrl = ""
		
        String uri = req.getAttribute("javax.servlet.forward.request_uri")
        String path = req.getAttribute("javax.servlet.forward.path_info")

        if (uri == null) {
            uri = req.requestURI
            path = req.pathInfo
        }
		lastUrl+=uri

        if (path) lastUrl+=path

		lastUrl+='?'
		req.getParameterMap().each { k,v ->
			lastUrl+="${k}=${v[0]?:''}&"
		}
		(lastUrl[0..lastUrl.length()-2]).replace(getContext().request.contextPath,"")
    }
}