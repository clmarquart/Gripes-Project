package net.sf.gripes.interceptor

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap 

import javax.servlet.ServletContext 
import javax.servlet.http.HttpServletRequest

import org.slf4j.Logger 
import org.slf4j.LoggerFactory;

import net.sourceforge.stripes.action.ActionBeanContext 
import net.sourceforge.stripes.action.Resolution 
import net.sourceforge.stripes.config.ConfigurableComponent 
import net.sourceforge.stripes.config.Configuration 
import net.sourceforge.stripes.controller.ExecutionContext 
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts 
import net.sourceforge.stripes.controller.LifecycleStage 
import net.sourceforge.stripes.controller.StripesConstants 

@Intercepts( [ LifecycleStage.RequestInit, LifecycleStage.RequestComplete ])
public class Gripes implements Interceptor, ConfigurableComponent {
	Logger logger = LoggerFactory.getLogger(Gripes.class)
	static Logger _logger = LoggerFactory.getLogger(Gripes.class)

	static private final ThreadLocal<Map<String, Object>> contextAttributes = new ThreadLocal<Map<String, Object>>();

	def config, request, actionBeanContext

	static {
		_logger.debug "+++++++++++++++++++++++++++++++++++++++++++++++"
		_logger.debug "Initializing Gripes Interceptor"
		_logger.debug "+++++++++++++++++++++++++++++++++++++++++++++++"
	}
	
	@Override void init(Configuration configuration) throws Exception {
		
	}
	
	static void requestInit(def context, HttpServletRequest request) {
		Map<String, Object> map = Gripes.contextAttributes.get()

		if (!map) {
			map = new ConcurrentHashMap<String, Object>();
			map.putAll(["ActionBeanContext": context, "Request": request])
			Gripes.contextAttributes.set(map);
		}
	}

	static void requestComplete() {
		Map<String, Object> map = Gripes.contextAttributes.get();

		if (!map) {
			// looks like nobody needed us this time
			return;
		}

		Gripes.contextAttributes.remove();
	}
	
	
	@Override Resolution intercept(ExecutionContext context) throws Exception {
        ActionBeanContext beanContext = context.getActionBeanContext()
        HttpServletRequest request = (beanContext == null) ? null : beanContext.getRequest()

        if (request == null || request.getAttribute(StripesConstants.REQ_ATTR_INCLUDE_PATH) == null) {
            switch (context.getLifecycleStage()) {
                case net.sourceforge.stripes.controller.LifecycleStage.RequestInit:
                    requestInit(beanContext, request)
                    break
                case net.sourceforge.stripes.controller.LifecycleStage.RequestComplete:
                    requestComplete()
                    break
            }
        }

        context.proceed()
	}
	
	static def getActionBeanContext() {
		Gripes.contextAttributes.get().get("ActionBeanContext")
	}
	static def getServletContext() {
		Gripes.contextAttributes.get().get("ActionBeanContext").servletContext
	}
	static def getConfig() {
		try {
			Gripes.contextAttributes.get().get("ActionBeanContext").servletContext.getAttribute("ehealth.config")
		} catch (e) {
			Gripes.loadConfig()
		}
	}

	static def loadConfig() {
		try {
			new ConfigSlurper().parse(this.getClassLoader().getResource("Config.groovy"))
		} catch (e) {
			e.printStackTrace()
			[:]
		}
	}

	static def getRequest() {
		Gripes.contextAttributes.get().get("Request")
	}
}