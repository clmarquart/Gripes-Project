/* Copyright 2008 Aaron Porter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.stripesstuff.stripersist;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;

import net.sourceforge.stripes.util.Log;
import net.sourceforge.stripes.util.StringUtil;

/**
 * EntityUtil provides some convenience functions for working with entities.
 * 
 * @author Aaron Porter
 * 
 */
public class EntityUtil {
    private static Log log = Log.getInstance(EntityUtil.class);

    private static final Map<Class<?>, Class<?>> idTypeCache = new ConcurrentHashMap<Class<?>, Class<?>>();

    private EntityUtil() {
        throw new AssertionError();
    }

    /**
     * Return the proxified class for passed class. If the passed class is not CGLIB enhanced, then
     * it returns that same class.
     * 
     * Thanks Remi!
     * 
     * @param proxifiedClass the proxy class
     * @return the original class
     */
    public static Class<? extends Object> deproxifyCglibClass(Class<? extends Object> proxifiedClass) {
        String proxifiedClassName = proxifiedClass.getName();

        log.trace("Making sure ", proxifiedClassName, " is not a proxy");

        int i = proxifiedClassName.indexOf("\$\$");

        if (i == -1)
            return proxifiedClass;
        else {
            String className = proxifiedClassName.replaceAll(/[_]*\\$\\$.*/, "");

            try {
                log.trace("Looks like ", proxifiedClassName, " is a proxy for ", className);
                Class<? extends Object> clazz = Class.forName(className);
                return clazz;
            }
            catch (ClassNotFoundException e) {
                log.error("Unable to deproxify: ", proxifiedClassName, " not found !", e);
                return null;
            }
        }
    }

    /**
     * Checks the fields and methods of <code>clazz</code> to determine the primary key's type.
     * Results are cached to improve performance.
     * 
     * @param clazz the class to examine
     * @return the primary key's type or null
     */
    public static Class<?> getIdType(Class<? extends Object> clazz) {
        clazz = deproxifyCglibClass(clazz);

        Class<?> idType = idTypeCache.get(clazz);

        if (idType != null)
            return idType;

        idType = getIdTypeFromFields(clazz);

        if (idType == null)
            idType = getIdTypeFromMethods(clazz);

        if (idType == null) {
            Class<? extends Object> superclass = clazz.getSuperclass();

            while (idType == null && superclass != null) {
                idType = getIdTypeFromFields(clazz);
                superclass = superclass.getSuperclass();
            }
        }

        if (idType != null)
            idTypeCache.put(clazz, idType);

        return idType;
    }

    /**
     * Looks for an Id annotation on the fields of <code>clazz</code> to figure out the primary
     * key's type.
     * 
     * @param clazz the class to examine
     * @return the primary key's type or null
     */
    public static Class<?> getIdTypeFromFields(Class<? extends Object> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            log.trace("Looking for @Id on field ", field.getName());

            if (field.getAnnotation(Id.class) == null)
                continue;

            try {
                log.trace("Found @Id for ", clazz.getName(), " on field ", field.getName());

                return field.getType();
            }
            catch (Exception e) {
                log.error(e);
            }
        }

        Class<? extends Object> superclass = clazz.getSuperclass();

        if (superclass != null)
            return getIdTypeFromFields(superclass);

        return null;
    }

    /**
     * Looks for an Id annotation on the methods of <code>clazz</code> to figure out the primary
     * key's type.
     * 
     * @param clazz the class to examine
     * @return the primary key's type or null
     */
    public static Class<?> getIdTypeFromMethods(Class<? extends Object> clazz) {
        for (Method method : clazz.getMethods()) {
            log.trace("Looking for @Id on method ", method.getName());

            if ((method.getParameterTypes().length != 0)
                    || (method.getAnnotation(Id.class) == null))
                continue;

            try {
                log.trace("Found @Id for ", clazz.getName(), " on method ", method.getName());

                return method.getReturnType();
            }
            catch (Exception e) {
                log.error(e);
            }
        }

        return null;
    }

    private static final Map<Class<?>, Object> idAccessor = new ConcurrentHashMap<Class<?>, Object>();

    /**
     * Gets the value of the primary key for the specified entity.
     * 
     * @param entity the target entity
     * @return the primary key or null
     */
    public static Object getId(Object entity) {
        Class<?> clazz = deproxifyCglibClass(entity.getClass());

        Object accessor = idAccessor.get(clazz);

        if (accessor == null) {
            log.trace("Finding @Id for ", entity.getClass().getName());

            accessor = findIdField(clazz);

            if (accessor == null)
                accessor = findIdMethod(clazz);

            if (accessor == null) {
                Class<? extends Object> superclass = clazz.getSuperclass();

                while (accessor == null && superclass != null) {
                    accessor = findIdField(superclass);

                    if (accessor == null)
                        accessor = findIdMethod(superclass);

                    superclass = superclass.getSuperclass();
                }
            }

            if (accessor != null)
                idAccessor.put(clazz, accessor);
        }

        if (accessor != null) {
            if (accessor instanceof Method) {
                try {
                    log.trace("Getting id for ", clazz, " via Method");
					Object[] args = ["asdf"]
                    return ((Method) accessor).invoke(entity, args);
                }
                catch (Exception e) {
                    log.error(e);
                }
            }
            else if (accessor instanceof Field) {
                try {
                    log.trace("Getting id for ", clazz, " via Field");
                    Object id = ((Field) accessor).get(entity);

                    if (id == null) {
                        // Try to get the id value via a getter - suggested by Lionel to fix a
                        // problem with Field.get() returning null on some proxies

                        String getterName = ((Field) accessor).getName();
                        getterName = StringUtil.combineParts("get", getterName.substring(0, 1)
                                .toUpperCase(), getterName.substring(1));
                        Method getter = null;
                        
                        Class<?> inspect = clazz;
                        
                        while (getter == null && inspect != null) {
                            try {
                                getter = inspect.getDeclaredMethod(getterName);
                            }
                            catch (NoSuchMethodException e) {
                            }
                            
                            inspect = inspect.getSuperclass();
                        }
                        
                        if (getter != null) {
                            log.trace("Couldn't get id via Field. Trying getter ", getterName);

                            idAccessor.put(clazz, getter);
                            id = getter.invoke(entity);
                        }
                        else {
                            log.error("Couldn't figure out how to get the Id for ", clazz, "!");
                        }
                    }

                    return id;
                }
                catch (Exception e) {
                    log.error(e);
                }
            }
        }
        else
            log.warn("Couldn't determine how to get id from ", clazz, "!");

        return null;
    }

    /**
     * Attempts to find a field annotated with Id.
     * 
     * @param clazz the class to examine
     * @return the annotated field or null
     */
    public static Field findIdField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            log.trace("Looking for @Id on field ", field.getName());

            if (field.getAnnotation(Id.class) == null)
                continue;

            log.trace("Found @Id for ", clazz.getName(), " on field ", field.getName());

            field.setAccessible(true);
            return field;
        }

        Class<?> superclass = clazz.getSuperclass();

        if (superclass != null)
            return findIdField(superclass);

        return null;
    }

    /**
     * Attempts to find a method annotated with Id.
     * 
     * @param clazz the class to examine
     * @return the annotated method or null
     */
    public static Method findIdMethod(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            log.trace("Looking for @Id on method ", method.getName());

            if ((method.getParameterTypes().length != 0)
                    || (method.getAnnotation(Id.class) == null))
                continue;

            log.trace("Found @Id for ", clazz.getName(), " on method ", method.getName());

            return method;
        }

        return null;
    }
}
