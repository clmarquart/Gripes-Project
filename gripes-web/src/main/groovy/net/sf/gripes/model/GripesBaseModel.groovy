package net.sf.gripes.model

import javax.persistence.EntityManager
import org.hibernate.Criteria
import org.hibernate.Session

abstract class GripesBaseModel {
	
	def missingMethods = ["save","list","findBy"]
	
	def getList() {
/*        getEntityManager().createQuery("from " + this.class.name + " t ").getResultList()*/
		getDao().list()
	}
	
	def save() {
		def dao = getDao() //Class.forName("${this.class.package.name.replace('model','dao')}.${this.class.simpleName}Dao").newInstance()
		dao.save(this)
		dao.commit()
	}
	
	def save(map) {
		def obj = this.class.newInstance()
		def dao = getDao() 
		map[0].each {k,v->
			obj."${k}" = v
		}
		dao.save(obj)
		dao.commit()
	}
	
	def list(map){
/*        getEntityManager().createQuery("from " + this.class.name + " t ").getResultList()*/
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
/*	
	protected EntityManager getEntityManager() {
		Stripersist.getEntityManager()
	}
	
    protected Session getSession() {
        (Session) getEntityManager().getDelegate();
    }
*/
	def methodMissing(String name, args) {
		def method = missingMethods.find{name.startsWith(it)}
		if(method) {
			if(method.replaceFirst(name,"")!="")
				this."$method"(args,name.replaceFirst(method,""))
			else
				this."$method"(args)
		} else {
			println "no method still!!!!"
			//throw new MissingMethodException(name, args)
		}
	}

	def propertyMissing(String name, value) { println "$name property doesn't exist." }
}