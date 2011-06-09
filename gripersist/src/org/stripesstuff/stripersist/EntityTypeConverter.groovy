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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.MappedSuperclass;

import net.sourceforge.stripes.config.TargetTypes;
import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.util.Log;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

/**
 * <p>
 * EntityTypeConverter retrieves an object of the specified target type by
 * converting the value parameter into the primary key type for the class and
 * calling {@link EntityManager#find(Class, Object)}.
 * </p>
 * 
 * <p>
 * {@link EntityFormatter} performs the reverse operation.
 * </p>
 * 
 * @author Aaron Porter
 * @see EntityFormatter
 * 
 */
@TargetTypes( [ javax.persistence.Entity.class, javax.persistence.MappedSuperclass.class ])
public class EntityTypeConverter implements TypeConverter<Object> {
    private static final Log log = Log.getInstance(EntityTypeConverter.class);

    private Locale locale;

    /**
     * Converts <code>value</code> into the primary key's type for
     * <code>targetType</code> and calls
     * {@link EntityManager#find(Class, Object)}.
     * 
     * @param value
     *            the value to be converted
     * @param targetType
     *            the type to return
     * @param errors
     *            a collection of errors that occurred during validation and
     *            conversion
     * @return an instance of <code>targetType</code> or null
     */
    @SuppressWarnings("unchecked")
    public Object convert(String value, Class<? extends Object> targetType, Collection<ValidationError> errors) {

        if (value == null)
            return null;

        if (targetType.getAnnotation(Entity.class) == null && targetType.getAnnotation(MappedSuperclass.class) == null) {
            log.debug("The target type is not annotated with @Entity or @MappedSuperclass! ", targetType);
        }

        log.debug("Retrieving ", targetType.getName(), " with primary key ", value);

        EntityManager entityManager = Stripersist.getEntityManager(targetType);

        if (entityManager == null) {
            log.error("Couldn't find an EntityManager associated with ", targetType.getName());
            return null;
        }

        Class idType = EntityUtil.getIdType(targetType);

        if (idType == null) {
            log.error("Couldn't determine id type for ", targetType.getName(), ". unable to retrieve object.");
        } else {
            log.debug("Id for ", targetType.getName(), " is type ", idType);

            Object id = null;

            try {
                TypeConverter<?> converter = StripesFilter.getConfiguration().getTypeConverterFactory()
                        .getTypeConverter(idType, locale);

                log.trace("Using ", converter.getClass().getName(), " to convert id for ", targetType.getName());
                id = converter.convert(value, idType, new ArrayList<ValidationError>());

                log.trace("Retrieving entity from database using id ", id);

                Object entity = entityManager.find(targetType, id);

                if (entity == null)
                    log.debug("Couldn't retrieve the entity");
                else
                    log.debug("Retrieved the entity!");

                return entity;
            } catch (EntityNotFoundException e) {
                errors.add(new SimpleError("Could not find an instance of ", targetType.getName(),
                        " with primary key ", id));
            } catch (IllegalArgumentException e) {
                errors.add(new SimpleError("", id, " is not a valid primary key for ", targetType.getName()));
            } catch (Exception e) {
                log.error(e);
            }
        }

        return null;
    }

    /**
     * Setter for locale
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
