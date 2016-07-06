/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.server.core.persistence;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author Steven
 */
@MappedSuperclass
public abstract class SimpleEntityBean implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        if (this.id != null)
            return id.hashCode();

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SimpleEntityBean))
            return false;

        if (this.id == null)
            return false;

        SimpleEntityBean bean = (SimpleEntityBean)obj;
        return this.id.equals(bean.id);
    }

    @Override
    public abstract SimpleEntityBean clone();
}
