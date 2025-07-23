package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.acme.model.Reservation;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ReservationRepository implements PanacheRepository<Reservation> {

    public List<Reservation> findByUserId(Long userId) {
        return list("user.id", userId);
    }
}