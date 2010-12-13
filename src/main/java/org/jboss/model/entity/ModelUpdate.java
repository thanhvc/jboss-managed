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

package org.jboss.model.entity;

import org.jboss.model.values.MetaValue;

/**
 * TODO add class javadoc for ModelUpdate.
 *
 * @author Brian Stansberry
 */
public class ModelUpdate {

    private static final MetaValue[] EMPTY = new MetaValue[0];

    private final EntityAddress entityAddress;
    private final String operationId;
    private final MetaValue[] params;
    private transient volatile UpdateIdentifier identifier;

    public ModelUpdate(final EntityAddress entityAddress, final String operationId, final MetaValue... params) {
        // TODO assertions
        this.entityAddress = entityAddress;
        this.operationId = operationId;
        this.params = params == null ? EMPTY : params;
    }

    public EntityAddress getEntityAddress() {
        return entityAddress;
    }

    public String getOperationId() {
        return operationId;
    }

    public MetaValue[] getParams() {
        return params;
    }

    public UpdateIdentifier getUpdateIdentifier() {
        if (identifier == null) {
            // Thread safety -- we don't care if we create this twice
            identifier = new UpdateIdentifier(entityAddress.getEntityIdTypes(), operationId);
        }
        return identifier;
    }

}
