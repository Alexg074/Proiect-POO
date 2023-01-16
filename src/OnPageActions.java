import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.security.CodeSigner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * clasa ce contine metode pentru a rezolva fiecare actiune "on page" care poate fi
 * primita ca input
 */
public final class OnPageActions {

    private static OnPageActions instance = null;

    private OnPageActions() { }

    public static OnPageActions getInstance() {
        if (instance == null) {
            instance = new OnPageActions();
        }
        return instance;
    }

    public void onPageMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                             Actions action, final InputData inputdata, final ArrayNode output,
                             final ObjectMapper objectMapper) {
        // retin tipul (feature-ul) actiunii curente
        String feature = action.getFeature();

        switch (feature) {
            case "login":
                loginMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
                break;
            case "register":
                registerMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
                break;
            case "search":
                searchMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
                break;
            case "filter":
                filterMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
                break;
            case "buy premium account":
                buyPremiumAccountMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
                break;
            case "buy tokens":
                buyTokensMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
                break;
            case "purchase":
                purchaseMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
                break;
            case "watch":
                watchMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
                break;
            case "like":
                likeMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
                break;
            case "rate a movie":
                rateMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
                break;
            case "subscribe":
                subscribeMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
            case "default":
                break;
        }
    }

    /**
     * metoda verifica si realizeaza actiunea de login, daca este posibila
     * @param currinfo - retine detaliile despre pagina curenta
     * @param pagesList
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    private static void loginMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                    Actions action, final InputData inputdata, final ArrayNode output,
                                    final ObjectMapper objectMapper) {

        if (currinfo.getName().equals("login")) {
            for (User user : inputdata.getUsers()) {
                Credentials credentials = action.getCredentials();
                // verific daca user ul curent citit (odata cu actiunea) exista in baza de date
                if (credentials.getName().equals(user.getCredentials().getName())) {
                    if (credentials.getPassword().equals(user.getCredentials().getPassword())) {
                        currinfo.setName("Homepage autentificat");
                        // actualizez user ul logat curent
                        currinfo.setUser(user);

                        currinfo.getUser().setTokensCount(0);
                        currinfo.getUser().setNumFreePremiumMovies(15);
                        currinfo.getUser().setPurchasedMovies(null);
                        currinfo.getUser().setWatchedMovies(null);
                        currinfo.getUser().setLikedMovies(null);
                        currinfo.getUser().setRatedMovies(null);

                        // login realizat cu succes
                        pagesList.add(new CurrInfo(currinfo));
                        GenerateOutput.printFullOutput(currinfo, output, objectMapper);
                        break;
                    }
                }
            }

            if (currinfo.getName().equals("login")) {
                // user ul a ramas null (negasit)
                currinfo.setName("Homepage neautentificat");
                GenerateOutput.printError(output, objectMapper);
            }
        } else {
            GenerateOutput.printError(output, objectMapper);
        }
    }

    /**
     * Mmetoda ce realizeaza propriu-zis actiunea de register
     * @param currinfo
     * @param pagesList
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    private static void registerMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                       Actions action, final InputData inputdata, final ArrayNode output,
                                       final ObjectMapper objectMapper) {

        // verific daca numele exista deja => eroare
        // daca nu exista, updatez pagina si user ul curent
        if (currinfo.getName().equals("register")) {
            for (User user : inputdata.getUsers()) {
                if (action.getCredentials().getName().equals(user.getCredentials().getName())) {
                    currinfo.setName("Homepage neautentificat");
                    GenerateOutput.printError(output, objectMapper);
                    break;
                }
            }

            if (currinfo.getName().equals("register")) {
                currinfo.setName("Homepage autentificat");
                // creez user ul daca nu exista
                // actualizez informatiile curente despre pagina, user si restu
                Credentials credentials = action.getCredentials();
                currinfo.setUser(new User(credentials.getName(), credentials.getPassword(),
                                            credentials.getAccountType(), credentials.getCountry(),
                                            credentials.getBalance()));
                currinfo.getUser().setTokensCount(0);
                currinfo.getUser().setNumFreePremiumMovies(15);
                currinfo.getUser().setPurchasedMovies(null);
                currinfo.getUser().setWatchedMovies(null);
                currinfo.getUser().setLikedMovies(null);
                currinfo.getUser().setRatedMovies(null);

                // adaug in baza de date noul user
                inputdata.getUsers().add(currinfo.getUser());

                // register realizat cu succes
                pagesList.add(new CurrInfo(currinfo));
                GenerateOutput.printFullOutput(currinfo, output, objectMapper);
            }
        } else {
            GenerateOutput.printError(output, objectMapper);
        }
    }

    /**
     * metoda ce rezolva actiunea de search
     * @param currinfo
     * @param pagesList
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    private static void searchMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                     Actions action, final InputData inputdata, final ArrayNode output,
                                     final ObjectMapper objectMapper) {

        // caut printre filmele din database si le retin pe cele care incep cu startswith
        if (currinfo.getName().equals("movies")) {
            currinfo.setName("movies");

            // refac lista de filme vizibile pt user ul curent (nebanate)
            ArrayList<Movie> visibleMoviesList = new ArrayList<>();

            int ok = 0;
            // refac lista de filme vizibile
            for (Movie movie : inputdata.getMovies()) {
                for (String countryBanned : movie.getCountriesBanned()) {
                    if (countryBanned.equals(currinfo.getUser().getCredentials().getCountry())) {
                        ok = 0;
                        break;
                        // continue;
                    } else {
                        ok = 1;
                    }
                }
                // daca filmul e nebanat, il adaug
                if (ok == 1)
                    visibleMoviesList.add(movie);
            }

            currinfo.setMoviesList(visibleMoviesList);

            // creez o lista ce va contine filmele ce incep cu startsWith
            ArrayList<Movie> searchedMoviesList = new ArrayList<>();

            for (Movie movie : currinfo.getMoviesList()) {
                if (movie.getName().startsWith(action.getStartsWith())) {
                    searchedMoviesList.add(movie);
                }
            }

            currinfo.setMoviesList(searchedMoviesList);
            // afisez lista curenta de filme
            GenerateOutput.printFullOutput(currinfo, output, objectMapper);

        } else {
            GenerateOutput.printError(output, objectMapper);
        }
    }

    /**
     * metoda prin care un user poate filtra lista de filme
     * @param currinfo
     * @param pagesList
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    private static void filterMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                     Actions action, final InputData inputdata, final ArrayNode output,
                                     final ObjectMapper objectMapper) {

        ArrayList<Movie> visibleMoviesList = new ArrayList<>();

        int ok = 0;
        // refac lista de filme vizibile
        for (Movie movie : inputdata.getMovies()) {
            for (String countryBanned : movie.getCountriesBanned()) {
                if (countryBanned.equals(currinfo.getUser().getCredentials().getCountry())) {
                    ok = 0;
                    break;
                    // continue;
                } else {
                    ok = 1;
                }
            }
            // daca filmul e nebanat, il adaug
            if (ok == 1)
                visibleMoviesList.add(movie);
        }

        currinfo.setMoviesList(visibleMoviesList);

        // impelemetez metode ce suprascriu Comparator, pentru a sorta in functie de rating,
        // respectiv duration in caz de egalitate de rating

        // rating = increasing, duration = increasing
        class SortIncreasingIncreasing implements Comparator<Movie> {
            @Override
            public int compare(final Movie mov1, final Movie mov2) {
                if (mov1.getRating() < mov2.getRating()) {
                    return 1;
                } else if (mov1.getRating() > mov2.getRating()) {
                    return -1;
                } else {
                    // verific duration-urile in caz de egalitate
                    if (mov1.getDuration() < mov2.getDuration()) {
                        return 1;
                    } else if (mov1.getDuration() > mov2.getDuration()) {
                        return -1;
                    } else {
                        // daca filmele au si duration-uri egale
                        return 0;
                    }
                }
            }
        }

        // rating = increasing,  duration = decreasing
        class SortIncreasingDecreasing implements Comparator<Movie> {
            @Override
            public int compare(final Movie mov1, final Movie mov2) {
                if (mov1.getRating() < mov2.getRating()) {
                    return 1;
                } else if (mov1.getRating() > mov2.getRating()) {
                    return -1;
                } else {
                    // verific rating urile in caz de egalitate
                    if (mov1.getDuration() < mov2.getDuration()) {
                        return -1;
                    } else if (mov1.getDuration() > mov2.getDuration()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        }

        // rating = decreasing, duration = increasing
        class SortDecreasingIncreasing implements Comparator<Movie> {
            @Override
            public int compare(final Movie mov1, final Movie mov2) {
                if (mov1.getRating() < mov2.getRating()) {
                    return -1;
                } else if (mov1.getRating() > mov2.getRating()) {
                    return 1;
                } else {
                    // verific rating urile in caz de egalitate
                    if (mov1.getDuration() < mov2.getDuration()) {
                        return 1;
                    } else if (mov1.getDuration() > mov2.getDuration()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        }

        // rating = decreasing, duration = decreasing
        class SortDecreasingDecreasing implements Comparator<Movie> {
            @Override
            public int compare(final Movie mov1, final Movie mov2) {
                if (mov1.getRating() < mov2.getRating()) {
                    return -1;
                } else if (mov1.getRating() > mov2.getRating()) {
                    return 1;
                } else {
                    // verific rating urile in caz de egalitate
                    if (mov1.getDuration() < mov2.getDuration()) {
                        return -1;
                    } else if (mov1.getDuration() > mov2.getDuration()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        }

        // rating = null, duration = increasing
        class SortNullIncreasing implements Comparator<Movie> {
            @Override
            public int compare(final Movie mov1, final Movie mov2) {
                if (mov1.getDuration() < mov2.getDuration()) {
                    return 1;
                } else if (mov1.getDuration() > mov2.getDuration()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }

        // rating = null, duration = decreasing
        class SortNullDecreasing implements Comparator<Movie> {
            @Override
            public int compare(final Movie mov1, final Movie mov2) {
                if (mov1.getDuration() < mov2.getDuration()) {
                    return -1;
                } else if (mov1.getDuration() > mov2.getDuration()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }

        // rating = increasing, duration = null
        class SortIncreasingNull implements Comparator<Movie> {
            @Override
            public int compare(final Movie mov1, final Movie mov2) {
                if (mov1.getRating() < mov2.getRating()) {
                    return 1;
                } else if (mov1.getRating() > mov2.getRating()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }

        // rating = decreasing, duration = null
        class SortDecreasingNull implements Comparator<Movie> {
            @Override
            public int compare(final Movie mov1, final Movie mov2) {
                if (mov1.getRating() < mov2.getRating()) {
                    return -1;
                } else if (mov1.getRating() > mov2.getRating()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }

        // metoda propriu-zisa de filter
        if (currinfo.getName().equals("movies")) {
            currinfo.setName("movies");

            // reconstruiesc lista initiala cu filmele banate

            // filtrez dupa actori filmele din currMoviesList

            for (Movie movie : currinfo.getMoviesList()) {
                int foundActor = 0;
                if (action.getFilters().getContains() != null
                    && action.getFilters().getContains().getActors() != null) {
                    for (String actor : movie.getActors()) {
                        // caut actorul dupa care vreau sa filtrez, daca nu exista
                        if (actor.equals(action.getFilters().getContains().getActors().get(0)))
                            foundActor = 1;
                    }

                    // elimin din lista curenta de filme filmele care nu contin actorul respectiv
                    // la final voi avea lista filtrata dupa actori
                    if (foundActor == 0)
                        currinfo.getMoviesList().remove(movie);
                }
            }

            // filtrez si dupa genuri
            for (Movie movie : currinfo.getMoviesList()) {
                int foundGenre= 0;
                if (action.getFilters().getContains() != null
                    && action.getFilters().getContains().getGenre() != null) {
                    for (String genre : movie.getGenres()) {
                        // caut genul dupa care vreau sa filtrez, daca nu exista
                        if (genre.equals(action.getFilters().getContains().getGenre().get(0)))
                            foundGenre = 1;
                    }
                    // elimin din lista curenta de filme filmele care nu contin actorul respectiv
                    // la final voi avea lista filtrata dupa actori
                    if (foundGenre == 0) {


                        currinfo.getMoviesList().remove(movie);
                    }

                }
            }

            Sort sort = action.getFilters().getSort();

            if (sort.getRating() == null) {
                if (sort.getDuration().equals("increasing")) {
                    Collections.sort(currinfo.getMoviesList(), new SortNullIncreasing());
                } else if (sort.getDuration().equals("decreasing")) {
                    Collections.sort(currinfo.getMoviesList(), new SortNullDecreasing());
                } else if (sort.getDuration() == null) {
//                    GenerateOutput.printFullOutput(currinfo, output, objectMapper);
                }

            } else if (sort.getRating().equals("increasing")) {
                if (sort.getDuration().equals("increasing")) {
                    Collections.sort(currinfo.getMoviesList(), new SortIncreasingIncreasing());
                } else if (sort.getDuration().equals("decreasing")) {
                    Collections.sort(currinfo.getMoviesList(), new SortIncreasingDecreasing());
                }

            } else if (sort.getRating().equals("decreasing")) {
                if (sort.getDuration().equals("increasing")) {
                    Collections.sort(currinfo.getMoviesList(), new SortDecreasingIncreasing());
                } else if (sort.getDuration().equals("decreasing")) {
                    Collections.sort(currinfo.getMoviesList(), new SortDecreasingDecreasing());
                }

            } else if (sort.getDuration() == null) {
                if (sort.getRating().equals("increasing")) {
                    Collections.sort(currinfo.getMoviesList(), new SortIncreasingNull());
                } else if (sort.getRating().equals("decreasing")) {
                    Collections.sort(currinfo.getMoviesList(), new SortDecreasingNull());
                }
            }
            // daca filter ul s a realizat cu succes, afisez output ul de succes
            GenerateOutput.printFullOutput(currinfo, output, objectMapper);
        }  else {
            GenerateOutput.printError(output, objectMapper);
        }
    }

    /**
     * metoda prin care un user poate cumpara tokens
     * @param currinfo
     * @param pagesList
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    private static void buyTokensMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                        Actions action, final InputData inputdata, final ArrayNode output,
                                        final ObjectMapper objectMapper) {

        if (currinfo.getName().equals("upgrades")) {
            if (Integer.parseInt(currinfo.getUser().getCredentials().getBalance()) >= action.getCount()) {
                // adaug numarul de tokeni cumparati in contul user ului
                // updatez acest numar si in user ul curent
                int newTokensCount = currinfo.getUser().getTokensCount() + action.getCount();
                currinfo.getUser().setTokensCount(newTokensCount);
                currinfo.getUser().setTokensCount(newTokensCount);

                // scad din balance numarul de bani cheltuiti pe tokens
                int newBalance = Integer.parseInt(currinfo.getUser().getCredentials().getBalance()) - action.getCount();
                currinfo.getUser().getCredentials().setBalance(Integer.toString(newBalance));
                currinfo.getUser().getCredentials().setBalance(Integer.toString(newBalance));

            } else {
                GenerateOutput.printError(output, objectMapper);
            }

        } else {
            GenerateOutput.printError(output, objectMapper);
        }
    }

    /**
     * metoda prin care un user poate deveni premium
     * @param currinfo
     * @param pagesList
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    private static void buyPremiumAccountMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                                Actions action, final InputData inputdata, final ArrayNode output,
                                                final ObjectMapper objectMapper) {

       if (currinfo.getName().equals("upgrades")) {
           if (currinfo.getUser().getTokensCount() >= 10) {
               // contul user-ului devine premium, iar numarul sau de tokens scade cu 10
               currinfo.getUser().getCredentials().setAccountType("premium");

               int newTokensCount = currinfo.getUser().getTokensCount() - 10;
               currinfo.getUser().setTokensCount(newTokensCount);

               // actualize informatiile despre cont si tokens si in baza de date
               // pentru acelasi user
               for (User user : inputdata.getUsers()) {
                   if (user.getCredentials().getName().equals(currinfo.getUser().getCredentials().getName())) {

                       user.getCredentials().setAccountType("premium");
                       user.setTokensCount(newTokensCount);
                   }
               }
           }
       } else {
           GenerateOutput.printError(output, objectMapper);
       }
   }

    /**
     * metoda prin care un user poate cumpara un film
     * @param currinfo
     * @param pagesList
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
   private static void purchaseMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                      Actions action, final InputData inputdata, final ArrayNode output,
                                      final ObjectMapper objectMapper) {

        int alreadyPurchased = 0;
        if (currinfo.getName().equals("see details")) {
            if (currinfo.getUser().getPurchasedMovies() != null) {
                for (Movie purchasedMovie : currinfo.getUser().getPurchasedMovies()) {
                    // daca filmul a mai fost cumparat => eroare
                    if (purchasedMovie.getName().equals(action.getMovie())) {
                        alreadyPurchased = 1;
                        GenerateOutput.printError(output, objectMapper);
                        break;
                    }
                }
            }

            if (alreadyPurchased == 0) {
                // un user premium poate cumpara 15 filme gratuite
                // adaug filmul cumparat in lista de purchasedMovies a user-ului curent
                // filmul deja e salvat in currinfo.movie de cand am intrat pe pag See details
                if (currinfo.getUser().getCredentials().getAccountType().equals("premium")
                    && currinfo.getUser().getNumFreePremiumMovies() > 0) {

                    currinfo.getUser().getPurchasedMovies().add(currinfo.getMovie());

                    int newNumFreePremiumMovies = currinfo.getUser().getNumFreePremiumMovies() - 1;
                    currinfo.getUser().setNumFreePremiumMovies(newNumFreePremiumMovies);

                    // afisez filmul curent
                    ArrayList<Movie> currMovie = new ArrayList<Movie>();
                    currMovie.add(currinfo.getMovie());

                    currinfo.setMovie(currinfo.getMovie());
                    currinfo.setMoviesList(currMovie);

                    GenerateOutput.printFullOutput(currinfo, output, objectMapper);

                } else {
                    // un user normal plateste 2 tokens / movie
                    if (currinfo.getUser().getTokensCount() >= 2) {

                        currinfo.getUser().getPurchasedMovies().add(currinfo.getMovie());

                        int newTokensCount = currinfo.getUser().getTokensCount() - 2;
                        currinfo.getUser().setTokensCount(newTokensCount);

                        // afisez filmul curent
                        ArrayList<Movie> currMovie = new ArrayList<Movie>();
                        currMovie.add(currinfo.getMovie());

                        currinfo.setMovie(currinfo.getMovie());
                        currinfo.setMoviesList(currMovie);

                        GenerateOutput.printFullOutput(currinfo, output, objectMapper);

                    } else {
                        GenerateOutput.printError(output, objectMapper);
                    }
                }
            }
        } else {
            GenerateOutput.printError(output, objectMapper);
        }
   }

    /**
     * metoda prin care un user vede un film
     * @param currinfo
     * @param pagesList
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    private static void watchMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                    Actions action, final InputData inputdata, final ArrayNode output,
                                    final ObjectMapper objectMapper) {

        int purchased = 0;
        int watched = 0;
        if (currinfo.getName().equals("see details")) {
            if (currinfo.getUser().getPurchasedMovies() != null) {
                outerloop:
                for (Movie purchasedMovie : currinfo.getUser().getPurchasedMovies()) {
                    // verfic daca filmul a fost cumparat, in primul rand
                    if (purchasedMovie.getName().equals(currinfo.getMovie().getName())) {
                        purchased = 1;
                        for (Movie watchedMovie : currinfo.getUser().getWatchedMovies()) {
                            // daca vreau sa vad iar un film, printez output normal si nu modific nimic
                            if (watchedMovie.getName().equals(action.getMovie())) {
                                watched = 1;

                                // afisez filmul curent
                                ArrayList<Movie> currMovie = new ArrayList<Movie>();
                                currMovie.add(currinfo.getMovie());

                                currinfo.setMovie(currinfo.getMovie());
                                currinfo.setMoviesList(currMovie);

                                GenerateOutput.printFullOutput(currinfo, output, objectMapper);
                                break outerloop;
                            }
                        }
                        // daca filmul nu a mai fost vazut, il adaug in lista de watched movies
                        currinfo.getUser().getWatchedMovies().add(purchasedMovie);
                        watched = 1;

                        // afisez filmul curent
                        ArrayList<Movie> currMovie = new ArrayList<Movie>();
                        currMovie.add(currinfo.getMovie());

                        currinfo.setMovie(currinfo.getMovie());
                        currinfo.setMoviesList(currMovie);

                        GenerateOutput.printFullOutput(currinfo, output, objectMapper);
                    }
                }
            }
            // daca filmul nu s-a regasit prin lista de filme cumparate de user => eroare
            if (purchased == 0 || watched == 0)
                GenerateOutput.printError(output, objectMapper);

        } else {
            GenerateOutput.printError(output, objectMapper);
        }
    }

    /**
     * metoda prin care un user poate realiza actiunea de like pentru un film
     * @param currinfo
     * @param pagesList
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    private static void likeMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                   Actions action, final InputData inputdata, final ArrayNode output,
                                   final ObjectMapper objectMapper) {

        int purchased = 0;
        int watched = 0;
        int liked = 0;
        if (currinfo.getName().equals("see details")) {
            if (currinfo.getUser().getPurchasedMovies() != null) {
                outerloop:
                for (Movie purchasedMovie : currinfo.getUser().getPurchasedMovies()) {
                    // daca a fost cumparat
                    if (purchasedMovie.getName().equals(currinfo.getMoviesList().get(0).getName())) {
                        purchased = 1;
                        for (Movie watchedMovie : currinfo.getUser().getWatchedMovies()) {
                            if (watchedMovie.getName().equals(currinfo.getMoviesList().get(0).getName())) {
                                watched = 1;
                                // verific daca a mai dat like o data
                                for (Movie likedMovie : currinfo.getUser().getLikedMovies()) {
                                    if (likedMovie.getName().equals(currinfo.getMoviesList().get(0).getName())) {
                                        liked = 1;
                                        GenerateOutput.printError(output, objectMapper);
                                        break outerloop;
                                    }
                                }
                                // daca filmul a fost si cumparat si vazut => pot da like si incrementa nr de like uri
                                currinfo.getUser().getLikedMovies().add(watchedMovie);
                                liked = 1;
                                watchedMovie.setNumLikes(watchedMovie.getNumLikes() + 1);
                            }
                        }
                    }
                }
            }
            if (purchased == 0 || watched == 0)
                GenerateOutput.printError(output, objectMapper);

             if (liked == 1) {
                 // afisez filmul curent
                 ArrayList<Movie> currMovie = new ArrayList<Movie>();
                 currMovie.add(currinfo.getMovie());

                 currinfo.setMovie(currinfo.getMovie());
                 currinfo.setMoviesList(currMovie);

                 GenerateOutput.printFullOutput(currinfo, output, objectMapper);

             }
        } else {
            GenerateOutput.printError(output, objectMapper);
        }
    }

    /**
     * metoda prin care un user poate oferi un ratin unui film
     * @param currinfo
     * @param pagesList
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    private static void rateMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                   Actions action, final InputData inputdata, final ArrayNode output,
                                   final ObjectMapper objectMapper) {

        int purchased = 0;
        int watched = 0;
        int rated = 0;
        if (currinfo.getName().equals("see details")) {
            if (currinfo.getUser().getPurchasedMovies() != null) {
                outerloop:
                for (Movie purchasedMovie : currinfo.getUser().getPurchasedMovies()) {
                    // daca a fost cumparat
                    if (purchasedMovie.getName().equals(action.getMovie())) {
                        purchased = 1;
                        // daca a fost vazut
                        for (Movie watchedMovie : currinfo.getUser().getWatchedMovies()) {
                            if (watchedMovie.getName().equals(currinfo.getMoviesList().get(0).getName())) {
                                watched = 1;
                                // daca a mai dat rate o data, suprascriu rating ul vechi
                                for (Movie ratedMovie : currinfo.getUser().getRatedMovies()) {
                                    if (ratedMovie.getName().equals(currinfo.getMoviesList().get(0).getName())) {
                                        ratedMovie.setRating(ratedMovie.getRating() + action.getRate());

                                        // System.out.println(ratedMovie.getRating());
                                        break outerloop;
                                    }
                                }
                                // daca filmul a fost si cumparat si vazut => pot da like si incrementa nr de like uri
                                currinfo.getUser().getRatedMovies().add(watchedMovie);
                                rated = 1;
                                watchedMovie.setRating(watchedMovie.getRating() + action.getRate());
                                watchedMovie.setNumRatings(watchedMovie.getNumRatings() + 1);
                            }
                        }
                    }
                }
            }

            if (purchased == 0 || watched == 0)
                GenerateOutput.printError(output, objectMapper);

            if (rated == 1) {
                // afisez filmul curent
                ArrayList<Movie> currMovie = new ArrayList<Movie>();
                currMovie.add(currinfo.getMovie());

                currinfo.setMovie(currinfo.getMovie());
                currinfo.setMoviesList(currMovie);

                GenerateOutput.printFullOutput(currinfo, output, objectMapper);
            }
        } else {
            GenerateOutput.printError(output, objectMapper);
        }
    }

    /**
     * metoda ce rezolva actiunea de subscribe
     * @param currinfo
     * @param pagesList
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    private static void subscribeMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                        Actions action, final InputData inputdata, final ArrayNode output,
                                        final ObjectMapper objectMapper) {

        if (currinfo.getUser().getSubscribedGenres() == null)
            currinfo.getUser().setSubscribedGenres(new ArrayList<>());

        if (currinfo.getName().equals("see details")) {
            int subscribed = 0;
            if (currinfo.getUser().getSubscribedGenres() != null) {
                for (String subscribedGenre : currinfo.getUser().getSubscribedGenres()) {
                    // daca user ul e deja abonat la genul primit ca input in actiunea curenta => eroare
                    if (subscribedGenre.equals(action.getSubscribedGenre())) {
                        subscribed = 1;
                        GenerateOutput.printError(output, objectMapper);
                        break;
                    }
                }
            }
            // iterez printre genurile filmului pe care ma aflu si caut sa dau subscribe la genul din input
            if (subscribed == 0) {
                for (String genre : currinfo.getMovie().getGenres()) {
                    if (genre.equals(action.getSubscribedGenre())) {
                        subscribed = 1;
                        // creez lista de genuri subscribed
                        currinfo.getUser().getSubscribedGenres().add(genre);
                    }
                }
            }
            // daca vrea sa dea subscribe unui alt gen decat cele existente ale filmului
            if (subscribed == 0)
                GenerateOutput.printError(output, objectMapper);

        } else {
            GenerateOutput.printError(output, objectMapper);
        }
    }

}



