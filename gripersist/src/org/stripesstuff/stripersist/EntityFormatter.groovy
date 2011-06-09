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

import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import net.sourceforge.stripes.config.TargetTypes;
import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.format.Formatter;
import net.sourceforge.stripes.util.Log;

/**
 * <p>
 * EntityFormatter returns an entity's primary key as a string so Stripes can
 * insert it into web pages.
 * </p>
 * 
 * <p>
 * {@link EntityTypeConverter} performs the reverse operation.
 * </p>
 * 
 * @author Aaron Porter
 * @see EntityTypeConverter
 * 
 */
@TargetTypes( [ javax.persistence.Entity.class, javax.persistence.MappedSuperclass.class ])
public class EntityFormatter implements Formatter<Object> {
    private static final Log log = Log.getInstance(EntityFormatter.class);

    private Locale locale;

    /**
     * Converts the entity's primary key to a String and returns it.
     * 
     * @param entity
     *            the object being formatted
     * @return the entity's primary key formatted as a String or null
     */
    @SuppressWarnings("unchecked")
    public String format(Object entity) {
        if (entity == null)
            return null;

        if (entity.getClass().getAnnotation(Entity.class) == null
                && entity.getClass().getAnnotation(MappedSuperclass.class) == null) {
            log.debug("The object passed in was not annotated with @Entity or @MappedSuperclass! ", entity.getClass());
        }

        log.debug("Retrieving primary key to return as format string for instance of ", entity.getClass().getName());

        Object id = EntityUtil.getId(entity);

        if (id == null) {
            log.warn("Couldn't get the primary key for instance of ", entity.getClass().getName());
            return "";
        }

        Formatter idFormatter = StripesFilter.getConfiguration().getFormatterFactory().getFormatter(id.getClass(),
                locale, null, null);

        String value = null;

        if (idFormatter != null)
            value = idFormatter.format(id);
        else
            value = id.toString();

        log.debug("Returning ", value);

        return value;
    }

    /**
     * Unused
     */
    public void init() {
    }

    /**
     * Unused
     */
    public void setFormatPattern(String pattern) {
    }

    /**
     * Unused
     */
    public void setFormatType(String type) {
    }

    /**
     * Setter for locale
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
