package com.github.mmonkey.destinations.entities;

import com.google.common.base.Preconditions;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@DynamicUpdate
@Table(name = "homes", uniqueConstraints = {
        @UniqueConstraint(columnNames = "home_id")
})
public class HomeEntity implements Serializable {

    private static final long serialVersionUID = -3949473855412057897L;

    @Id
    @Column(name = "home_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;

    /**
     * HomeEntity default constructor
     */
    public HomeEntity() {
    }

    /**
     * HomeEntity constructor
     *
     * @param name     String
     * @param location LocationEntity
     */
    public HomeEntity(String name, LocationEntity location) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(location);

        this.name = name;
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
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * @param name String
     */
    public void setName(String name) {
        Preconditions.checkNotNull(name);

        this.name = name;
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
