package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.acme.model.Fine;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class FineRepository implements PanacheRepository<Fine> {

    public List<Fine> findByUserId(Long userId) {
        return list("userId", userId);
    }
}