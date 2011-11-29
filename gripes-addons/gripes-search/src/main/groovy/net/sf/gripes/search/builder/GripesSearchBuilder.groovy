package net.sf.gripes.search.builder

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GripesSearchBuilder {
	Logger logger = LoggerFactory.getLogger(GripesSearchBuilder.class)
	
	def missingMethods = []
	
	Class klass 
	File searchCfg
	def props
	
	GripesSearchBuilder() {
		println "New Search Builder"
	}
	
	GripesSearchBuilder(Class klass) {
		logger.debug "Creating GripesSearchBuilder with ${klass}"
		this.klass = klass
		
		searchCfg = new File(System.getProperty("gripes.temp")+"/addons/gripes-search/Config.properties")
		if(!searchCfg.parentFile.exists()){
			searchCfg.parentFile.mkdirs()
			searchCfg.parentFile.deleteOnExit()
		}
		searchCfg.createNewFile()
		searchCfg.deleteOnExit()
		
		props = new Properties()
	}

	def methodMissing(String name, args) {
		logger.debug "Making {} on {} searchable.", name, klass
		
		props.put(klass.name,name)
		writeOut()
	}
	
	def writeOut() {
		props.store(searchCfg.newWriter(true),"")
	}
}