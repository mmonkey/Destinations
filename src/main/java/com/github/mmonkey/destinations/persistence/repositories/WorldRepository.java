package com.github.mmonkey.destinations.persistence.repositories;

import com.github.mmonkey.destinations.entities.WorldEntity;
import com.google.common.base.Preconditions;
import org.hibernate.Session;

import javax.persistence.Query;
import java.util.Optional;

public class WorldRepository extends EntityRepository<WorldEntity> {

    public static final WorldRepository instance = new WorldRepository();

    /**
     * Get a PlayerEntity by identifier
     *
     * @param identifier String
     * @return PlayerEntity
     */
    public Optional<WorldEntity> get(String identifier) {
        Preconditions.checkNotNull(identifier);

        Session session = this.sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createNamedQuery("getWorldByIdentifier", WorldEntity.class);
        query.setParameter("identifier", identifier);

        try {
            WorldEntity result = (WorldEntity) query.getSingleResult();
            session.close();
            return Optional.of(result);
        } catch (Exception e) {
            session.close();
            return Optional.empty();
        }
    }

}
