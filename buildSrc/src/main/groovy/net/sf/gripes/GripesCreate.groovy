import javax.persistence.Column

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GripesCreate {
	Logger logger = LoggerFactory.getLogger(GripesCreate.class)
	def project
	def urlLoader
	
	/**
	 * Tear down the Gripes application. 
	 *
	 * TODO: Probably don't need this in later versions.  Nice for testing app creation.
	 */
	def delete() {
		logger.info "Tearing down the Gripes project"
		
		new AntBuilder().sequential {
			delete(dir: "conf")
			delete(dir: "src")
			delete(dir: "resources")
			delete(dir: "web")
			delete(dir: "addons")
		}
	}
	
	/**
	 * Initializes a directory as a Gripes application.  Creates the folder structure,
	 * gripes-basic` gradle script, and edits the build.gradle to use gripes-basic
	 */
	def init() {
		logger.info "Initializing Gripes project"

		['src','resources','web','conf'].each {
			def dir = new File("${it}")
			if(!dir.exists()){
				dir.mkdirs()
			}
		}
		
		['conf/gripes-basic.gradle','conf/gripes.properties'].each {
			def newFile = new File("${it}")
			if(!newFile.exists()) {
				newFile.createNewFile()
				def tempFile = getResource("${it}")
				if(tempFile) {
					newFile.text = tempFile.text
				}
			}
		}
	}
	
	/**
	 * Called after the application has been initialized.  Creates the necessary 
	 * packages and base models, from the gripes{} settings in the build script.
	 *
	 * TODO: Restructure the copying of files, seems rather messy and could be cleaned up
	 */ 
	def setup() {
		logger.info "Setting up Gripes project"
		
		[
			'resources/DB.groovy','resources/Config.groovy',
			'resources/StripesResources.properties',
			'resources/logback.groovy',
			'web/index.jsp'
		].each {
			def newFile = new File("${it}")
			if(!newFile.exists()) {
				newFile.createNewFile()
				def tempFile = getResource("${it}")
				logger.debug "Creating: ${it} from ${tempFile}"
				if(tempFile)
					newFile.text = tempFile.text.replace("PACKAGE",GripesUtil.getSettings(project).packageBase)	
			}
		}
		
		// This is the META-INF for the persistence.xml file in the source folder
		['META-INF'].each {
			def dir = new File(GripesUtil.getSourceDir(project)+"/${it}")
			if(!dir.exists()){
				dir.mkdirs()
			}
		}
		
		['action/base','model/base','util','dao/base'].each {
			def dir = new File(GripesUtil.getBasePackage(project)+"/${it}")
			if(!dir.exists()){
				dir.mkdirs()
			}
		}
		
		[
		 'style',
		 'script',
		 'images',
		 'WEB-INF/jsp/layout',
		 'WEB-INF/jsp/includes',
		 'WEB-INF/classes',
		 'WEB-INF/work',
		 'META-INF'
		].each {
			def dir = new File(GripesUtil.getRoot(project)+"/web/${it}")
			if(!dir.exists()){
				dir.mkdirs()
			}
		}
		
		// Reference for file creation
		def dir = new File(GripesUtil.getRoot(project)+"/web/WEB-INF/jsp/")
		
		[
			"layout/main.jsp",
			"includes/taglibs.jsp",
			"includes/_adminBar.jsp"
		].each {
			saveFile(new File(dir.canonicalPath+"/${it}"),getResource("templates/jsp/${it}").text)
		}
		
		[
			"web/style/main.css",
			"web/script/jquery.js",
			"web/script/main.js",
			"resources/StripesResources.properties",
			"resources/import.groovy"
		].each {
			saveFile(new File(GripesUtil.getRoot(project)+"/${it}"),getResource(it).text)
		}
		
		[
			"dao/base/BaseDao.groovy",
			"action/base/BaseActionBean.groovy",
			"util/base/BaseActionBeanContext.groovy",
			"model/base/BaseModel.groovy"
		].each {
			saveFile(
				new File(GripesUtil.getBasePackage(project)+"/${it}"),
				getResource	("templates/${it}").text
					.replaceAll("PACKAGE",GripesUtil.getSettings(project).packageBase)
					.replaceAll("PKG","PACKAGE")
			)
		}
/*		
		saveFile(
			new File(GripesUtil.getRoot(project)+"/web/WEB-INF/web.xml"),
			getResource("web.xml").text.replaceAll("PACKAGE",GripesUtil.getSettings(project).packageBase)
		)
*/
	}
	
	/**
	 * Creates a JPA Entity model object
	 * 
	 * TODO Check for packageBase from command line, then use default
	 * TODO Fix GripesUtil call to get pkg folder, need to use cmd line
	 */
	def model(name, pkg) {
		logger.info "Creating {} Model in Package: {}", name, pkg
/*		GripesUtil.getSettings(project).packageBase = pkg?:GripesUtil.getSettings(project).packageBase*/
		
		def file, template

		template = getResource("templates/model/Model.template").text
						.replaceAll("MODEL",name)	
						.replaceAll("BASEPACKAGE",GripesUtil.getSettings(project).packageBase)
						.replaceAll("PACKAGE",pkg?:GripesUtil.getSettings(project).packageBase)
		file = new File(GripesUtil.packageToDir(project,pkg?:GripesUtil.getSettings(project).packageBase)+"/model/${name}.groovy")
		saveFile(file, template)
		
		template = getResource("templates/dao/Dao.template").text
						.replaceAll("MODEL",name)
						.replaceAll("BASEPACKAGE",GripesUtil.getSettings(project).packageBase)
						.replaceAll("PACKAGE",pkg?:GripesUtil.getSettings(project).packageBase)
		file = new File(GripesUtil.packageToDir(project,pkg?:GripesUtil.getSettings(project).packageBase)+"/dao/${name}Dao.groovy")		
		saveFile(file,template)
	}
	
	/**
	 * Creates a Stripes ActionBean for a previously created Model
	 *
	 * TODO Check for packageBase from command line, then use default
	 */
	def action(name, pkg) {
		logger.info "Creating {} ActionBean in Package: {}", name, pkg
/*		GripesUtil.getSettings(project).packageBase = pkg?:GripesUtil.getSettings(project).packageBase*/
		
		def file, template
		
		template =  getResource("templates/action/ActionBean.template").text
						.replaceAll("MODELL",name.toLowerCase())
						.replaceAll("MODEL_FIELD",name+" "+name.toLowerCase())
						.replaceAll("MODEL",name)
						.replaceAll("BASEPACKAGE",GripesUtil.getSettings(project).packageBase)
						.replaceAll("PACKAGE",pkg?:GripesUtil.getSettings(project).packageBase)
						
		file = new File(GripesUtil.packageToDir(project,pkg?:GripesUtil.getSettings(project).packageBase)+"/action/${name}ActionBean.groovy")
		saveFile(file, template)
		
		
		def gripesProps = new Properties()
		new File("conf/gripes.properties").withInputStream { 
		  stream -> gripesProps.load(stream) 
		}
		
		if(gripesProps["actions"] && (gripesProps["actions"].indexOf("${pkg}.action") < 0))
			gripesProps["actions"] = (gripesProps['actions']+","+pkg+".action")
		else if (!gripesProps["actions"])
			gripesProps["actions"] = pkg+".action"
			
		gripesProps.store(new FileOutputStream(new File("conf/gripes.properties")), null)
		
		urlLoader = (URLClassLoader) this.class.classLoader
		urlLoader.addURL(new File("build/classes/main/").toURL())
		urlLoader.addURL(new File("gripes-web/build/classes/main/").toURL())
		
		createViews(name, urlLoader.findClass("${pkg?:GripesUtil.getSettings(project).packageBase}.model.${name}"), pkg)	
	}
	
	/**
	 * Creates the views for an ActionBean.  Can be called directly if the views need
	 * to be recreated.
	 */
	def views(name,pkg) {
		logger.info "Creating Views for the {}ActionBean in Package: {}", name, pkg
		GripesUtil.getSettings(project).packageBase = pkg?:GripesUtil.getSettings(project).packageBase
		
		urlLoader = (URLClassLoader) this.class.classLoader
		urlLoader.addURL(new File("build/classes/main/").toURL())				
		
		createViews(name, urlLoader.findClass("${GripesUtil.getSettings(project).packageBase}.model.${name}"), pkg)
	}
	
	/**
	 * Install the specified add-on.  
	 * 
	 * TODO Verify use of both source and jar addons, spec. the installer 
	 */
	def install(addon) {
		def addonName = addon
		logger.info "Installing the {} add-on.", addon
		
		def addonConfig, installScript = "", installScriptFile, installScriptResource
		if((addon=~/-src/).find()){
			addon = addon.replaceFirst(/-src/,'')
			addonConfig = new ConfigSlurper().parse(new File("gripes-addons/${addon}/gripes.addon").text)
			
			installScriptFile = new File("gripes-addons/${addon}/gripes.install")
			
			if(installScriptFile.exists()) installScript = installScriptFile.text
		} else {		
			makeDir(new File("addons/${addon}"))
			
			if(!new File("addons/${addon}/gripes.install").exists()) {
				logger.info "Downloading the addon ${addon}"
				download(addon)	
			}
			
			installScriptResource = new File("addons/${addon}/gripes.install") //getResource("addons/${addon}/gripes.install")
			logger.info "InstallScript: {}", installScriptResource
			if(installScriptResource) installScript = installScriptResource.text
			else installScript = new File("addons/${addon}/gripes.install").text
		}
		
		if(installScript!=""){
			URLClassLoader childLoader = new URLClassLoader ([new File("addons/${addon}/bin/${addon}.jar").toURL()] as URL[], this.class.classLoader);
			
			logger.info "Executing the ${addon} install script"
			new GroovyShell(childLoader,new Binding([project: project])).evaluate(installScript)
		}
		
		def gripesConfigFile = new File("resources/Config.groovy")
		def gripesConfig = gripesConfigFile.text
		if((gripesConfig =~ /addons\s*=\s*\[\]/).find()){
			gripesConfig = gripesConfig.replaceFirst(/addons\s*=\s*\[\s*\]/,'addons=["'+addonName+'"]')
		} else {
			gripesConfig = gripesConfig.replaceFirst(/addons\s*=\s*\[/,'addons=["'+addonName+'",')
		}
		gripesConfigFile.text = gripesConfig
	}
	
	private def download(addon) {
		[
		 "addons/${addon}/bin"
		].each {
			def dir = new File("${it}")
			if(!dir.exists()) dir.mkdirs()
		}
		def fos, out, file
		["gripes.addon","gripes.install"].each {
			def location = "addons/${addon}/${it}"
				
		    fos = new FileOutputStream(location)		
			
		    out = new BufferedOutputStream(fos)
		    out << new URL("http://www.gripes-project.org/addons/${addon}/${it}").openStream()
		    out.close()	
		}
	
	    file = new FileOutputStream("addons/${addon}/bin/${addon}.jar")
	    out = new BufferedOutputStream(file)
	    out << new URL("http://www.gripes-project.org/addons/${addon}/bin/${addon}.jar").openStream()
	    out.close()
	}
	
	private def makeDir(parentFile) {
		if(!parentFile.exists()){
			parentFile.mkdirs()
		}
	}
	
	private def saveFile(file,template) {
		logger.info "Saving {}", file
		logger.info "setting text: {}", template.length()
		
		makeDir(file.parentFile)
		if(!file.exists()) {
			file.createNewFile()
			file.text = template
		}
	}

	private def createViews(action, model,pkg) {
		def fields = model.declaredFields.findAll { !it.isSynthetic() } // && it.getAnnotation(Column.class) }

		[new File(GripesUtil.getRoot(project)+"/web/WEB-INF/jsp/${action.toLowerCase()}")].each{
			if(!it.exists()){it.mkdirs()}
		}
		
		def jspdir = new File(GripesUtil.getRoot(project)+"/web/WEB-INF/jsp/${action.toLowerCase()}")
		
		["view"].each {
			def file = new File(jspdir.canonicalPath+"/${it}.jsp")
			def template = getResource("templates/jsp/${it}.template").text
			def newContents = ""
			fields.each {
				newContents+=template.replace("LABEL",it.name)
								.replace("VALUE",getViewValue(model,it))
								/*								
								.replace("TYPE",getFieldType(it))
								.replace("MODEL",model.simpleName)
								*/
			}			
			file.createNewFile()
			file.text =  createJspTemplate(model,newContents,"list,create","View", pkg)
		}
		["edit","create"].each {
			def file = new File(jspdir.canonicalPath+"/${it}.jsp")
			def template = getResource("templates/jsp/${it}.template").text
			def newContents = ""			
			fields.each {
				newContents+=template.replace("LABEL",it.name)
								.replace("VALUE",'${bean.'+model.simpleName.toLowerCase()+"."+it.name+'}')
								.replace("TYPE",getFieldType(it))
								.replace("MODEL",model.simpleName.toLowerCase())
								.replace("INPUT",createInputField(model,it))
			}
			newContents =  """
	<stripes:form beanclass="${pkg?:GripesUtil.getSettings(project).packageBase}.action.${model.simpleName}ActionBean">
		${newContents}
"""			
			newContents+= '<stripes:hidden name="'+model.simpleName.toLowerCase()+'" value="${bean.'+model.simpleName.toLowerCase()+'.id}" />'
			newContents+= """
		<stripes:submit name="save" value="Save" />
	</stripes:form>
"""			
			file.createNewFile()
			file.text =  createJspTemplate(model,newContents,"list",it, pkg)
		}
		["list"].each {
			def newContents = ""
			def file = new File(jspdir.canonicalPath+"/${it}.jsp")
			def template = getResource("templates/jsp/${it}.template").text
			
			newContents += template
							.replace("MODEL",model.simpleName)
							.replace("BEANLIST",'${'+"requestScope['list']"+'}')
							.replace("LISTHEADER",getTableHeader(fields))
							.replace("LISTENTRY",getTableRow(fields,model))
			def layout = createJspTemplate(model,newContents,"create","List", pkg)
			file.createNewFile()
			file.text = layout
		}
	}
	
	private def getViewValue(model,field) {
		def html = ""
		
		if(isEntity(field)) {
			if(isCollection(field)){
				html += '<c:forEach items="${bean.'+model.simpleName.toLowerCase()+"."+field.name+'}" var="it">\n'
				html += '\t<stripes:link href="/${fn:toLowerCase(it.class.simpleName)}/view?${fn:toLowerCase(it.class.simpleName)}=${it.id}">${it}</stripes:link>\n'
				html += '</c:forEach>\n'
			} else {
				html += '\t<stripes:link href="/'+field.type.simpleName.toLowerCase()+'/view?'+field.type.simpleName.toLowerCase()+'=${bean.'+model.simpleName.toLowerCase()+'.'+field.name+'.id}">${bean.'+model.simpleName.toLowerCase()+'.'+field.name+'}</stripes:link>\n'	
			}
		} else {	
			html += '${bean.'+model.simpleName.toLowerCase()+"."+field.name+'}'
		}
		html
	}
	
	private def createInputField(model,field) {	
		def html=""
		if(!field.type.isPrimitive()){
			if(field.type.equals(String) || (field.type.superclass.equals(Number))) {
				html = '<input type="text" value="${bean.'+model.simpleName.toLowerCase()+'.'+field.name+'}" name="'+model.simpleName.toLowerCase()+'.'+field.name+'" />'
			} else if(field.type.getAnnotation(javax.persistence.Entity)) {
				html = '<stripes:link href="/'+field.type.simpleName.toLowerCase()+'/edit?'+field.type.simpleName.toLowerCase()+'=${bean.'+model.simpleName.toLowerCase()+'.'+field.name+'.id}">Link</stripes:link>'
			} else if(field.type.interfaces.find{it.equals(java.util.Collection)}){
				html += '<c:forEach items="${bean.'+model.simpleName.toLowerCase()+"."+field.name+'}" var="it">\n'
				html += '\t<stripes:link href="/${fn:toLowerCase(it.class.simpleName)}/view?${it.class.simpleName}=${it.id}">${it}</stripes:link>\n'
				html += '</c:forEach>\n'
			}			
		} else {
			if(field.type.equals(boolean)) {	
				html = '<input type="checkbox" value="1" ${(bean.'+model.simpleName.toLowerCase()+'.'+field.name+'==true)?"checked=\'checked\'":""} name="'+model.simpleName.toLowerCase()+'.'+field.name+'" />'
			}
		}

		html
	}
	
	private def createJspTemplate(model,newContents,adminBar,action,pkg) {
		def str = """
<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>
<stripes:useActionBean id="bean" beanclass="${pkg?:GripesUtil.getSettings(project).packageBase}.action.${model.simpleName}ActionBean"/>
<stripes:layout-render name='../layout/main.jsp' pageTitle='${model.simpleName} ${action}'>
"""
		str +=  '\t<stripes:layout-component name="adminBar">\n'
		str += 	'\t\t<stripes:layout-render name="../includes/_adminBar.jsp" bean="${bean}" links="'+adminBar+'" />\n'
		str +=  '\t</stripes:layout-component>\n'
		str += """
	<stripes:layout-component name="content">
		${newContents}
	</stripes:layout-component>
</stripes:layout-render>
"""
		str
	}
	
	private def getTableRow(fields,model) {
		def header = "<tr>\n"
		fields.each {
			header += "\t\t\t\t<td>\n"
			if(!it.type.interfaces.find{it.equals(java.util.Collection)}){
				header += '\t\t\t\t\t${item.'+it.name+"}\n"	
			}
			header += "\t\t\t\t</td>\n"
		}
		header += "\t\t\t\t<td>\n"
		header += "\t\t\t\t\t<stripes:link href='/"+model.simpleName.toLowerCase()+"/view?"+model.simpleName.toLowerCase()+'=${item.id}'+"'>View</stripes:link>"
		header += "&nbsp;|&nbsp;"
		header += "\t\t\t\t\t<stripes:link href='/"+model.simpleName.toLowerCase()+"/edit?"+model.simpleName.toLowerCase()+'=${item.id}'+"'>Edit</stripes:link>"
		header += "&nbsp;|&nbsp;"
		header += "<stripes:link class='deleteObject' href='/"+model.simpleName.toLowerCase()+"/delete?"+model.simpleName.toLowerCase()+'=${item.id}'+"'>Delete</stripes:link>"
		header += "\n\t\t\t\t</td>\n\t\t\t</tr>"
	}
	
	private def getTableHeader(fields) {
		def header = "<tr>"
		fields.each {
			if(!it.type.interfaces.find{it.equals(java.util.Collection)}){
				header += "<th>${it.name}</th>"
			}
		}
		header += "</tr>"
	}

	private def getFieldType(field) {
		def baseModel = this.class.classLoader.findLoadedClass("${GripesUtil.getSettings(project).packageBase}.model.base.BaseModel")

		if(field.type.equals(String)){
			"text"
		} else if(field.type.equals(boolean)){
			"checkbox"
		} else if(field.type.getAnnotation(javax.persistence.Entity)) {
			"text"
		}
		"CHANGEME"
	}
	
	private def isCollection(field) { field.type.interfaces.find{it.equals(java.util.Collection)} }
	private def isEntity(field) { (field.type.getAnnotation(javax.persistence.Entity))?true:false }
	
	private def copyActionBeanContext(project) {
		def basePackage = GripesUtil.getRoot(project)+GripesUtil.getSettings(project).src+"/"+GripesUtil.getSettings(project).packageBase.replace(".","/")
		def template = getResource("templates/util/ActionBeanContext.template")
		template = template.text.replaceAll("PROJECTNAME",GripesUtil.getSettings(project).appName)
								.replaceAll("PACKAGE",GripesUtil.getSettings(project).packageBase)
		
		def newFile = new File(basePackage+"/util/base/BaseActionBeanContext.groovy")
		if(!newFile.exists()) {
			newFile.createNewFile()
			newFile.text = template
		}
	}
	
	private def copyJpaXml(project,config) {
	    def jpaXmlText = getResource("config/persistence.template").text
 		def jpaXml = new File(GripesUtil.getSourceDir(project)+"/META-INF/persistence.xml")
		if(!jpaXml.exists()){
			jpaXml.createNewFile()
			jpaXml.text = jpaXmlText.replaceAll("PACKAGE",GripesUtil.getSettings(project).packageBase)
			jpaXml	
		}
	}
	
	private def getResource(resource) {
		getClass().classLoader.getResource(resource)
	}
}