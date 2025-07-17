package org.acme.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Base entity class providing common properties for all domain entities.
 * Implements basic functionality for id, creation and update timestamps.
 * 
 * @author Davon Library System
 * @version 1.0
 */
public abstract class BaseEntity {
    
    /**
     * Unique identifier for the entity
     */
    private Long id;
    
    /**
     * Timestamp when the entity was created
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the entity was last updated
     */
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor initializing creation timestamp
     */
    public BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with id parameter
     * 
     * @param id the unique identifier
     */
    public BaseEntity(Long id) {
        this();
        this.id = id;
    }
    
    // Getters and Setters
    
    /**
     * Gets the unique identifier
     * 
     * @return the id
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the unique identifier
     * 
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the creation timestamp
     * 
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the creation timestamp
     * 
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the last update timestamp
     * 
     * @return the last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Sets the last update timestamp
     * 
     * @param updatedAt the last update timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Updates the last modified timestamp to current time
     */
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 