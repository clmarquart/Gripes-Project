package net.sf.gripes.db

import org.stripesstuff.stripersist.StripersistInit

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GripesStripersistInit implements StripersistInit {
	Logger logger = LoggerFactory.getLogger(GripesStripersistInit.class)
	
	/**
	 * Initalizing the Gripes implementation of the StripersistInit interface.  Currently, 
	 * this only functions as a way to bootstrap an application with data.  It looks for
	 * and loads a resource named `import.groovy`.  The `import.groovy` script must be 
	 * located in the root of the resources directory.
	 * 
	 * TODO Provide proper error checking for the import of data.
	 */
	void init() {
		logger.info "Gripes Stripersist Initialization."
		
/*		String importScript = this.class.classLoader.getResource("import.groovy").text
		
		GroovyShell shell = new GroovyShell()
		Object value = shell.evaluate(importScript)*/
	}
}