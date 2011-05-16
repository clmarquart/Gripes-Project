package PACKAGE.action

import net.sf.gripes.GripesActionBean

import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.ForwardResolution
import net.sourceforge.stripes.action.DefaultHandler
import net.sourceforge.stripes.action.HandlesEvent
import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.validation.Validate
import PACKAGE.model.User
import PACKAGE.dao.UserDao
import net.sf.gripes.converter.PasswordTypeConverter

@UrlBinding("/user")
class UserActionBean extends GripesActionBean {
	
	String loginUrl
	String username
	
	@Validate(converter=PasswordTypeConverter.class)
	String password
	
	@HandlesEvent("signin")  Resolution signin() {
		def jsp = this.class.classLoader.getResource("jsp/login.jsp")
		def jspFile = new File(System.getProperty("gripes.temp")+"/jsp/login.jsp")
	
		if(!jspFile.exists()){
			if(!jspFile.parentFile.exists()) jspFile.parentFile.mkdirs()
		
			jspFile.createNewFile()
			jspFile.deleteOnExit()
			jspFile.text = jsp.text.replaceAll("PKG",this.class.package.name)
		}
		
		def forwardUrl = jspFile.toString().substring(jspFile.toString().indexOf("/WEB-INF"))
		new ForwardResolution(forwardUrl)
	}

	@HandlesEvent("signout")  Resolution signout() {
		context.setAttribute("gripes.user",null)
		redirect("/")
	}
	
	@HandlesEvent("authenticate") Resolution login() {
		def user = User.findByUsername(username)
		if(user && user.password.equals(password)) {
			context.setAttribute("gripes.user",user)
			redirect(loginUrl)
		} else {
			new ForwardResolution("/WEB-INF/work/jsp/login.jsp")
		}
	}
}