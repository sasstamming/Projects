package ee.filmsociety.moviesapi.config;

import ee.filmsociety.moviesapi.entity.Actor;
import ee.filmsociety.moviesapi.entity.Genre;
import ee.filmsociety.moviesapi.entity.Movie;
import ee.filmsociety.moviesapi.repository.ActorRepository;
import ee.filmsociety.moviesapi.repository.GenreRepository;
import ee.filmsociety.moviesapi.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Seeds a demo dataset on first startup. Runs only when the genres table is
 * empty, so it won't duplicate data on subsequent runs.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    private final Map<String, Genre> genres = new LinkedHashMap<>();
    private final Map<String, Actor> actors = new LinkedHashMap<>();

    public DataLoader(GenreRepository genreRepository,
                      ActorRepository actorRepository,
                      MovieRepository movieRepository) {
        this.genreRepository = genreRepository;
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (genreRepository.count() > 0) {
            return; // already seeded
        }

        seedGenres("Action", "Sci-Fi", "Thriller", "Drama", "Comedy", "Crime", "Romance", "Adventure");

        seedActor("Keanu Reeves", "1964-09-02");
        seedActor("Laurence Fishburne", "1961-07-30");
        seedActor("Carrie-Anne Moss", "1967-08-21");
        seedActor("Leonardo DiCaprio", "1974-11-11");
        seedActor("Tom Hardy", "1977-09-15");
        seedActor("Elliot Page", "1987-02-21");
        seedActor("Bruce Willis", "1955-03-19");
        seedActor("Alan Rickman", "1946-02-21");
        seedActor("Tom Hanks", "1956-07-09");
        seedActor("Meryl Streep", "1949-06-22");
        seedActor("Brad Pitt", "1963-12-18");
        seedActor("Edward Norton", "1969-08-18");
        seedActor("Morgan Freeman", "1937-06-01");
        seedActor("Tim Robbins", "1958-10-16");
        seedActor("Christian Bale", "1974-01-30");
        seedActor("Heath Ledger", "1979-04-04");
        seedActor("Scarlett Johansson", "1984-11-22");
        seedActor("Samuel L. Jackson", "1948-12-21");
        seedActor("Uma Thurman", "1970-04-29");
        seedActor("John Travolta", "1954-02-18");

        seedMovie("The Matrix", 1999, 136,
                new String[]{"Action", "Sci-Fi"},
                new String[]{"Keanu Reeves", "Laurence Fishburne", "Carrie-Anne Moss"});
        seedMovie("The Matrix Reloaded", 2003, 138,
                new String[]{"Action", "Sci-Fi"},
                new String[]{"Keanu Reeves", "Laurence Fishburne", "Carrie-Anne Moss"});
        seedMovie("Inception", 2010, 148,
                new String[]{"Action", "Sci-Fi", "Thriller"},
                new String[]{"Leonardo DiCaprio", "Tom Hardy", "Elliot Page"});
        seedMovie("Die Hard", 1988, 132,
                new String[]{"Action", "Thriller"},
                new String[]{"Bruce Willis", "Alan Rickman"});
        seedMovie("Forrest Gump", 1994, 142,
                new String[]{"Drama", "Romance"},
                new String[]{"Tom Hanks"});
        seedMovie("The Devil Wears Prada", 2006, 109,
                new String[]{"Comedy", "Drama"},
                new String[]{"Meryl Streep"});
        seedMovie("Mamma Mia!", 2008, 108,
                new String[]{"Comedy", "Romance"},
                new String[]{"Meryl Streep"});
        seedMovie("The Iron Lady", 2011, 105,
                new String[]{"Drama"},
                new String[]{"Meryl Streep"});
        seedMovie("Fight Club", 1999, 139,
                new String[]{"Drama", "Thriller"},
                new String[]{"Brad Pitt", "Edward Norton"});
        seedMovie("Se7en", 1995, 127,
                new String[]{"Crime", "Thriller", "Drama"},
                new String[]{"Brad Pitt", "Morgan Freeman"});
        seedMovie("The Shawshank Redemption", 1994, 142,
                new String[]{"Drama", "Crime"},
                new String[]{"Morgan Freeman", "Tim Robbins"});
        seedMovie("The Dark Knight", 2008, 152,
                new String[]{"Action", "Crime", "Drama"},
                new String[]{"Christian Bale", "Heath Ledger", "Morgan Freeman"});
        seedMovie("Batman Begins", 2005, 140,
                new String[]{"Action", "Adventure"},
                new String[]{"Christian Bale", "Morgan Freeman"});
        seedMovie("Pulp Fiction", 1994, 154,
                new String[]{"Crime", "Drama"},
                new String[]{"Samuel L. Jackson", "Uma Thurman", "John Travolta", "Bruce Willis"});
        seedMovie("Kill Bill: Vol. 1", 2003, 111,
                new String[]{"Action", "Crime", "Thriller"},
                new String[]{"Uma Thurman"});
        seedMovie("Lost in Translation", 2003, 102,
                new String[]{"Drama", "Comedy", "Romance"},
                new String[]{"Scarlett Johansson"});
        seedMovie("The Avengers", 2012, 143,
                new String[]{"Action", "Adventure", "Sci-Fi"},
                new String[]{"Scarlett Johansson", "Samuel L. Jackson"});
        seedMovie("Mad Max: Fury Road", 2015, 120,
                new String[]{"Action", "Adventure", "Sci-Fi"},
                new String[]{"Tom Hardy"});
        seedMovie("The Wolf of Wall Street", 2013, 180,
                new String[]{"Drama", "Comedy", "Crime"},
                new String[]{"Leonardo DiCaprio"});
        seedMovie("Cast Away", 2000, 143,
                new String[]{"Drama", "Adventure"},
                new String[]{"Tom Hanks"});
        seedMovie("John Wick", 2014, 101,
                new String[]{"Action", "Thriller"},
                new String[]{"Keanu Reeves"});
        seedMovie("Titanic", 1997, 194,
                new String[]{"Drama", "Romance"},
                new String[]{"Leonardo DiCaprio"});
    }

    private void seedGenres(String... names) {
        for (String name : names) {
            genres.put(name, genreRepository.save(new Genre(name)));
        }
    }

    private void seedActor(String name, String isoBirthDate) {
        actors.put(name, actorRepository.save(new Actor(name, LocalDate.parse(isoBirthDate))));
    }

    private void seedMovie(String title, int year, int duration, String[] genreNames, String[] actorNames) {
        Movie movie = new Movie(title, year, duration);
        for (String g : genreNames) {
            movie.addGenre(genres.get(g));
        }
        for (String a : actorNames) {
            movie.addActor(actors.get(a));
        }
        movieRepository.save(movie);
    }
}
