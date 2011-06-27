package net.sf.gripes.security

import java.lang.reflect.Method
import java.util.Collection
import javax.servlet.http.HttpServletResponse

import net.sourceforge.stripes.action.ActionBean
import net.sourceforge.stripes.action.ErrorResolution
import net.sourceforge.stripes.action.RedirectResolution
import net.sourceforge.stripes.action.Resolution

import org.stripesstuff.plugin.security.InstanceBasedSecurityManager
import org.stripesstuff.plugin.security.SecurityHandler

import net.sf.gripes.action.GripesActionBean
import net.sf.gripes.model.GripesRole
import net.sf.gripes.model.GripesUser

class GripesSecurityManager extends InstanceBasedSecurityManager implements SecurityHandler {
	
    @Override protected Boolean isUserAuthenticated(ActionBean bean, Method handler) {
		def user = getUser(bean)
        user && user.activated
    }
	
    @Override protected Boolean hasRoleName(ActionBean bean, Method handler, String role) {
        def user = getUser(bean)
        if (user) {
            def roles = user.getRoles()
            return (roles && roles.find{it.name==role})
        }

        false
    }

    Resolution handleAccessDenied(ActionBean bean, Method handler) {
        if (!isUserAuthenticated(bean, handler)) {
            RedirectResolution resolution = new RedirectResolution("/user/signin")

            if (bean.context.request.method.equalsIgnoreCase("GET")) {
                String loginUrl = ((GripesActionBean) bean).getLastUrl()
                resolution.addParameter("loginUrl", loginUrl)
            }

            return resolution
        }

        new ErrorResolution(HttpServletResponse.SC_UNAUTHORIZED)
    }

    def getUser(ActionBean bean) {
		((GripesActionBean) bean).context.attributes["gripes.user"]?:null
    }
}