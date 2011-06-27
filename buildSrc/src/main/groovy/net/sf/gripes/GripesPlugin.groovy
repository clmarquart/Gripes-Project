import net.sf.gripes.*

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.tasks.SourceSet
import javax.persistence.Column

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This is the heart of creating a Gripes application.
 * 
 * Creates the following tasks: init, setup, create, run, stop, and delete
 * 
 * TODO hook into War task to ensure correct packaging	
 * TODO Create GripesException class for handling sequence errors
 */
class GripesPlugin implements Plugin<Project> {
	Logger logger = LoggerFactory.getLogger(GripesPlugin.class)
	
	String root
	String basePackage
	String tmpDir
	
	GripesPlugin() {}
	
	/**
	 * Create the tasks for the plugin
	 */
	def void apply(Project project) {
        project.convention.plugins.gripes = new GripesPluginConvention()

		def deleteTask = project.task('delete') << {
			GripesCreate creator = new GripesCreate([project: project])
			creator.delete()
		}
		
		def initTask = project.task('init') << {
			GripesCreate creator = new GripesCreate([project: project])
			creator.init()
		}
		
		def setupTask = project.task('setup') << {
			if(new File("src").exists()) {
				GripesCreate creator = new GripesCreate([project: project])
				creator.setup()
			} else {
				logger.error "GripesSequenceError: You must first run `gradle init`."
				return null
			}
		}
		
		def createTask = project.task('create') << {
			if(!(new File("resources/Config.groovy").exists())) {
				logger.error "GripesSequenceError: You must first run `gradle init` and `gradle setup`."
				return null
			}
			GripesCreate creator = new GripesCreate([project: project])

			if(project.hasProperty('model')) {
				creator.model(project.properties.model)
			} else if (project.hasProperty('action')) {
				creator.action(project.properties.action)
			} else if (project.hasProperty('views')) {
				creator.views(project.properties.views)
			}
		}
		createTask.dependsOn<<project.compileGroovy

		/**
		 * Runs the Gripes application using the built-in Gradle Jetty 
		 * implementation. 
		 */
		def runTask = project.task('run') << {task->
			if(!(new File("resources/Config.groovy").exists())) {
				logger.error "GripesSequenceError: You must first run `gradle init` and `gradle setup`."
				return null
			}
						
			def jetty = new GripesJetty(project: project)
			
			jetty.webAppSourceDirectory = new File(project.projectDir.canonicalPath+"/web")
			jetty.start()
		}
		runTask.dependsOn<<project.compileGroovy
		runTask.configure {
			def configFile = new File("resources/Config.groovy")
			if(configFile.exists()) {
				def gripesConfig = new ConfigSlurper().parse(configFile.text)
				gripesConfig.addons.each {
					logger.debug "Adding: gripes-addons/{}/src/main/groovy to the sourceSet", it.replaceAll('-src','')
					project.sourceSets.main.groovy.srcDirs += new File("gripes-addons/${it.replaceAll('-src','')}/src/main/groovy")
				}
			}
		}
		
		def stopTask = project.task('stop') << {
			project.convention.plugins.gripes.server.each {k,v->
				project.jettyStop[k] = v
			}
			project.jettyStop.execute()
		}
		
		
		def installTask = project.task('install') << {
			if(!(new File("resources/Config.groovy").exists())) {
				logger.error "GripesSequenceError: You must first run `gradle init` and `gradle setup`."
				return null
			}
			
			GripesCreate creator = new GripesCreate([project: project])
			creator.install(project.properties.addon)
		}
    }

	private def copyWebXml(project) {
	    def webXmlText = getResource("web.xml").text
 		def webXml = new File(GripesUtil.getTempDir(project).canonicalPath+"/web.xml")
		webXml.createNewFile()
		webXml.deleteOnExit()
		webXml.text = webXmlText.replaceAll("PACKAGE",GripesUtil.getSettings(project).packageBase)
		
		webXml
	}
	
	private def getResource(resource) {
		getClass().classLoader.getResource(resource)
	}
}