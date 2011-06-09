package net.sf.gripes.util

import net.sourceforge.stripes.action.ActionBean
import net.sourceforge.stripes.action.ActionBeanContext
import net.sourceforge.stripes.controller.NameBasedActionResolver

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GripesActionResolver extends NameBasedActionResolver {
	Logger logger = LoggerFactory.getLogger(GripesActionResolver.class)
	
	//TODO adding actions on the fly needs to happen from plugin.
	GripesActionResolver() {
		logger.info "Initializing Gripes Action Resolver"
		
		super.addActionBean(Class.forName("net.sf.gripes.action.GripesLoginActionBean"))
	}

	/**
	 * There is no use for this method at this time.  Leaving this hear to help
	 * remember in case it is needed.
	 */
	//@Override Set<String> getBasePackages() { return Literal.set("ui", "client"); }

	/**
	 * The binding suffix should be blank, as we want the URLs to be as clean 
	 * as possible.  
	 *
	 * TODO: BindingSuffix should be configurable, defaulting to blank is fine.ˇ
	 */
    @Override String getBindingSuffix() { 
		""
	}
	
	@Override void init(config) {
		super.init(config)
		
		println "CONFIG!"
	}
	
/*	@Override ActionBean getActionBean(ActionBeanContext context, String binding) {
		def classes = super.getActionBeanClasses()+[Class.forName("net.sf.gripes.action.GripesLoginActionBean")]
		println "Find $binding in CLASSES ${classes}"
		classes
	}*/

	/**
	 * The url binding should be the same as the Stripes standard (e.g. the Class name
	 * less 'ActionBean'), just made lowercase. This is still overridden using the
	 * @UrlBinding annotation
	 *
	 * TODO: UrlBinding should be centrally configured
	 */
	@Override String getUrlBinding(String className) {
		String binding = super.getUrlBinding(className)
		
		logger.info "Binding $binding for action $className"
		
		binding.toLowerCase()
	}
}