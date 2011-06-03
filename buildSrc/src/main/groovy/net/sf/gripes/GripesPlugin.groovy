import net.sf.gripes.*

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.tasks.SourceSet
import javax.persistence.Column

/**
 * This is the heart of creating a Gripes application.
 * 
 * Creates the following tasks: init, setup, create, run, stop, and delete
 */
class GripesPlugin implements Plugin<Project> {
	String root
	String basePackage
	String tmpDir
	
	GripesPlugin() {}
	
	/**
	 * Create the tasks for the plugin
	 */
	def void apply(Project project) {
        project.convention.plugins.gripes = new GripesPluginConvention()

		project.beforeEvaluate {
			println "BEFORE"
		}

		def deleteTask = project.task('delete') << {
			GripesCreate creator = new GripesCreate([project: project])
			creator.delete()
		}
		
		def initTask = project.task('init') << {
			GripesCreate creator = new GripesCreate([project: project])
			creator.init()
		}
		
		def setupTask = project.task('setup') << {
			GripesCreate creator = new GripesCreate([project: project])
			creator.setup()
		}
		
		def createTask = project.task('create') << {			
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
		 *
		 * TODO: Stop calling copyWebXml(), web.xml should be created once on `setup`.
		 */
		def runTask = project.task('run') << {task->			
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
					println "Adding: gripes-addons/${it}/src/main/groovy to the sourceSet"
					project.sourceSets.main.groovy.srcDirs += new File("gripes-addons/${it}/src/main/groovy")
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