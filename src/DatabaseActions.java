import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;

/**
 * clasa ce contine metodele pentru actiunile de tip "database" - add si delete
 */
public final class DatabaseActions {
    private static DatabaseActions instance = null;
    private DatabaseActions() { }

    /**
     * Singleton pentru databaseActions
     * @return
     */
    public static DatabaseActions getInstance() {
        if (instance == null) {
            instance = new DatabaseActions();
        }
        return instance;
    }

    /**
     * metoda ce trateaza feature-urile posibile ale unei actiuni de tip "database"
     * @param currinfo
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    public void databaseMethod(final CurrInfo currinfo, final Actions action,
                                 final InputData inputdata, final ArrayNode output,
                                 final ObjectMapper objectMapper) {

        String feature = action.getFeature();
        switch (feature) {
            case "add":
                addMethod(currinfo, action, inputdata, output, objectMapper);
                break;
            case "delete":
                deleteMethod(currinfo, action, inputdata, output, objectMapper);
                break;
            case "default":
                break;
        }
    }

    /**
     * metoda generica ce elimina un film anume dintr-o lista de filme,
     * daca exista in acea lista (helpful later on)
     * @param movies - lista de filme in care caut
     * @param searchedMovie - filmul cautat
     * @return
     */
    public static int removeMovie(final ArrayList<Movie> movies, final String searchedMovie) {
        if (movies != null) {
            for (Movie movie : movies) {
                if (movie.getName().equals(searchedMovie)) {
                    movies.remove(movie);
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     * metoda ce adauga un film in baza de date
     * @param currinfo
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    public static void addMethod(final CurrInfo currinfo, final Actions action,
                                    final InputData inputdata, final ArrayNode output,
                                    final ObjectMapper objectMapper) {

        if (currinfo.getUser().getNotifications() == null) {
            currinfo.getUser().setNotifications(new ArrayList<Notification>());
        }

        // pot adauga un film in baza de date indiferent de unde ma aflu
        // daca exista deja in baza de date => eroare
        int exists = 0;
        for (Movie movie : inputdata.getMovies()) {
            if (movie.equals(action.getAddedMovie())) {
                exists = 1;
                GenerateOutput.printError(output, objectMapper);
                break;
            }
        }

        // daca filmul nu mai exista in baza de date, il pot adauga
        if (exists == 0) {
            inputdata.getMovies().add(action.getAddedMovie());
            // intai verific caror utilizatori le pot trimite notificarea
            outerloop:
            for (User user : inputdata.getUsers()) {
                int okCountry = 0;
                int subscribed = 0;
                // ma uit printre genurile filmului adaugat
                // verific daca user ul e abonat la cel putin unul din ele
                genresloop:
                for (String addedMovieGenre : action.getAddedMovie().getGenres()) {
                    if (user.getSubscribedGenres() != null) {
                        for (String subscribedGenre : user.getSubscribedGenres()) {
                            if (subscribedGenre.equals(addedMovieGenre)) {
                                subscribed = 1;
                                break;
                            }
                        }
                    } else {
                        subscribed = 0;
                        break;
                    }
                    // daca am gasit macar un gen la care e abonat user ul, ies din for ul mare
                    if (subscribed == 1) {
                        break genresloop;
                    }
                }

                if (subscribed == 1) {
                    // iterez si prin tarile banate ale filmului adaugat
                    // verific ca user ul sa nu se afle intr-o tara banata a filmului
                    if (action.getAddedMovie().getCountriesBanned() != null) {
                        for (String countryBanned : action.getAddedMovie().getCountriesBanned()) {
                            if (countryBanned.equals(user.getCredentials().getCountry())) {
                                GenerateOutput.printError(output, objectMapper);
                                okCountry = 0;
                                break outerloop;
                            } else {
                                okCountry = 1;
                                // daca e totul ok, adaug notificarea in lista de notificari
                                Notification newNotification = new Notification();
                                newNotification.setMovieName(action.getAddedMovie().getName());
                                newNotification.setMessage("ADD");

                                user.getNotifications().add(newNotification);
                                break;
                            }
                        }
                    } else {
                        // daca filmul nu e banat in nicio tara, notific fiecare user subscribed
                        okCountry = 1;
                        // daca e totul ok, adaug notificarea in lista de notificari
                        Notification addedMovieNotification = new Notification();
                        addedMovieNotification.setMovieName(action.getAddedMovie().getName());
                        addedMovieNotification.setMessage("ADD");

                        user.getNotifications().add(addedMovieNotification);
                    }
                }
            }
        }
    }


    /**
     * metoda ce sterge un film din baza de date
     * @param currinfo
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    public static void deleteMethod(final CurrInfo currinfo, final Actions action,
                                          final InputData inputdata, final ArrayNode output,
                                          final ObjectMapper objectMapper) {

        if (currinfo.getUser().getNotifications() == null) {
            currinfo.getUser().setNotifications(new ArrayList<Notification>());
        }

        String deteledMovie = action.getDeletedMovie();
        // in primul rand pot sterge filmul doar daca acesta exista)))
        int exists = 0;
        for (Movie movie : inputdata.getMovies()) {
            if (movie.equals(deteledMovie)) {
                exists = 1;
                break;
            }
        }

        if (exists == 0) {
            GenerateOutput.printError(output, objectMapper);
        } else {
            // in primul rand elimin filmul din baza de date a filmelor
            removeMovie(inputdata.getMovies(), deteledMovie);

            // pentru fiecare user, ma uit in care din listele sale se mai regaseste filmul
            for (User user : inputdata.getUsers()) {
                // daca gasesc filmul in lista de filme cumparate, il elimin
                if (removeMovie(user.getPurchasedMovies(), deteledMovie) == 1) {

                    // restitui resursele user-ului
                    if (user.getCredentials().getAccountType().equals("premium")) {
                        user.setNumFreePremiumMovies(user.getNumFreePremiumMovies() + 1);
                    } else if (user.getCredentials().getAccountType().equals("standard")) {
                        user.setTokensCount(user.getTokensCount() + 2);
                    }

                    // notific userii
                    Notification deletedMovieNotification = new Notification();
                    deletedMovieNotification.setMovieName(deteledMovie);
                    deletedMovieNotification.setMessage("DELETE");

                    user.getNotifications().add(deletedMovieNotification);
                }

                removeMovie(user.getWatchedMovies(), deteledMovie);
                removeMovie(user.getLikedMovies(), deteledMovie);
                removeMovie(user.getRatedMovies(), deteledMovie);
            }
        }
    }
}
