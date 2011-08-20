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
	Logger logger = LoggerFactory.getLogger(GripesSearch.class)
	
	static def query(String query) {
		def results = []
		EntityManager em 
		Session session
		FullTextSession fullTextSession
		
		def javaEntityClasses = Gripersist.getEntityClasses().collect { it.javaType }
		javaEntityClasses.each { Class cls ->
			if(cls.isAnnotationPresent(org.hibernate.search.annotations.Indexed.class)) {
				cls.declaredFields.findAll { Field field -> 
					field.isAnnotationPresent(org.hibernate.search.annotations.Field.class) 
				}.each { Field field -> 
					em = Gripersist.getEntityManager(cls)
					session = (Session) em.getDelegate()
					fullTextSession = Search.getFullTextSession(session)
					
					QueryBuilder b = fullTextSession.searchFactory.buildQueryBuilder().forEntity(cls).get();
					org.apache.lucene.search.Query luceneQuery = b.keyword().onField(field.name).matching(query).createQuery()
					
					org.hibernate.Query fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, cls )

					results += fullTextQuery.list()
				}
			}
		}
		
		results
	}
}