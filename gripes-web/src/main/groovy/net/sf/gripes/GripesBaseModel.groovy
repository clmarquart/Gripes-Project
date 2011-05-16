package net.sf.gripes

import org.stripesstuff.stripersist.Stripersist
import javax.persistence.EntityManager
import org.hibernate.Criteria
import org.hibernate.Session

abstract class GripesBaseModel {
	
	def missingMethods = ["save","list","findBy"]
	
	def getList() {
        getEntityManager().createQuery("from " + this.class.name + " t ").getResultList()
	}
	
	def save() {
		def dao = Class.forName("${this.class.package.name.replace('model','dao')}.${this.class.simpleName}Dao").newInstance()
		dao.save(this)
		dao.commit()
	}
	
	def save(map) {
		def obj = this.class.newInstance()
		def dao = Class.forName("${this.class.package.name.replace('model','dao')}.${this.class.simpleName}Dao").newInstance()
		map[0].each {k,v->
			obj."${k}" = v
		}
		dao.save(obj)
		dao.commit()
	}
	
	def list(map){
		println "LISTING"
        getEntityManager().createQuery("from " + this.class.name + " t ").getResultList()
	}
	
	def find(map) {
		def pack = this.class.package.name.replace("model","dao")
		def daoClass = Class.forName(pack+"."+this.class.simpleName+"Dao")
		daoClass.newInstance().find(new Long(map[0]))
	}
	
	def findBy(map, params) {
		def dao = Class.forName(this.class.package.name.replaceFirst("model","dao")+"."+this.class.simpleName+"Dao").newInstance()
		
		dao.findBy(params.toLowerCase(),map[0])
	}
	
	protected EntityManager getEntityManager() {
		Stripersist.getEntityManager()
	}
	
    protected Session getSession() {
        (Session) getEntityManager().getDelegate();
    }

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