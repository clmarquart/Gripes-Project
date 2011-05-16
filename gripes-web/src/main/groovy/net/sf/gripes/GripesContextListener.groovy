package net.sf.gripes

import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GripesContextListener  implements ServletContextListener {
	Logger logger = LoggerFactory.getLogger(GripesContextListener.class)
	
	ServletContext context
	
	void contextInitialized(ServletContextEvent contextEvent) {
		logger.info "Loading the Gripes Application..."
		context = contextEvent.getServletContext()
		
		def pack = context.getInitParameter("GripesPackage")+".model"
		(new File(this.class.classLoader.getResource(pack.replace(".","/")).getFile())).listFiles().each{
			if(it.isFile()) {
				def klass = Class.forName(pack.replace("/",".")+"."+it.name.replace(".class",""))
				if(klass && klass.getAnnotation(javax.persistence.Entity)){
					klass.metaClass.static.methodMissing = {String name, args ->
						klass.newInstance().methodMissing(name, args)
					}
				}
			}
		}
		
		def tempStr
		try {
			tempStr = context.TEMPDIR
		} catch(e) {
			tempStr = context.getRealPath("/")+"/WEB-INF/work"
		}
		
		def tempDir = new File("${tempStr}")
		if(!tempDir.exists()){
			tempDir.mkdirs()
			tempDir.deleteOnExit()
		}
		System.setProperty("gripes.temp", tempDir.toString())
	}
	
	void contextDestroyed(ServletContextEvent contextEvent) {
		context = contextEvent.getServletContext()
		
		logger.info "Gripes Application Shutdown."
	}
}