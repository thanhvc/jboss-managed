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

package org.jboss.model.detyped;

import java.util.HashMap;
import java.util.Map;

import org.jboss.metatype.api.types.MetaType;
import org.jboss.metatype.api.values.MetaValue;
import org.jboss.model.detyped.info.EntityAttributeInfo;
import org.jboss.model.detyped.info.ModelEntityInfo;

/**
 * Provides a detyped view of an {@link AbstractAddressablesModelElement}.
 * <p/>
 * A ModelEntity can come in two flavors, depending on the value of its
 * {@link #isIdOnly()} property. If <code>true</code> the object should be
 * regarded as simply a placeholder in a larger data structure. If <code>false</code>
 * the object contains information on the underlying model element's properties.
 * </p/>
 * Typical usages of a ModelEntity whose {@link #isIdOnly()} property
 * is <code>true</code> are:
 * <ul>
 * <li>For a remote client reading the model, as a placeholder entry for model
 * data in which the client is not interested, allowing less data to be
 * shipped to the client while still maintaining the basic model structure.</li>
 * <li>For management client that passes back a ModelEntity to the server
 * that is meant to represent an updated view of the model, as a placeholder
 * entry for a portion of the model that is not modified by the update. Including
 * the placeholder allows to server to clearly distinguish elements that are
 * added, updated and removed.</li>
 * </ul>
 *
 * @author Brian Stansberry
 */
public class ModelEntity {

    /** The entity id. */
    private final EntityId id;
    private final boolean idOnly;

    /** The entity info. */
    private final ModelEntityInfo entityInfo;

    /** The attribute values. */
    private final Map<String, MetaValue> attributeValues = new HashMap<String, MetaValue>();

    /**
     * Create a new ModelEntity, with idOnly false.
     *
     * @param id the entity id
     * @param info the model entity info
     */
    public ModelEntity(final EntityId id, final ModelEntityInfo info) {
        this(id, info, false);
    }

    /**
     * Create a new ModelEntity.
     *
     * @param id the entity id
     * @param info the model entity info
     * @param idOnly true if this is a idOnly entity
     */
    public ModelEntity(final EntityId id, final ModelEntityInfo info, final boolean idOnly) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        if(info == null) {
            throw new IllegalArgumentException("info is null");
        }
        // Check the ID type
//        if(! info.getIdentifierType().equals(id.getIdentifierType())) {
//            throw new IllegalArgumentException(String.format("invalid identifier type (%s), should be (%s)",
//                    info.getIdentifierType(), id.getIdentifierType()));
//        }
        this.id = id;
        this.entityInfo = info;
        this.idOnly = idOnly;
    }

    /**
     * Gets the id of the model element.
     *
     * @return the id. Will not be <code>null</code>
     */
    public EntityId getElementId() {
        return this.id;
    }

    /**
     * Gets the entity type info.
     *
     * @return the entityInfo
     */
    public ModelEntityInfo getEntityInfo() {
        return entityInfo;
    }

    /**
     * Set an attribute value.
     *
     * @param attributeName the attribute name
     * @param value the value to set
     * @throws IllegalArgumentException if the types don't match
     */
    public void setAttribute(final String attributeName, final MetaValue value) {
        if(attributeName == null) {
            throw new IllegalArgumentException("null attribute name");
        }
        final EntityAttributeInfo attribute = entityInfo.getAttributeInfo(attributeName);
        if(attribute == null) {
            throw new IllegalArgumentException(String.format("attribute (%s) not declared.", attributeName));
        }
        final MetaType attributeType = attribute.getType();
        if(! attributeType.isValue(value)) {
            throw new IllegalArgumentException(String.format("invalid attribute value (%s), should be (%s).", attributeName, attributeType));
        }
        attributeValues.put(attributeName, value);
    }

    /**
     * Get an attribute value.
     *
     * @param attributeName the attribute name
     * @return the attribute value, <code>null</code> if not set
     */
    public MetaValue getAttribute(final String attributeName) {
        return attributeValues.get(attributeName);
    }

    /**
     * Get an attribute value.
     *
     * @param <T> the expected <code>MetaValue</code> type
     * @param attributeName the attribute name
     * @param expected the expected class
     * @return the attribute value, <code>null</code> if not set
     */
    public <T extends MetaValue> T getAttribute(final String attributeName, Class<T> expected) {
        final MetaValue value = getAttribute(attributeName);
        if (value == null) {
            return null;
        }
        return expected.cast(value);
    }

    /**
     *
     * @return
     */
    public boolean isIdOnly() {
        return this.idOnly;
    }

    private void checkIdOnly() {
        if (idOnly) {
            throw new IllegalStateException("Element is id-only; content cannot be accessed");
        }
    }

}
