package ee.filmsociety.moviesapi.repository;

import ee.filmsociety.moviesapi.entity.Actor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorRepository extends JpaRepository<Actor, Long> {

    // Backs GET /api/actors?name=... (case-insensitive partial match)
    Page<Actor> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
