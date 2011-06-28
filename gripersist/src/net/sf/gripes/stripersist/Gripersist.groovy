package net.sf.gripes.stripersist

import org.stripesstuff.stripersist.Stripersist

import net.sourceforge.stripes.config.ConfigurableComponent
import net.sourceforge.stripes.controller.Intercepts
import net.sourceforge.stripes.controller.LifecycleStage
import net.sourceforge.stripes.controller.Interceptor

import javax.persistence.EntityManager;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Intercepts([LifecycleStage.RequestInit, LifecycleStage.RequestComplete])
class Gripersist extends Stripersist implements Interceptor, ConfigurableComponent {
	Logger logger = LoggerFactory.getLogger(Gripersist.class)
	static Logger _logger= LoggerFactory.getLogger(Gripersist.class)
	
    static {
			println "COME ON!!!!!!"
        Package pkg = Stripersist.class.getPackage();
        _logger.info("""
##################################################
# Stripersist Version: ${pkg.getSpecificationVersion()}, Build: ${pkg.getImplementationVersion()}
# Gripersist Version: 0.1.1
##################################################""")
    }

    static EntityManager getEntityManager() {
		_logger.debug "Searching for the EntityManager..."
		
        if (Stripersist.entityManagerFactories.size() != 1) {
			def dbConfig = new ConfigSlurper().parse(this.classLoader.getResource("DB.groovy").text)
			def primary = dbConfig.database.find{k,v->v.containsKey("primary")}?:dbConfig.database.find{it!=null}
			def key = primary.key
			
			_logger.debug "Using PersistenceUnit ${key} as the Default, can be overidden using 'primary=true' in DB.groovy"
			
	        getEntityManager(key)
        } else {
			_logger.debug "There is only one PersistenceUnit, using that."
			_logger.debug "Factories: {}", Stripersist.entityManagerFactories.values().iterator().next()
	        Stripersist.getEntityManager(Stripersist.entityManagerFactories.values().iterator().next())
		}
    }
}