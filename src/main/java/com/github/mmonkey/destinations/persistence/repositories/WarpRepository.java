package com.github.mmonkey.destinations.persistence.repositories;

import com.github.mmonkey.destinations.entities.WarpEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

public class WarpRepository extends EntityRepository<WarpEntity> {

    public static final WarpRepository instance = new WarpRepository();

    /**
     * Get all warps
     *
     * @return List<WarpEntity>
     */
    public List<WarpEntity> getAllWarps() {

        Session session = this.sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createNamedQuery("getWarps", WarpEntity.class);

        try {
            List<WarpEntity> result = query.getResultList();
            session.close();
            return result;
        } catch (Exception e) {
            session.close();
            return Collections.emptyList();
        }
    }

}
