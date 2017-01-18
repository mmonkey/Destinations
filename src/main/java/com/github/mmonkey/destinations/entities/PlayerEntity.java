package com.github.mmonkey.destinations.entities;

import com.google.common.base.Preconditions;
import org.hibernate.annotations.DynamicUpdate;
import org.spongepowered.api.entity.living.player.Player;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@DynamicUpdate
@Table(name = "players", uniqueConstraints = {
        @UniqueConstraint(columnNames = "player_id")
})
@NamedQueries({
        @NamedQuery(name = "getPlayer", query = "from PlayerEntity p where p.identifier = :identifier"),
        @NamedQuery(name = "getPlayerBacks", query = "select p from PlayerEntity p join fetch p.backs where p.identifier = :identifier"),
        @NamedQuery(name = "getPlayerBeds", query = "select p from PlayerEntity p join fetch p.beds where p.identifier = :identifier"),
        @NamedQuery(name = "getPlayerHomes", query = "select p from PlayerEntity p join fetch p.homes where p.identifier = :identifier")
})
public class PlayerEntity implements Serializable {

    private static final long serialVersionUID = -6211408372176281682L;

    @Id
    @Column(name = "player_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "identifier", unique = true, nullable = false)
    private String identifier;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BackEntity> backs = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BedEntity> beds = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<HomeEntity> homes = new HashSet<>();

    /**
     * PlayerEntity default constructor
     */
    public PlayerEntity() {
    }

    /**
     * PlayerEntity constructor
     *
     * @param player Player
     */
    public PlayerEntity(Player player) {
        Preconditions.checkNotNull(player);

        this.identifier = player.getIdentifier();
        this.name = player.getName();
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
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier String
     */
    public void setIdentifier(String identifier) {
        Preconditions.checkNotNull(identifier);

        this.identifier = identifier;
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
     * @return Set<BackEntity>
     */
    public Set<BackEntity> getBacks() {
        return backs;
    }

    /**
     * @param backs Set<BackEntity>
     */
    public void setBacks(Set<BackEntity> backs) {
        Preconditions.checkNotNull(backs);

        this.backs = backs;
    }

    /**
     * @return Set<BedEntity>
     */
    public Set<BedEntity> getBeds() {
        return beds;
    }

    /**
     * @param beds Set<BedEntity>
     */
    public void setBeds(Set<BedEntity> beds) {
        Preconditions.checkNotNull(beds);

        this.beds = beds;
    }

    /**
     * @return Set<HomeEntity>
     */
    public Set<HomeEntity> getHomes() {
        return homes;
    }

    /**
     * @param homes Set<HomeEntity>
     */
    public void setHomes(Set<HomeEntity> homes) {
        Preconditions.checkNotNull(homes);

        this.homes = homes;
    }
}
