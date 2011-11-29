package net.sf.gripes.util.jetty

class JettyContextHelper {
	def servletContext
	
	public JettyContextHelper(def context) {
		this.servletContext = context
	}
	
	def getFilter(def key){
		servletContext.contextHandler.servletHandler.getFilter(key)
	}
	
	void addFilter(def holder, def mapping) {
		servletContext.contextHandler.servletHandler.addFilter(holder, mapping)
	}
}