package com.github.mmonkey.destinations.entities;

import com.flowpowered.math.vector.Vector3d;
import com.github.mmonkey.destinations.persistence.repositories.WorldRepository;
import com.google.common.base.Preconditions;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Optional;

@Entity
@DynamicUpdate
@Table(name = "spawns", uniqueConstraints = {
        @UniqueConstraint(columnNames = "spawn_id")
})
@NamedQueries({
        @NamedQuery(name = "getSpawns", query = "from SpawnEntity")
})
public class SpawnEntity implements Serializable {

    private static final long serialVersionUID = -396101307042415790L;

    @Id
    @Column(name = "spawn_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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
     * SpawnEntity default constructor
     */
    public SpawnEntity() {
    }

    /**
     * SpawnEntity Constructor
     *
     * @param entity Entity
     */
    public SpawnEntity(org.spongepowered.api.entity.Entity entity) {
        Preconditions.checkNotNull(entity);

        this.yaw = entity.getRotation().getX();
        this.pitch = entity.getRotation().getY();
        this.roll = entity.getRotation().getZ();

        Optional<WorldEntity> optional = WorldRepository.instance.get(entity.getWorld().getUniqueId().toString());
        this.world = optional.orElseGet(() -> WorldRepository.instance.save(new WorldEntity(entity.getWorld())));
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
    public Double getYaw() {
        return yaw;
    }

    /**
     * @param yaw Double
     */
    public void setYaw(Double yaw) {
        Preconditions.checkNotNull(yaw);

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
        Preconditions.checkNotNull(pitch);

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
        Preconditions.checkNotNull(roll);

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

}
