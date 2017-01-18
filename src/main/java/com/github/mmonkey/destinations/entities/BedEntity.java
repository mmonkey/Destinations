package com.github.mmonkey.destinations.entities;

import com.google.common.base.Preconditions;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@DynamicUpdate
@Table(name = "beds", uniqueConstraints = {
        @UniqueConstraint(columnNames = "bed_id")
})
public class BedEntity implements Serializable {

    private static final long serialVersionUID = -6115854156991482893L;

    @Id
    @Column(name = "bed_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "last_use", nullable = false)
    private Timestamp lastUse;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;

    /**
     * BedEntity default constructor
     */
    public BedEntity() {
    }

    /**
     * BedEntity constructor
     *
     * @param location LocationEntity
     */
    public BedEntity(LocationEntity location) {
        Preconditions.checkNotNull(location);

        this.lastUse = new Timestamp(new Date().getTime());
        this.location = location;
    }

    /**
     * @return long
     */
    public long getId() {
        return id;
    }

    /**
     * @param id long
     */
    public void setId(long id) {
        Preconditions.checkNotNull(id);

        this.id = id;
    }

    /**
     * @return Timestamp
     */
    public Timestamp getLastUse() {
        return lastUse;
    }

    /**
     * @param lastUse Timestamp
     */
    public void setLastUse(Timestamp lastUse) {
        Preconditions.checkNotNull(lastUse);

        this.lastUse = lastUse;
    }

    /**
     * @return LocationEntity
     */
    public LocationEntity getLocation() {
        return location;
    }

    /**
     * @param location LocationEntity
     */
    public void setLocation(LocationEntity location) {
        Preconditions.checkNotNull(location);

        this.location = location;
    }

}
