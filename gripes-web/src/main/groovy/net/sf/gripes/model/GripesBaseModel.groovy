package net.sf.gripes.model

import javax.persistence.EntityManager
import org.hibernate.Criteria
import org.hibernate.Session

import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class GripesBaseModel {
	Logger logger = LoggerFactory.getLogger(GripesBaseModel.class)
	
	GripesBaseModel(){
		this.class.metaClass.static.methodMissing = {String name, args ->			
			this.class.newInstance().methodMissing(name, args)
		}
	}
	
	def missingMethods = ["save","list","findBy"]
	
	def getList() {
		getDao().list()
	}
	
	def save() {
		def dao = getDao()
		dao.save(this)
		dao.commit()
	}
	
	def save(map) {
		logger.debug "Saving for {}", this.class.simpleName
		
		def obj = this.class.newInstance()
		def dao = getDao() 
		map[0].each {k,v->
			obj."${k}" = v
		}
		dao.save(obj)
		dao.commit()
	}
	
	def list(map){
		getList()
	}
	
	def find(map) {
		def daoClass = getDao().find(new Long(map[0]))
	}
	
	def findBy(map, params) {		
		getDao().findBy(params.toLowerCase(),map[0])
	}
	
	def getDao() {
		Class.forName("${this.class.package.name.replace('model','dao')}.${this.class.simpleName}Dao").newInstance()
	}
	
	def methodMissing(String name, args) {
		logger.debug "Missing the method {} on {}", name, this.class.simpleName
		def method = missingMethods.find{name.startsWith(it)}
		if(method) {
			if(method.replaceFirst(name,"")!="")
				this."$method"(args,name.replaceFirst(method,""))
			else
				this."$method"(args)
		} else {
			logger.debug "no method still!!!!"
			//throw new MissingMethodException(name, args)
		}
	}
	def propertyMissing(String name, value) { logger.debug "{} property doesn't exist.", name }
}