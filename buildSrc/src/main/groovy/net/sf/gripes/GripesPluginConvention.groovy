package net.sf.gripes

class GripesPluginConvention {
	String appName
	String src
	String resources
	String packageBase
	String httpPort
	String stopPort
	String webappSource
	def server

    def gripes(Closure closure) {
        closure.delegate = this
        closure() 
    }
}