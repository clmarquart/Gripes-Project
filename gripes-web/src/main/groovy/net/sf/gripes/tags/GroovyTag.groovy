package net.sf.gripes.tags

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport

import org.slf4j.Logger;
import org.slf4j.LoggerFactory 

class GroovyTag extends BodyTagSupport {
	Logger logger = LoggerFactory.getLogger(GroovyTag.class)

	def klass
	def method
	def attrs
	def body
	def presetOut
	
	@Override int doEndTag() {
		def out
		if (getPresetOut()==null) {
			out = pageContext.getOut()
		} else { 
			out = getPresetOut()
		}
		
		if(getKlass()) {
			def loadedClass
			
			if(getKlass() instanceof Class) {
				logger.debug "Class: {}", getKlass()
				loadedClass = getKlass().newInstance()
			} else {
				try {
					logger.debug "Class from String: {}", getKlass()
					loadedClass = this.class.classLoader.loadClass(getKlass()).newInstance()
				} catch (e) {
					logger.warn "Could not load ${getKlass()}. Must either be full classname or the class itself"
				}
			}
			logger.debug "Loaded class: {}", loadedClass
			if(loadedClass && getMethod()) {
				logger.debug "Calling method: {}", getMethod()
				loadedClass."${getMethod()}".call([out: out, attrs: getAttrs(), body: getTagBodyContents()])		
				
				return BodyTagSupport.EVAL_PAGE
			} else {
				return BodyTagSupport.SKIP_PAGE
			}
		} else if (getMethod()) {
			def lastDot = getMethod().lastIndexOf(".")
			def klassName = getMethod().substring(0,lastDot)
			def methodName = getMethod().substring(lastDot+1)
			
			this.class.classLoader.loadClass(klassName).newInstance()."${methodName}".call([out: out, attrs: getAttrs(), body: getTagBodyContents()])
				
			return BodyTagSupport.EVAL_PAGE
		} else {
			if(getTagBodyContents()) {
				def result = BaseGroovyTag.executeBodyTag(getAttrs(), getTagBodyContents(), out)
				result.run()
				return BodyTagSupport.EVAL_PAGE
			} else {
				logger.warn "Could not figure out what to do"
			}
		}
		
		BodyTagSupport.SKIP_PAGE
	}
	
	String getTagBodyContents() {
		try {
			getBodyContent().getString()
		} catch (e) {
			null
		}
	}
}