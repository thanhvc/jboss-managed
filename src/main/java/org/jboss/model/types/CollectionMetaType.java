/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.model.types;

import java.util.Collection;

import org.jboss.model.values.CollectionValue;

/**
 * CollectionMetaType.
 *
 * @author <a href="ales.justin@jboss.com">Ales Justin</a>
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 */
public class CollectionMetaType extends AbstractMetaType {

    /** The serialVersionUID */
    private static final long serialVersionUID = -2062790692152055156L;

    /** The element type for the array */
    private MetaType elementType;

    /** Cached hash code */
    private transient int cachedHashCode = Integer.MIN_VALUE;

    /** Cached string representation */
    private transient String cachedToString = null;

    public CollectionMetaType(String className, MetaType elementType) {
        super(className);
        if (elementType == null) {
            throw new IllegalArgumentException("Null element type");
        }
        this.elementType = elementType;
    }

    public CollectionMetaType(String className, String description, MetaType elementType) {
        super(className, description);
        if (elementType == null) {
            throw new IllegalArgumentException("Null element type");
        }
        this.elementType = elementType;
    }

    public CollectionMetaType(String className, String typeName, String description, MetaType elementType) {
        super(className, typeName, description);
        if (elementType == null) {
            throw new IllegalArgumentException("Null element type");
        }
        this.elementType = elementType;
    }

    public boolean isCollection() {
        return true;
    }

    /**
     * Get collection meta type.
     *
     * @param collectionType the element meta type
     * @param elementType the element meta type
     * @return collection meta type
     */
    public static CollectionMetaType getCollectionType(String collectionType, MetaType elementType) {
        return new CollectionMetaType(collectionType, elementType);
    }

    /**
     * Get the meta type of the array elements
     *
     * @return the element type
     */
    public MetaType getElementType() {
        return elementType;
    }

    @Override
    public boolean isValue(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Collection == false && (obj instanceof CollectionValue) == false) {
            return false;
        }
        if (obj instanceof CollectionValue) {
            CollectionValue cv = (CollectionValue) obj;
            return equals(cv.getMetaType());
        }

        Collection collection = (Collection) obj;
        for (Object element : collection) {
            if (elementType.isValue(element) == false) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj instanceof CollectionMetaType == false) {
            return false;
        }
        CollectionMetaType other = (CollectionMetaType) obj;
        return getTypeName().equals(other.getTypeName()) && getElementType().equals(other.getElementType());
    }

    @Override
    public int hashCode() {
        if (cachedHashCode != Integer.MIN_VALUE) {
            return cachedHashCode;
        }
        cachedHashCode = getTypeName().hashCode() + getElementType().hashCode();
        return cachedHashCode;
    }

    @Override
    public String toString() {
        if (cachedToString != null) {
            return cachedToString;
        }
        StringBuilder buffer = new StringBuilder(CollectionMetaType.class.getSimpleName());
        buffer.append("{type=");
        buffer.append(getTypeName());
        buffer.append(" elementType=");
        buffer.append(elementType);
        cachedToString = buffer.toString();
        return cachedToString;
    }
}
