package com.github.mmonkey.destinations.persistence.repositories;

import com.github.mmonkey.destinations.persistence.PersistenceService;
import com.google.common.base.Preconditions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public abstract class EntityRepository<Entity> {

    final SessionFactory sessionFactory;

    /**
     * EntityRepository constructor
     */
    EntityRepository() {
        this.sessionFactory = PersistenceService.getInstance().getSessionFactory();
    }

    /**
     * Save this entity
     *
     * @param entity Entity to save
     * @return the saved Entity
     */
    public Entity save(Entity entity) {
        Preconditions.checkNotNull(entity);

        Transaction transaction = null;
        try (Session session = this.sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(entity);
            session.flush();
            transaction.commit();
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
            return null;
        }
    }

    /**
     * Remove this entity
     *
     * @param entity Entity to remove
     * @return result of the removal
     */
    public boolean remove(Entity entity) {
        Preconditions.checkNotNull(entity);

        Transaction transaction = null;
        try (Session session = this.sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(entity);
            session.flush();
            transaction.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
            return false;
        }
    }

}
