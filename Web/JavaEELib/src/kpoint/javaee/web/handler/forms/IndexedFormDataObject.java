/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.web.handler.forms;

/**
 *
 * @author Steven
 */
public abstract class IndexedFormDataObject<T extends Number> extends FormDataObject
{
    protected Long id;
    private T keyId;

    public IndexedFormDataObject(Long id, T keyId) {
        this.id = id;
        this.keyId = keyId;
    }

    public T getKeyId() {
        return this.keyId;
    }
    public boolean isNewRecord() {
        return (this.id == null);
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.keyId != null ? this.keyId.hashCode() : 0);
        return hash;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof IndexedFormDataObject)) return false;

        final IndexedFormDataObject other = (IndexedFormDataObject)obj;
        if (!this.keyId.equals(other.keyId))
            return false;

        return true;
    }
}
