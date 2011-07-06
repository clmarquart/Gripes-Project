package net.sf.gripes

import org.junit.After
import org.junit.Before

import net.sourceforge.stripes.controller.DispatcherServlet
import net.sourceforge.stripes.controller.StripesFilter
import net.sourceforge.stripes.mock.*

import net.sf.gripes.stripersist.Gripersist

class GripesBaseTest {	
	
	MockServletContext context = new MockServletContext("test")
	
	@Before void setupTests() {
		// Add the Stripes Filter
		Map<String,String> filterParams = new HashMap<String,String>()
		filterParams.put("ActionResolver.Packages", "net.sf.gripes.action")
		filterParams.put("Extension.Packages", "net.sf.gripes.stripersist")
		filterParams.put("StripersistInit.Classes", "net.sf.gripes.db.GripesStripersistInit")
		
		context.addFilter(StripesFilter.class, "StripesFilter", filterParams)

		// Add the Stripes Dispatcher
		context.setServlet(DispatcherServlet.class, "StripesDispatcher", null);
		
		Gripersist.requestInit()
	}
	
	@After void TearDown() {
		Gripersist.requestComplete()
	}
}