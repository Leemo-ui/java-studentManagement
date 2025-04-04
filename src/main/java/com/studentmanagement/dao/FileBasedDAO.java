package com.studentmanagement.dao;

import java.util.List;

/**
 * Base interface for file-based DAO operations
 * @param <T> The type of entity
 * @param <ID> The type of the entity's ID
 */
public interface FileBasedDAO<T, ID> {
    /**
     * Save an entity to file
     * @param entity The entity to save
     * @return The saved entity with generated ID
     */
    T save(T entity) throws Exception;

    /**
     * Update an existing entity in file
     * @param entity The entity to update
     * @return The updated entity
     */
    T update(T entity) throws Exception;

    /**
     * Delete an entity by ID
     * @param id The ID of the entity to delete
     */
    void delete(ID id) throws Exception;

    /**
     * Find an entity by ID
     * @param id The ID of the entity to find
     * @return The found entity or null if not found
     */
    T findById(ID id) throws Exception;

    /**
     * Get all entities
     * @return List of all entities
     */
    List<T> findAll() throws Exception;
} 