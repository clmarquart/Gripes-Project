package net.sf.gripes.util

import net.sourceforge.stripes.action.ActionBean
import net.sourceforge.stripes.action.ActionBeanContext
import net.sourceforge.stripes.controller.NameBasedActionResolver

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GripesActionResolver extends NameBasedActionResolver {
	Logger logger = LoggerFactory.getLogger(GripesActionResolver.class)
	
	/**
	 * TODO adding actions on the fly needs to happen from plugin.
	 */
	GripesActionResolver() {
		logger.info "Initializing Gripes Action Resolver"
		
		def gripesConfig = new ConfigSlurper().parse(this.class.classLoader.getResource("Config.groovy").text)
		gripesConfig.addons.each {
			def addonName = it
			def addonConfig = this.class.classLoader.getResource("gripes/addons/${addonName}/gripes.addon")
			if(!addonConfig){
				addonConfig = this.class.classLoader.getResource("gripes/gripes-addons/${addonName}/gripes.addon")
			}
			
			def addon = new ConfigSlurper().parse(addonConfig)
			addon.actions.each {
				super.addActionBean(Class.forName(it))
			}
		}
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
	 * TODO: BindingSuffix should be configurable, defaulting to blank is fine.
	 */
    @Override String getBindingSuffix() { 
		""
	}
	
	@Override void init(config) {
		super.init(config)
	}
	
	/*	
	@Override ActionBean getActionBean(ActionBeanContext context, String binding) {
		def classes = super.getActionBeanClasses()+[Class.forName("net.sf.gripes.action.GripesLoginActionBean")]
		println "Find $binding in CLASSES ${classes}"
		classes
	}
	*/

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