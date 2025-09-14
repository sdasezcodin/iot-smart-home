package com.smarthome.db;

import java.util.List;

/**
 * Generic Data Access Object (DAO) interface
 * defining standard CRUD operations.
 *
 * @param <T> the type of entity this DAO manages
 */
public interface ApplianceDAO<T> {

    /**
     * Persists a new entity in the database.
     *
     * @param entity the entity to save
     */
    void save(T entity);

    /**
     * Updates an existing entity in the database.
     *
     * @param entity the entity with updated data
     */
    void update(T entity);

    /**
     * Retrieves an entity by its unique identifier.
     *
     * @param id the unique ID of the entity
     * @return the matching entity, or {@code null} if not found
     */
    T findById(String id);

    /**
     * Fetches all entities of this type from the database.
     *
     * @return list of all entities
     */
    List<T> findAll();

    /**
     * Removes an entity by its unique identifier.
     *
     * @param id the unique ID of the entity to delete
     */
    void deleteById(String id);
}
