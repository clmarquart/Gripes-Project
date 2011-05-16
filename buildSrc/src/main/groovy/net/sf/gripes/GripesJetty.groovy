package net.sf.gripes

/*import org.mortbay.jetty.NCSARequestLog*/

class GripesJetty {
	def project
	def webXml
	def webAppSourceDirectory
	def httpPort = 8888
	def stopPort = 8889
	def stopKey  = "stopJetty"
	def contextPath = "/gripes"
	def scanIntervalSeconds = 1
	def scanTargets
	
	
	def start(config) {
		[
			"webXml",
			"webAppSourceDirectory",
			"httpPort",
			"stopPort",
			"stopKey",
			"contextPath",
			"scanIntervalSeconds"
		].each {
			project.jettyRun[it] = GripesUtil.getSettings(project).server."$it"
		}
		
		project.jettyRun.scanTargets = [
			new File(GripesUtil.getRoot(project)+"/build/classes/main"),
			new File(GripesUtil.getRoot(project)+"/buildSrc/build/classes/main")
		]
		
		def dbConfig = new ConfigSlurper().parse(new File('conf/DB.groovy').toURL())
		
		def jpaTemplate = getResource("conf/persistence.template").text
		jpaTemplate = jpaTemplate
						.replaceAll(/\[DBSCHEMA\]/,dbConfig.database.schema)
						.replaceAll(/\[DBDIALECT\]/,dbConfig.database.dialect)
						.replaceAll(/\[DBDRIVER\]/,dbConfig.database.driver)
						.replaceAll(/\[DBURL\]/,dbConfig.database.url)
						.replaceAll(/\[DBUSER\]/,dbConfig.database.user)
						.replaceAll(/\[DBPASSWORD\]/,dbConfig.database.password)
						
		[
			new File("build/classes/main/META-INF/"), 
			new File(project.jettyRun['webAppSourceDirectory'].canonicalPath+"/WEB-INF/classes/")
		].each {
			it.mkdirs()
			it.deleteOnExit()
		}
		
		def jpaFile = new File("build/classes/main/META-INF/persistence.xml")
		jpaFile.createNewFile()
		jpaFile.deleteOnExit()
		jpaFile.text = jpaTemplate
		
		def boot = new File(project.jettyRun['webAppSourceDirectory'].canonicalPath+"/WEB-INF/classes/import.groovy")
		boot.createNewFile()
		boot.deleteOnExit()
		boot.text = new File(GripesUtil.getResourceDir(project)+"/import.groovy").text
		
		def props = new File(project.jettyRun['webAppSourceDirectory'].canonicalPath+"/WEB-INF/classes/StripesResources.properties")
		props.createNewFile()
		props.deleteOnExit()
		props.text = new File(GripesUtil.getResourceDir(project)+"/StripesResources.properties").text
		
		/*		
		def importSQL = new File(project.jettyRun['webAppSourceDirectory'].canonicalPath+"/WEB-INF/classes/import.sql")
		importSQL.createNewFile()
		importSQL.deleteOnExit()
		importSQL.text = new File(GripesUtil.getResourceDir(project)+"/import.sql").text
		
		def logback = new File(project.jettyRun['webAppSourceDirectory'].canonicalPath+"/WEB-INF/classes/logback.groovy")
		logback.createNewFile()
		logback.deleteOnExit()
		logback.text = new File(GripesUtil.getResourceDir(project)+"/logback.groovy").text*
		
		logback = new File(project.jettyRun['webAppSourceDirectory'].canonicalPath+"/WEB-INF/classes/log4j.properties")
		logback.createNewFile()
		logback.deleteOnExit()
		logback.text = new File(GripesUtil.getResourceDir(project)+"/log4j.properties").text
		*/
		
		// Call createPersistenceXml() and create it from the 
		// template, with the proper configuration
		// createPersistenceXml()
		
		project.jettyRun.execute()
	}
	
	def stop() {
		
	}
	
	def createPersistenceXml(){
		
	}

	private def getResource(resource) {
		getClass().classLoader.getResource(resource)
	}
}