package PACKAGE.dao.base

import java.util.List

import org.stripesstuff.stripersist.Stripersist
import javax.persistence.EntityManager
import javax.persistence.NoResultException
import javax.persistence.NonUniqueResultException
import javax.persistence.Query

import org.hibernate.Session;

public abstract class BaseDao<T,Long> {
    protected Class entityClass;
    
    BaseDao() {
		entityClass = Class.forName(this.class.name.replace("dao","model").replace("Dao",""))
    }
    
    List list() {
        getEntityManager()
        	.createQuery("from " + entityClass.getName())
            .getResultList();
    }
    T find(Long id) {
        getEntityManager().find(entityClass, id);
    }
    void save(T object) {
    	getEntityManager().persist(object);
    }
    void delete(T object) {
    	getEntityManager().remove(object);
    }
    void commit() {
        getEntityManager().getTransaction().commit();
    }

    T findBy(String fieldName, Object value) {
        Query query = getEntityManager()
            .createQuery(getQuery(fieldName))
            .setParameter(fieldName, value)

        getSingleResult(query);
    }
	List<T> findAll(String fieldName, Object value) {
        Query query = getEntityManager()
            .createQuery(getQuery(fieldName))
            .setParameter(fieldName, value);
        
		query.getResultList();
    }
    
    void merge(T entity) {
    	((Session) getEntityManager().getDelegate()).merge(entity);	
    }
    
    protected String getQuery(String fieldName){
	    "from " + entityClass.getName() + " t where t." + fieldName + " = :" + fieldName
    }
    
    private T getSingleResult(Query query) {
        try {
            (T) query.getSingleResult()
        } catch (NonUniqueResultException exc) {
            (T) query.getResultList().get(0)
        } catch (NoResultException exc) {
            null
        }
    }

    def getEntityManager() {
		Stripersist.getEntityManager()
	}

    Session getSession() {
		Stripersist.getEntityManager().getDelegate()
    }
    
    protected Class getEntityClass() {
        return entityClass;
    }
}
