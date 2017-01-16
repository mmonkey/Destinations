package com.github.mmonkey.destinations.entities;

import com.google.common.base.Preconditions;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@DynamicUpdate
@Table(name = "access", uniqueConstraints = {
        @UniqueConstraint(columnNames = "access_id")
})
public class AccessEntity implements Serializable {

    private static final long serialVersionUID = -6492016883124144444L;

    @Id
    @Column(name = "access_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "can_administrate")
    private boolean canAdministrate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "player_id", nullable = false)
    private PlayerEntity player;

    /**
     * AccessEntity default constructor
     */
    public AccessEntity() {
    }

    /**
     * AccessEntity constructor
     *
     * @param canAdministrate boolean
     * @param player          PlayerEntity
     */
    public AccessEntity(boolean canAdministrate, PlayerEntity player) {
        Preconditions.checkNotNull(canAdministrate);
        Preconditions.checkNotNull(player);

        this.canAdministrate = canAdministrate;
        this.player = player;
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
     * @return boolean
     */
    public boolean isCanAdministrate() {
        return canAdministrate;
    }

    /**
     * @param canAdministrate boolean
     */
    public void setCanAdministrate(boolean canAdministrate) {
        this.canAdministrate = canAdministrate;
    }

    /**
     * @return PlayerEntity
     */
    public PlayerEntity getPlayer() {
        return player;
    }

    /**
     * @param player PlayerEntity
     */
    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }

}
