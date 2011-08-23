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
		EntityManager em 
		Session session
		FullTextSession fullTextSession
		QueryBuilder b
		org.apache.lucene.search.Query luceneQuery
		org.hibernate.Query fullTextQuery
		
		def javaEntityClasses = Gripersist.getEntityClasses().collect { it.javaType }
		javaEntityClasses.each { Class cls ->
			if(cls.isAnnotationPresent(org.hibernate.search.annotations.Indexed.class)) {
				cls.declaredFields.findAll { Field field -> 
					field.isAnnotationPresent(org.hibernate.search.annotations.Field.class) 
				}.each { Field field -> 
					em = Gripersist.getEntityManager()
					
					_logger.debug "Searching field $field on $cls for $query"
					_logger.debug "Searching with EntityManager: $em"
					
					session = (Session) em.getDelegate()
					fullTextSession = Search.getFullTextSession(session)
					
					b = fullTextSession.searchFactory.buildQueryBuilder().forEntity(cls).get();
					luceneQuery = b.keyword().wildcard().onField(field.name).matching(query).createQuery()
					
					fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, cls )
					_logger.debug "Found {}", fullTextQuery.list()
						
					results += fullTextQuery.list()
				}
			}
		}
		
		results
	}
}