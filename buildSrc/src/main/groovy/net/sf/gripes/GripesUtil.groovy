class GripesUtil {
	static def getRoot(project) {
		project.projectDir.canonicalPath
	}
	static def getTempDir(project) {
		def tmp = new File(project.buildDir.canonicalPath+"/tmp/gripes")
		if(!tmp.exists()) tmp.mkdirs()
		tmp
	}
	static def getSettings(project) {
		project.convention.plugins.gripes
	}

	static def getSourceDir(project) {
		this.getRoot(project)+this.getSettings(project).src
	}
	
	static def getResourceDir(project) {
		this.getRoot(project)+this.getSettings(project).resources
	}

	static def getBasePackage(project) {
		this.getRoot(project)+this.getSettings(project).src+"/"+this.getSettings(project).packageBase.replace(".","/")
	}
	
	static def makeDir(parentFile) {
		if(!parentFile.exists()){
			parentFile.mkdirs()
		}
	}
	
	static def saveFile(file,template) {
		println "Saving " + file
		println "setting text: " + template.length()
		
		makeDir(file.parentFile)
		if(!file.exists()) {
			file.createNewFile()
			file.text = template
		}
	}
}