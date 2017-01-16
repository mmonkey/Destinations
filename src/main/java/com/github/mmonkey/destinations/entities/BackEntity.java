package com.github.mmonkey.destinations.entities;

import com.google.common.base.Preconditions;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@DynamicUpdate
@Table(name = "backs", uniqueConstraints = {
        @UniqueConstraint(columnNames = "back_id")
})
public class BackEntity implements Serializable {

    private static final long serialVersionUID = -4693508387568138195L;

    @Id
    @Column(name = "back_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "created", nullable = false)
    private Timestamp created;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;

    /**
     * BackEntity default constructor
     */
    public BackEntity() {
    }

    /**
     * BackEntity constructor
     *
     * @param location LocationEntity
     */
    public BackEntity(LocationEntity location) {
        Preconditions.checkNotNull(location);

        this.created = new Timestamp(new Date().getTime());
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
    public Timestamp getCreated() {
        return created;
    }

    /**
     * @param created Timestamp
     */
    public void setCreated(Timestamp created) {
        Preconditions.checkNotNull(created);

        this.created = created;
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
