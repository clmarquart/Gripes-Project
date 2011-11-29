package net.sf.gripes.util.tomcat

class TomcatContextHelper {
	def servletContext
	
	public TomcatContextHelper(def context){
		this.servletContext = context
	}
	
	def getFilter(def key){
		context.contextHandler.servletHandler.getFilter(key)
	}
}