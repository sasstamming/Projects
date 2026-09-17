package ee.filmsociety.moviesapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "genres")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Genre name must not be blank")
    @Size(max = 100, message = "Genre name must be at most 100 characters")
    @Column(nullable = false)
    private String name;

    /**
     * Inverse side of the Movie<->Genre relationship.
     * Movie is the OWNING side (it declares @JoinTable), so it controls the
     * join-table rows. Changes made only here are NOT persisted.
     */
    @ManyToMany(mappedBy = "genres")
    private Set<Movie> movies = new HashSet<>();

    public Genre() {
    }

    public Genre(String name) {
        this.name = name;
    }

    // id is intentionally immutable: getter only, no setter.
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Movie> getMovies() {
        return movies;
    }

    public void setMovies(Set<Movie> movies) {
        this.movies = movies;
    }
}
