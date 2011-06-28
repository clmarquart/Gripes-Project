package net.sf.gripes.util

import net.sourceforge.stripes.action.ActionBeanContext
/*import org.stripesstuff.stripersist.**/
import net.sf.gripes.stripersist.Gripersist
/*import net.sf.gripes.model.GripesUser*/

public class GripesActionBeanContext extends ActionBeanContext {
	public def attributes = [:]
	public def entityClasses = [:]
	public def actionableEntities = []
	
	void setAttribute(String name, value) {
		request.session.setAttribute name, value
	}
	def getAttribute(String name) {
		request.session.getAttribute(name)
	}
	def getAttributes() {
		def map = [:]
		request.session.getAttributeNames().each{map.put(it,request.session.getAttribute(it))}
		map
	}
	
	def getEntityClasses() {
		Gripersist.findEntities().findAll{it.isAnnotationPresent(javax.persistence.Entity)}
	}
	def getActionableEntities() {
		def entities = Gripersist.findEntities().findAll{it.isAnnotationPresent(javax.persistence.Entity)}
		
		def actions = []
		entities.each {
			def klass = this.class.classLoader.findLoadedClass(it.package.name.replace("model","action")+"."+it.simpleName+"ActionBean")
			if(klass) actions.add it
		}
		actions
	}
	
/*	
	void setUser(GripesUser user) {
        request.session.setAttribute("gripes.user", user.getId())
    }
    GripesUser getUser() {
		try {
	        GripesUser.find(request.session.getAttribute("gripes.user"))	
		} catch (e) {
			null
		}
    }
*/
}