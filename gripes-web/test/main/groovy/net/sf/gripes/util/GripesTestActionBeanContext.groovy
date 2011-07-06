package net.sf.gripes.util

import net.sourceforge.stripes.action.ActionBeanContext

public class MyTestActionBeanContext extends ActionBeanContext {
    Map<String,Object> fakeSession = new HashMap<String,Object>()

}