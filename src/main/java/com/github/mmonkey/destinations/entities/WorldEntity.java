package com.github.mmonkey.destinations.entities;

import com.google.common.base.Preconditions;
import org.hibernate.annotations.DynamicUpdate;
import org.spongepowered.api.world.World;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@DynamicUpdate
@Table(name = "worlds", uniqueConstraints = {
        @UniqueConstraint(columnNames = "world_id")
})
@NamedQueries({
        @NamedQuery(name = "getWorldByIdentifier", query = "from WorldEntity w where w.identifier = :identifier"),
})
public class WorldEntity implements Serializable {

    private static final long serialVersionUID = -458418606185755346L;

    @Id
    @Column(name = "world_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "identifier", unique = true, nullable = false)
    private String identifier;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    /**
     * WorldEntity default constructor
     */
    public WorldEntity() {
    }

    /**
     * @param world World
     */
    public WorldEntity(World world) {
        Preconditions.checkNotNull(world);

        this.identifier = world.getUniqueId().toString();
        this.name = world.getName();
    }

    /**
     * @return long
     */
    public long getId() {
        return this.id;
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
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * @param identifier UUID
     */
    public void setIdentifier(String identifier) {
        Preconditions.checkNotNull(identifier);

        this.identifier = identifier;
    }

    /**
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name String
     */
    public void setName(String name) {
        Preconditions.checkNotNull(name);

        this.name = name;
    }

}
