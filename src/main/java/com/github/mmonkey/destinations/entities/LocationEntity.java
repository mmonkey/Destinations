package com.github.mmonkey.destinations.entities;

import com.flowpowered.math.vector.Vector3d;
import com.github.mmonkey.destinations.persistence.repositories.WorldRepository;
import com.google.common.base.Preconditions;
import org.hibernate.annotations.DynamicUpdate;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

@Entity
@DynamicUpdate
@Table(name = "locations", uniqueConstraints = {
        @UniqueConstraint(columnNames = "location_id")
})
public class LocationEntity implements Serializable {

    private static final long serialVersionUID = 1381761003213600752L;

    @Id
    @Column(name = "location_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "x", nullable = false)
    private Double x;

    @Column(name = "y", nullable = false)
    private Double y;

    @Column(name = "z", nullable = false)
    private Double z;

    @Column(name = "yaw")
    private Double yaw;

    @Column(name = "pitch")
    private Double pitch;

    @Column(name = "roll")
    private Double roll;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    @JoinColumn(name = "world_id", nullable = false)
    private WorldEntity world;

    /**
     * LocationEntity default constructor
     */
    public LocationEntity() {
    }

    /**
     * LocationEntity constructor
     *
     * @param entity WorldEntity
     */
    public LocationEntity(org.spongepowered.api.entity.Entity entity) {
        Preconditions.checkNotNull(entity);

        this.x = entity.getLocation().getX();
        this.y = entity.getLocation().getY();
        this.z = entity.getLocation().getZ();
        this.yaw = entity.getRotation().getX();
        this.pitch = entity.getRotation().getY();
        this.roll = entity.getRotation().getZ();

        Optional<WorldEntity> optional = WorldRepository.instance.get(entity.getWorld().getUniqueId().toString());
        this.world = optional.orElseGet(() -> WorldRepository.instance.save(new WorldEntity(entity.getWorld())));
    }

    /**
     * LocationEntity constructor
     *
     * @param location Location<World>
     */
    public LocationEntity(Location<World> location) {
        Preconditions.checkNotNull(location);

        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();

        Optional<WorldEntity> optional = WorldRepository.instance.get(location.getExtent().getUniqueId().toString());
        this.world = optional.orElseGet(() -> WorldRepository.instance.save(new WorldEntity(location.getExtent())));
    }

    /**
     * LocationEntity constructor
     *
     * @param location Location<World>
     */
    public LocationEntity(Location<World> location, Vector3d rotation) {
        Preconditions.checkNotNull(location);

        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = rotation.getX();
        this.pitch = rotation.getY();
        this.roll = rotation.getZ();

        Optional<WorldEntity> optional = WorldRepository.instance.get(location.getExtent().getUniqueId().toString());
        this.world = optional.orElseGet(() -> WorldRepository.instance.save(new WorldEntity(location.getExtent())));
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
     * @return Double
     */
    public Double getX() {
        return x;
    }

    /**
     * @param x Double
     */
    public void setX(Double x) {
        Preconditions.checkNotNull(x);

        this.x = x;
    }

    /**
     * @return Double
     */
    public Double getY() {
        return y;
    }

    /**
     * @param y Double
     */
    public void setY(Double y) {
        Preconditions.checkNotNull(y);

        this.y = y;
    }

    /**
     * @return Double
     */
    public Double getZ() {
        return z;
    }

    /**
     * @param z Double
     */
    public void setZ(Double z) {
        Preconditions.checkNotNull(z);

        this.z = z;
    }

    /**
     * @return Double
     */
    public Double getYaw() {
        return yaw;
    }

    /**
     * @param yaw Double
     */
    public void setYaw(Double yaw) {
        this.yaw = yaw;
    }

    /**
     * @return Double
     */
    public Double getPitch() {
        return pitch;
    }

    /**
     * @param pitch Double
     */
    public void setPitch(Double pitch) {
        this.pitch = pitch;
    }

    /**
     * @return Double
     */
    public Double getRoll() {
        return roll;
    }

    /**
     * @param roll Double
     */
    public void setRoll(Double roll) {
        this.roll = roll;
    }

    /**
     * @return WorldEntity
     */
    public WorldEntity getWorld() {
        return world;
    }

    /**
     * Get this Rotation
     *
     * @return Vector3d
     */
    public Vector3d getRotation() {
        return (this.yaw == null || this.pitch == null || this.roll == null) ? new Vector3d(0, 0, 0) : new Vector3d(this.yaw, this.pitch, this.roll);
    }

    /**
     * Get this Location
     *
     * @return Location<World>
     */
    public Location<World> getLocation() {
        Optional<World> optional = Sponge.getGame().getServer().getWorld(UUID.fromString(this.getWorld().getIdentifier()));
        return optional.map(world -> new Location<>(world, this.x, this.y, this.z)).orElse(null);
    }

}
