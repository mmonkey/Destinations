package com.github.mmonkey.destinations.entities;

import com.google.common.base.Preconditions;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@DynamicUpdate
@Table(name = "warps", uniqueConstraints = {
        @UniqueConstraint(columnNames = "warp_id"), @UniqueConstraint(columnNames = "name")
})
@NamedQueries({
        @NamedQuery(name = "getWarps", query = "from WarpEntity")
})
public class WarpEntity implements Serializable {

    private static final long serialVersionUID = 8469390755174027818L;

    @Id
    @Column(name = "warp_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "is_private")
    private boolean isPrivate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private PlayerEntity owner;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<AccessEntity> access;

    /**
     * WarpEntity default constructor
     */
    public WarpEntity() {
    }

    /**
     * WarpEntity constructor
     *
     * @param name      String
     * @param isPrivate boolean
     * @param location  LocationEntity
     * @param owner     PlayerEntity
     */
    public WarpEntity(String name, boolean isPrivate, LocationEntity location, PlayerEntity owner) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(location);
        Preconditions.checkNotNull(owner);

        this.name = name;
        this.isPrivate = isPrivate;
        this.location = location;
        this.owner = owner;
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
     * @return boolean
     */
    public boolean isPrivate() {
        return isPrivate;
    }

    /**
     * @param isPrivate boolean
     */
    public void setPrivate(boolean isPrivate) {
        isPrivate = isPrivate;
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

    /**
     * @return PlayerEntity
     */
    public PlayerEntity getOwner() {
        return owner;
    }

    /**
     * @param owner PlayerEntity
     */
    public void setOwner(PlayerEntity owner) {
        Preconditions.checkNotNull(owner);

        this.owner = owner;
    }

    /**
     * @return Set<AccessEntity>
     */
    public Set<AccessEntity> getAccess() {
        return access;
    }

    /**
     * @param access Set<AccessEntity>
     */
    public void setAccess(Set<AccessEntity> access) {
        Preconditions.checkNotNull(access);

        this.access = access;
    }

}
