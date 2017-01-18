package com.github.mmonkey.destinations.persistence.repositories;

import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.google.common.base.Preconditions;
import org.hibernate.Session;
import org.spongepowered.api.entity.living.player.Player;

import javax.persistence.Query;
import java.util.Optional;

public class PlayerRepository extends EntityRepository<PlayerEntity> {

    public static final PlayerRepository instance = new PlayerRepository();

    /**
     * Get a PlayerEntity by Player
     *
     * @param player Player
     * @return PlayerEntity
     */
    public Optional<PlayerEntity> get(Player player) {
        return this.get(player.getIdentifier());
    }

    /**
     * Get a PlayerEntity by identifier
     *
     * @param identifier String
     * @return PlayerEntity
     */
    public Optional<PlayerEntity> get(String identifier) {
        Preconditions.checkNotNull(identifier);

        Session session = this.sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createNamedQuery("getPlayer", PlayerEntity.class);
        query.setParameter("identifier", identifier);

        try {
            PlayerEntity result = (PlayerEntity) query.getSingleResult();
            session.close();
            return Optional.of(result);
        } catch (Exception e) {
            session.close();
            return Optional.empty();
        }
    }

    /**
     * Get a PlayerEntity with BackEntities by Player
     *
     * @param player Player
     * @return PlayerEntity
     */
    public Optional<PlayerEntity> getWithBacks(Player player) {
        return this.getWithBacks(player.getIdentifier());
    }

    /**
     * Get a PlayerEntity with BackEntities by identifier
     *
     * @param identifier String
     * @return PlayerEntity
     */
    public Optional<PlayerEntity> getWithBacks(String identifier) {
        Preconditions.checkNotNull(identifier);

        Session session = this.sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createNamedQuery("getPlayerBacks", PlayerEntity.class);
        query.setParameter("identifier", identifier);

        try {
            PlayerEntity result = (PlayerEntity) query.getSingleResult();
            session.close();
            return Optional.of(result);
        } catch (Exception e) {
            session.close();
            return Optional.empty();
        }
    }

    /**
     * Get a PlayerEntity with BedEntities by Player
     *
     * @param player Player
     * @return PlayerEntity
     */
    public Optional<PlayerEntity> getWithBeds(Player player) {
        return this.getWithBeds(player.getIdentifier());
    }

    /**
     * Get a PlayerEntity with BedEntities by identifier
     *
     * @param identifier String
     * @return PlayerEntity
     */
    public Optional<PlayerEntity> getWithBeds(String identifier) {
        Preconditions.checkNotNull(identifier);

        Session session = this.sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createNamedQuery("getPlayerBeds", PlayerEntity.class);
        query.setParameter("identifier", identifier);

        try {
            PlayerEntity result = (PlayerEntity) query.getSingleResult();
            session.close();
            return Optional.of(result);
        } catch (Exception e) {
            session.close();
            return Optional.empty();
        }
    }

    /**
     * Get a PlayerEntity with HomeEntities by Player
     *
     * @param player Player
     * @return PlayerEntity
     */
    public Optional<PlayerEntity> getWithHomes(Player player) {
        return this.getWithHomes(player.getIdentifier());
    }

    /**
     * Get a PlayerEntity with HomeEntities by identifier
     *
     * @param identifier String
     * @return PlayerEntity
     */
    public Optional<PlayerEntity> getWithHomes(String identifier) {
        Preconditions.checkNotNull(identifier);

        Session session = this.sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createNamedQuery("getPlayerHomes", PlayerEntity.class);
        query.setParameter("identifier", identifier);

        try {
            PlayerEntity result = (PlayerEntity) query.getSingleResult();
            session.close();
            return Optional.of(result);
        } catch (Exception e) {
            session.close();
            return Optional.empty();
        }
    }

}
