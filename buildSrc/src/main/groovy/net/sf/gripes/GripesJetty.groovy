package net.sf.gripes

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
		
		def ant = new AntBuilder()
		def dbConfig = new ConfigSlurper().parse(new File('resources/DB.groovy').toURL())
		def gripesConfig = new ConfigSlurper().parse(new File('resources/Config.groovy').toURL())
		
		def jpaTemplate = """
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd" 
	version="1.0">
		"""
		dbConfig.database.each { k,v ->
			jpaTemplate += getResource("conf/persistence.template").text
			jpaTemplate = jpaTemplate
							.replaceAll(/\[NAME\]/,k)
							.replaceAll(/\[DBSCHEMA\]/,v.schema)
							.replaceAll(/\[DBDIALECT\]/,v.dialect)
							.replaceAll(/\[DBDRIVER\]/,v.driver)
							.replaceAll(/\[DBURL\]/,v.url)
							.replaceAll(/\[DBUSER\]/,v.user)
							.replaceAll(/\[DBPASSWORD\]/,v.password)
						
		    if(v.classes.equals("auto")) {
				jpaTemplate = jpaTemplate
								.replaceAll(/\[AUTO\]/,'<property name="hibernate.archive.autodetection" value="class"/>')
								.replaceAll(/\[CLASSES\]/,'')
			} else {
				jpaTemplate = jpaTemplate
								.replaceAll(/\[AUTO\]/,'')
								.replaceAll(/\[CLASSES\]/, v.classes.collect{"<class>$it</class>"}.join("\n"))
			}	
		}
		jpaTemplate += "\n</persistence>\n"
						
		[
			new File("build/classes/main/META-INF/"), 
			new File(project.jettyRun['webAppSourceDirectory'].canonicalPath+"/WEB-INF/classes/gripes/addons")
		].each {
			it.mkdirs()
			it.deleteOnExit()
		}
		
		def jpaFile = new File("build/classes/main/META-INF/persistence.xml")
		jpaFile.createNewFile()
		jpaFile.deleteOnExit()
		jpaFile.text = jpaTemplate
		
		
		[
			"import.groovy",
			"StripesResources.properties",
			"DB.groovy",
			"Config.groovy"
		].each {
			def newFile = new File(project.jettyRun['webAppSourceDirectory'].canonicalPath+"/WEB-INF/classes/${it}")
			newFile.createNewFile()
			newFile.deleteOnExit()
			newFile.text = new File(GripesUtil.getResourceDir(project)+"/${it}").text
		}
		
		gripesConfig.addons.each {
			try {
				def addonDir = ((it=~/-src$/).find())?(new File("gripes-addons/${it.replaceFirst('-src','')}")):(new File("addons/${it}"))
				ant.copy(
					file: addonDir.canonicalPath+"/gripes.addon",
					tofile: project.jettyRun['webAppSourceDirectory'].canonicalPath+"/WEB-INF/classes/gripes/gripes-addons/${it}/gripes.addon"
				)

				if(addonDir.canonicalPath.toString().startsWith("addons")) {
					addonDir.eachFileRecurse {
						def dest
						if(it.name.endsWith(".jar"))
							dest = project.jettyRun['webAppSourceDirectory'].canonicalPath+"/WEB-INF/lib/${it.name}"
						else
							dest = project.jettyRun['webAppSourceDirectory'].canonicalPath+"/WEB-INF/classes/gripes/${it}"
				
						dest = new File(dest)
						if(it.isFile()){
							ant.copy(
								file: it, 
								tofile: dest.canonicalPath
							)
							dest.deleteOnExit()	
						}
					}
				}
			}catch(e){
				e.printStackTrace()
			}
		}

		def webXmlTemplate = getResource("web.xml").text
		def webXml = new File(project.jettyRun['webAppSourceDirectory'].canonicalPath+"/WEB-INF/web.xml")
		webXml.createNewFile()
		webXml.deleteOnExit()
		webXml.text = webXmlTemplate
						.replaceAll("PROJECTNAME",GripesUtil.getSettings(project).appName)
						.replaceAll("PACKAGE",GripesUtil.getSettings(project).packageBase)
						
						/*
						.replaceAll(/\[PLUGINS\]/,",\n\t"+GripesUtil.getSettings(project).addons.join(",\n"))
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