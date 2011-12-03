package net.sf.gripes.entity.builder

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.hibernate.SessionFactory
import org.hibernate.metamodel.MetadataSources

class GripesEntityBuilder {
	Logger logger = LoggerFactory.getLogger(GripesEntityBuilder.class)
	
	
	Class klass 
	File entityCfg
	def props
	
	GripesEntityBuilder() {
		logger.debug "New Entity Builder"
	}
	
	GripesEntityBuilder(Class klass) {
		logger.debug "Creating GripesEntityBuilder with ${klass}"
		this.klass = klass
		
		entityCfg = new File(System.getProperty("gripes.temp")+"/gripersist/Config.properties")
		
		if(!entityCfg.parentFile.exists()){
			entityCfg.parentFile.mkdirs()
			entityCfg.parentFile.deleteOnExit()
		}
		
		entityCfg.createNewFile()
		entityCfg.deleteOnExit()
		
		props = new Properties()

		def tmpFile = new File(System.getProperty("gripes.temp")+"/"+klass.name.replace(".","/")+"/"+klass.simpleName+".hbm.xml")
		
		if(!tmpFile.parentFile.exists()){
			tmpFile.parentFile.mkdirs()
			tmpFile.parentFile.deleteOnExit()
		}
		tmpFile.createNewFile()
		tmpFile.deleteOnExit()
		tmpFile.text = """<class name="com.acme.model.Author" table="AUTHOR">
    <id name="id" column="AUTHOR_ID">
        <generator class="native"/>
    </id>
    <property name="name"/>

    <set name="posts" table="AUTHOR_POST">
        <key column="AUTHOR_ID"/>
        <many-to-many column="POST_ID" class="Post"/>
    </set>
</class>"""

		org.hibernate.service.BasicServiceRegistry registry = (new org.hibernate.service.ServiceRegistryBuilder().configure()).buildServiceRegistry()
		SessionFactory sessionFactory = new MetadataSources( registry )
//		        .addFile( tmpFile )
				.buildMetadata()
		        .buildSessionFactory();
		//println "REGISTRY: ${registry}"
	}

	def methodMissing(String name, args) {
		logger.debug "Making {} on {} a mapped field.", name, klass
		
		props.put(klass.name,name)
		writeOut()
	}
	
	def writeOut() {
		props.store(entityCfg.newWriter(true),"")
	}
}