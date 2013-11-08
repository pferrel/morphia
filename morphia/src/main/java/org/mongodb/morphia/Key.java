package org.mongodb.morphia;


import java.io.Serializable;


/**
 * <p> The key object; this class is take from the app-engine datastore (mostly) implementation. It is also Serializable and GWT-safe,
 * enabling your entity objects to be used for GWT RPC should you so desire. </p> <p/> <p> You may use normal DBRef objects as relationships
 * in your entities if you desire neither type safety nor GWT-ability. </p>
 *
 * @author Jeff Schnitzer <jeff@infohazard.org> (from Objectify codebase)
 * @author Scott Hernandez (adapted to morphia/mongodb)
 */
public class Key<T> implements Serializable, Comparable<Key<?>> {
    private static final long serialVersionUID = 1L;
    //CHECKSTYLE:OFF
    /**
     * The name of the class which represents the kind. As much as we'd like to use the normal String kind value here, translating back to a
     * Class for getKind() would then require a link to the OFactory, making this object non-serializable.
     */
    protected String kind;
    protected Class<? extends T> kindClass;

    /**
     * Id value
     */
    protected Object id;
    protected byte[] idBytes;
    //CHECKSTYLE:ON
    
    /**
     * For GWT serialization
     */
    protected Key() {
    }

    /**
     * Create a key with an id
     */
    public Key(final Class<? extends T> kind, final Object id) {
        kindClass = kind;
        this.id = id;
    }

    /**
     * Create a key with an id
     */
    public Key(final Class<? extends T> kind, final byte[] idBytes) {
        kindClass = kind;
        this.idBytes = idBytes;
    }

    /**
     * Create a key with an id
     */
    public Key(final String kind, final Object id) {
        this.kind = kind.intern();
        this.id = id;
    }

    /**
     * @return the id associated with this key.
     */
    public Object getId() {
        return id;
    }

    /**
     * @return the collection-name.
     */
    public String getKind() {
        return kind;
    }

    /**
     * sets the collection-name.
     */
    public void setKind(final String newKind) {
        kind = newKind.intern();
    }

    public void setKindClass(final Class<? extends T> clazz) {
        kindClass = clazz;
    }

    public Class<? extends T> getKindClass() {
        return kindClass;
    }

    private void checkState(final Key k) {
        if (k.kindClass == null && k.kind == null) {
            throw new IllegalStateException("Kind must be specified (or a class).");
        }
        if (k.id == null && k.idBytes == null) {
            throw new IllegalStateException("id must be specified");
        }
    }

    /**
     * <p> Compares based on the following traits, in order: </p> <ol> <li>kind/kindClass</li> <li>parent</li> <li>id or name</li> </ol>
     */
    public int compareTo(final Key<?> other) {
        checkState(this);
        checkState(other);

        int cmp;
        // First kind
        if (other.kindClass != null && kindClass != null) {
            cmp = kindClass.getName().compareTo(other.kindClass.getName());
            if (cmp != 0) {
                return cmp;
            }
        }
        cmp = compareNullable(kind, other.kind);
        if (cmp != 0) {
            return cmp;
        }

        try {
            cmp = compareNullable((Comparable<?>) id, (Comparable<?>) other.id);
            if (cmp != 0) {
                return cmp;
            }
        } catch (Exception e) {
            // Not a comparable, use equals and String.compareTo().
            cmp = id.equals(other.id) ? 0 : 1;
            if (cmp != 0) {
                return id.toString().compareTo(other.id.toString());
            }
        }

        return 0;
    }

    /** */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Key<?>)) {
            return false;
        }

        return compareTo((Key<?>) obj) == 0;
    }

    /** */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Creates a human-readable version of this key
     */
    @Override
    public String toString() {
        final StringBuilder bld = new StringBuilder("Key{");

        if (kind != null) {
            bld.append("kind=");
            bld.append(kind);
        } else {
            bld.append("kindClass=");
            bld.append(kindClass.getName());
        }
        bld.append(", id=");
        bld.append(id);
        bld.append("}");

        return bld.toString();
    }

    /** */
    @SuppressWarnings("unchecked")
    private static int compareNullable(final Comparable o1, final Comparable o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        } else if (o2 == null) {
            return 1;
        } else {
            return o1.compareTo(o2);
        }
    }

    // private void writeObject(java.io.ObjectOutputStream out) throws
    // IOException {
    // if (!(id instanceof Serializable))
    // throw new NotSerializableException(id.getClass().getName());
    // // TODO persist id to a BasicDBObject (or Map<String, Object>) using
    // // mapper to make serializable.
    // out.defaultWriteObject();
    // }
    // private void readObject(java.io.ObjectInputStream in) throws IOException
    // {
    // if (!(id instanceof Serializable))
    // throw new NotSerializableException(id.getClass().getName());
    // // TODO persist id to a BasicDBObject (or Map<String, Object>) using
    // // mapper to make serializable.
    // in.defaultWriteObject();
    // }
}