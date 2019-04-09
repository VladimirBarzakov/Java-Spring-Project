/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.entities.base;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@MappedSuperclass
public class BaseEntityLongID {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private long id;

    public BaseEntityLongID() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
