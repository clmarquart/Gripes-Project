package PACKAGE.action.base

import PACKAGE.util.base.BaseActionBeanContext

import net.sf.gripes.GripesActionBean

import net.sourceforge.stripes.action.ActionBeanContext

class BaseActionBean extends GripesActionBean {
	BaseActionBeanContext context
	
	void setContext(ActionBeanContext context) { 
		this.context = (BaseActionBeanContext) context
	}	
	
	BaseActionBeanContext getContext() { 
		context
	}
}