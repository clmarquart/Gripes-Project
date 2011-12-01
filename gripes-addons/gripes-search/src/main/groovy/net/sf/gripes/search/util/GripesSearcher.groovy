package net.sf.gripes.search.util

import org.hibernate.Session
import org.hibernate.search.Search
import org.hibernate.search.FullTextSession
import org.hibernate.search.query.dsl.QueryBuilder

import net.sf.gripes.stripersist.Gripersist
import org.hibernate.search.jpa.FullTextEntityManager
import javax.persistence.EntityManager

import java.lang.reflect.Field

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GripesSearcher {
	static Logger _logger = LoggerFactory.getLogger(GripesSearcher.class)
	
	static def query(String query) {
		def results = []
		EntityManager em = Gripersist.getEntityManager()
		Session session = (Session) em.getDelegate()
		FullTextSession fullTextSession = Search.getFullTextSession(session)
		QueryBuilder b
		org.apache.lucene.search.Query luceneQuery
		org.hibernate.Query fullTextQuery
		
		_logger.debug "Searching with EntityManager: $em"
		
		def propFile = new File(System.getProperty("gripes.temp")+"/addons/gripes-search/Config.properties")
		def props = new Properties()
		propFile.withInputStream { 
		  stream -> props.load(stream) 
		}
		props.keys().each { String className ->
			_logger.debug "Searching $className for $query on field {}", props.getProperty(className)
			
			b = fullTextSession.searchFactory.buildQueryBuilder().forEntity(Class.forName(className)).get();
			luceneQuery = b.keyword().wildcard().onField(props.getProperty(className)).matching(query).createQuery()
			
			fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, Class.forName(className) )
			_logger.debug "Found {}", fullTextQuery.list()
				
			results += fullTextQuery.list().collect { ent ->
				[
					entity: ent,
					action: ent.class.package.name.replace("model","action")+"."+ent.class.simpleName+"ActionBean"
				]
			}
		}
		
		results
	}
}