package com.github.mmonkey.destinations.persistence.repositories;

import com.github.mmonkey.destinations.entities.SpawnEntity;
import org.hibernate.Session;

import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

public class SpawnRepository extends EntityRepository<SpawnEntity> {

    public static final SpawnRepository instance = new SpawnRepository();

    /**
     * Get all spawns
     *
     * @return List<SpawnEntity>
     */
    public List<SpawnEntity> getAllSpawns() {

        Session session = this.sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createNamedQuery("getSpawns", SpawnEntity.class);

        try {
            List<SpawnEntity> result = query.getResultList();
            session.close();
            return result;
        } catch (Exception e) {
            session.close();
            return Collections.emptyList();
        }
    }

}
