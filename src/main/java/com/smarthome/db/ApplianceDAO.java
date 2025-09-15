package com.smarthome.db;

import java.util.List;

/**
 * Generic Data Access Object (DAO) interface.
 * <p>
 * This interface defines the contract for standard database operations (CRUD - Create, Read, Update, Delete)
 * that can be performed on any type of entity. It promotes loose coupling between the business logic
 * and the data persistence layer.
 *
 * @param <T> The type of the entity this DAO manages, e.g., Appliance, SensorData.
 */
public interface ApplianceDAO<T> {

    /**
     * Persists a new entity in the database.
     * <p>
     * This method saves the provided entity object to the underlying data store.
     *
     * @param entity The entity objects to be saved. It should not be null.
     */
    void save(T entity);

    /**
     * Updates an existing entity in the database.
     * <p>
     * This method modifies the record of an existing entity based on its unique identifier.
     *
     * @param entity The entity with updated data. The entity's unique ID must be set to
     * identify the record to be updated.
     */
    void update(T entity);

    /**
     * Retrieves an entity from the database by its unique identifier.
     *
     * @param id The unique identifier (primary key) of the entity to retrieve.
     * @return The matching entity object, or {@code null} if no entity with the given ID is found.
     */
    T findById(String id);

    /**
     * Fetches all entities of this type from the database.
     * <p>
     * Note: For large datasets, this operation can be resource-intensive and may
     * not be suitable for production environments.
     *
     * @return A {@link List} of all entities of type T found in the database.
     * The list will be empty if no entities are present.
     */
    List<T> findAll();

    /**
     * Removes an entity from the database using its unique identifier.
     *
     * @param id The unique identifier of the entity to be deleted.
     */
    void deleteById(String id);
}