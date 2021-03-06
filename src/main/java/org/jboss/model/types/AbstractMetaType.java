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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamField;

import org.jboss.model.values.MetaValue;

/**
 * AbstractMetaType.
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 */
public abstract class AbstractMetaType implements MetaType {

    /** The serialVersionUID */
    private static final long serialVersionUID = 5786422588217893696L;

    /** The peristent fields */
    private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[] {
            new ObjectStreamField("className", String.class),
            new ObjectStreamField("description", String.class),
            new ObjectStreamField("typeName", String.class) };

    /** The type's class name */
    private String className;

    // TODO internationalization
    /** The type's description */
    private String description;

    /** The type's name */
    private String typeName;

    /** Whether the class is an array */
    private transient boolean array = false;

    /**
     * Construct a new MetaType.
     * <p>
     *
     * The class name must be in {@link #ALLOWED_CLASSNAMES} or an array of
     * those classes.
     *
     * @param className the name of the class implementing the meta type, cannot be <code>null</code>
     * @param typeName the name of the meta type, cannot be <code>null</code>
     * @param description the human readable description of the type, cannot be <code>null</code>
     * @throws IllegalArgumentException for a <code>null</code> argument or a class is not an allowed class
     */
    protected AbstractMetaType(String className, String typeName, String description) {
        init(className, typeName, description);
    }

    /**
     * Construct a new MetaType.
     * <p>
     *
     * The class name must be in {@link #ALLOWED_CLASSNAMES} or an array of
     * those classes.
     *
     * @param className the name of the class implementing the meta type, cannot be <code>null</code>
     * @param description the human readable description of the type, cannot be <code>null</code>
     * @throws IllegalArgumentException for a <code>null</code> argument or a class is not an allowed class
     */
    protected AbstractMetaType(String className, String description) {
        init(className, className, description);
    }

    /**
     * Construct a new MetaType.
     *
     * The class name must be in {@link #ALLOWED_CLASSNAMES} or an array of
     * those classes.
     *
     * @param className the name of the class implementing the meta type, cannot be <code>null</code>
     * @throws IllegalArgumentException for a <code>null</code> argument or a class is not an allowed class
     */
    protected AbstractMetaType(String className) {
        init(className, className, className);
    }

    public String getClassName() {
        return className;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnum() {
        return false;
    }

    public boolean isComposite() {
        return false;
    }

    public boolean isSimple() {
        return false;
    }

    public boolean isTable() {
        return false;
    }

    public boolean isArray() {
        return array;
    }

    public boolean isCollection() {
        return false;
    }

    public abstract boolean isValue(Object obj);

    /**
     * Initialise the object
     *
     * @param className the name of the class implementing the meta type, cannot be
     *            null or an empty
     * @param typeName the name of the meta type, cannot be null or an empty string
     * @param description the human readable description of the type, cannot be null or
     *            an empty string
     * @exception IllegalArgumentException for a null or empty argument or when class name is not
     *            allowed class
     */
    private void init(String className, String typeName, String description) {
        if (className == null || className.trim().equals("")) {
            throw new IllegalArgumentException("null or empty class name");
        }
        if (typeName == null || typeName.trim().equals("")) {
            throw new IllegalArgumentException("null or empty type name");
        }
        if (description == null || description.trim().equals("")) {
            throw new IllegalArgumentException("null or empty description");
        }
        if (isCollection() == false) {
            // Calculate the underlying class and whether this is an array
            String testClassName = getBaseClassName(className);
            if (testClassName == null) {
                throw new IllegalArgumentException("Invalid array declaration (see the javadocs for java.lang.Class): " + className);
            }
            if (testClassName.equals(className) == false) {
                array = true;
            }
            // Check the underlying class
            boolean ok = false;
            for (int i = 0; i < ALLOWED_CLASSNAMES.size(); i++) {
                if (testClassName.equals(ALLOWED_CLASSNAMES.get(i))) {
                    ok = true;
                    break;
                }
            }
            if (ok == false) {
                // Check for a primative array type
                int index = className.lastIndexOf('[');
                if (index == className.length() - 2) {
                    ok = ArrayMetaType.isPrimitiveEncoding(className.substring(index + 1));
                }
                if (ok == false) {
                    throw new IllegalArgumentException("Not a MetaType allowed class name: " + className);
                }
            }
        }

        // Looks ok
        this.className = className;
        this.typeName = typeName;
        this.description = description;
    }

    /**
     * Gets the base class name, either the passed class name or the underlying
     * class name if it is an array.
     * <p>
     *
     * NOTE: The class is not check for validity.
     * <p>
     *
     * Null is returned when the array declaration is invalid.
     *
     * @param className the string to test
     * @return the underlying class name or null
     */
    private static String getBaseClassName(String className) {
        final int length = className.length();
        final int last = length - 1;
        int i = 0;

        // Eat the array dimensions
        while (i < length && className.charAt(i) == '[') {
            ++i;
        }
        // It looks like an array
        if (i > 0) {
            // But is it valid
            char type = className.charAt(i);
            // Primitive array
            if (type == 'B' || type == 'C' || type == 'D' || type == 'F'
                    || type == 'I' || type == 'J' || type == 'S' || type == 'Z'
                    || type == 'V') {
                if (i != last) {
                    return null;
                }
                return className.substring(last, length);
            }
            // Object Array
            else if (className.charAt(i) != 'L' || i >= last - 1 || className.charAt(last) != ';') {
                return null;
            }
            // Potentially valid array, class name might be rubbish
            return className.substring(i + 1, last);
        }

        // Not an array
        return className;
    }

    /** {@inheritDoc} */
    public boolean isValue(MetaValue metaValue) {
        if(metaValue == null) {
            return false;
        }
        return equals(metaValue.getMetaType());
    }

    /**
     * Read the object from a stream.
     *
     * @param in the input stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField getField = in.readFields();
        String className = (String) getField.get("className", null);
        String typeName = (String) getField.get("typeName", null);
        String description = (String) getField.get("description", null);
        try {
            init(className, typeName, description);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error deserializing MetaType: " + className, e);
        }
    }

    public <T extends MetaType> T as(Class<T> expected) {
        return expected.cast(this);
    }
}
