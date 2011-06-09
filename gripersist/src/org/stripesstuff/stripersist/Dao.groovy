package org.stripesstuff.stripersist;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.stripesstuff.stripersist.Stripersist;

/**
 * <p>
 * Stripersist's Dao provides a simple generified static DAO. It is not intended to perform magic.
 * It simply makes JPA a little easier to deal with. By providing generified methods it allows you
 * to avoid some casting. It also makes Query a little easier to deal with.
 * </p>
 * 
 * <p>
 * Because this class is static it is not possible to subclass it. For any advanced features that
 * you want to add you'll have to copy the source and create a new class. Sorry!
 * </p>
 * 
 * <p>
 * Special thanks to Freddy Daoud and his wonderful book on Stripes which inspired me to start using
 * DAOs.
 * </p>
 * 
 * @author Aaron Porter
 * 
 */
public class Dao {
    private Dao() {
    }

    /**
     * Generic version of JPA's find.
     * 
     * @param <T> the type you're looking for
     * @param <K> the type of the primary key
     * @param type the type you're looking for
     * @param key the primary key
     * @return the entity with type and primary key specified
     */
    public static <T, K> T find(Class<T> type, K key) {
        return Stripersist.getEntityManager(type).find(type, key);
    }

    /**
     * Store a new entity in the database. If you're trying to persist an object that already has a
     * primary key it is assumed that the object is already in the database so it isn't necessary
     * (and will cause errors) if we try to persist it again.
     * 
     * @param <T> the type of the entity
     * @param entity the entity to persist
     * @return the entity that was persisted
     */
    public static <T> T persist(T entity) {
        // If there is an ID it is already persisted
        if (EntityUtil.getId(entity) == null)
            Stripersist.getEntityManager().persist(entity);

        return entity;
    }

    /**
     * A shortcut to Stripersist.getEntityManager().getTransaction().begin()
     */
    public static void begin() {
        Stripersist.getEntityManager().getTransaction().begin();
    }

    /**
     * A shortcut to Stripersist.getEntityManager().getTransaction().commit()
     */
    public static void commit() {
        Stripersist.getEntityManager().getTransaction().commit();
    }

    /**
     * A shortcut to Stripersist.getEntityManager().getTransaction().rollback()
     */
    public static void rollback() {
        Stripersist.getEntityManager().getTransaction().rollback();
    }

    /**
     * Remove (delete) an entity from the database.
     * 
     * @param <T> the type of the entity
     * @param entity the entity to remove
     */
    public static <T> void remove(T entity) {
        Stripersist.getEntityManager().remove(entity);
    }

    /**
     * A shortcut to Stripersist.getEntityManager().createQuery(query)
     * 
     * @param jqpl a JPQL query
     * @return a JPA Query object
     */
    public static Query createQuery(String jqpl) {
        return Stripersist.getEntityManager().createQuery(jqpl);
    }

    /**
     * Search the database for a list of entities matching the given query. The magic of generics
     * allows us to return the type that you're expecting so you don't have to cast it. Of course
     * there will be problems if the type the query returns doesn't match what you're trying to
     * assign it to.
     * 
     * @param <T> the type of entity to search for
     * @param jpql a JPQL query
     * @param params parameters for the JPQL query
     * @return a list of the entities found
     */
    public static <T> List<T> list(String jpql, Object... params) {
        Query query = createQuery(jpql);

        if (params != null)
            for (int i = 0; i < params.length; i++)
                query.setParameter(i + 1, params[i]);

        @SuppressWarnings("unchecked")
        List<T> list = query.getResultList();

        return list;
    }

    /**
     * Search the database for a unique entity that matches the given query. The magic of generics
     * allows us to return the type that you're expecting so you don't have to cast it. Of course
     * there will be problems if the type the query returns doesn't match what you're trying to
     * assign it to.
     * 
     * @param <T> the type of entity to search for
     * @param jpql a JPQL query
     * @param params parameters for the JPQL query
     * @return the unique entity that matched the query or null
     */
    public static <T> T find(String jpql, Object... params) {
        Query query = createQuery(jpql);

        if (params != null)
            for (int i = 0; i < params.length; i++)
                query.setParameter(i + 1, params[i]);

        try {
            @SuppressWarnings("unchecked")
            T entity = (T) query.getSingleResult();

            return entity;
        }
        catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Search the database for a list of entities matching the given query and
     * return the first element of the list. The magic of generics allows us to
     * return the type that you're expecting so you don't have to cast it. Of
     * course there will be problems if the type the query returns doesn't match
     * what you're trying to assign it to.
     * 
     * @param <T> the type of entity to search for
     * @param jpql a JPQL query
     * @param params parameters for the JPQL query
     * @return the first element in the list or null
     */
    public static <T> T first(String jpql, Object... params) {
        Query query = createQuery(jpql);

        query.setMaxResults(1);

        if (params != null)
            for (int i = 0; i < params.length; i++)
                query.setParameter(i + 1, params[i]);

        @SuppressWarnings("unchecked")
        List<T> list = query.getResultList();

        if (!list.isEmpty())
            return list.get(0);
        else
            return null;
    }
}
