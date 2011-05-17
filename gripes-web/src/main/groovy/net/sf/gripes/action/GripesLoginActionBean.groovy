package net.sf.gripes.action

import net.sf.gripes.action.GripesActionBean

import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.ForwardResolution
import net.sourceforge.stripes.action.DefaultHandler
import net.sourceforge.stripes.action.HandlesEvent
import net.sourceforge.stripes.action.UrlBinding

@UrlBinding("/gripeslogin")
class GripesLoginActionBean extends GripesActionBean {
	
	String username
	String password
	
	@HandlesEvent("signin")  Resolution signin() {
		println "Username: $username"
		println "Password: $password"
		
	}
	
	@DefaultHandler @HandlesEvent("authenticate") Resolution login() {
		println "LOGIN"
		def jsp = this.class.classLoader.getResource("jsp/login.jsp")
		println "JSP: " + jsp
		
		def jspFile = new File(System.getProperty("gripes.temp")+"/jsp/login.jsp")
		println "JSPFILE: " + jspFile
		
		if(!jspFile.exists()){
			if(!jspFile.parentFile.exists()) jspFile.parentFile.mkdirs()
			
			jspFile.createNewFile()
			jspFile.deleteOnExit()
			jspFile.text = jsp.text
		}
		new ForwardResolution("/WEB-INF/work/tmp/gripes/jsp/login.jsp")
	}
}