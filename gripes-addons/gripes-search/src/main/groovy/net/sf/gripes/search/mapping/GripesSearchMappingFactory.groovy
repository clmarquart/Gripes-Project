package net.sf.gripes.search.mapping

import org.hibernate.search.annotations.Factory
import org.hibernate.search.cfg.SearchMapping

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.annotation.ElementType
import java.util.Properties
import groovy.util.ConfigSlurper

public class GripesSearchMappingFactory {
	Logger logger = LoggerFactory.getLogger(GripesSearchMappingFactory.class)
	
    @Factory SearchMapping getSearchMapping() {
        SearchMapping mapping = new SearchMapping();
		
		def propFile = new File(System.getProperty("gripes.temp")+"/addons/gripes-search/Config.properties")
		def props = new Properties()
		propFile.withInputStream { stream -> 
			props.load(stream) 
		}
		props.keys().each { String className ->
	        mapping.entity(Class.forName(className))
				.indexed()
				.property(props.getProperty(className), ElementType.FIELD)
				.field()
				
			logger.debug "Indexing class, {}, on field, {}", className, props.getProperty(className)
		}

		logger.debug "Mapped: {}", mapping
        return mapping;
    }
}