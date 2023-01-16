import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

/**
 * Clasa contine metodele pentru pagini (login, register, movies, see details, upgrades),
 * implementate pe rand - in README
 */
public final class ChangePageActions {

    private static ChangePageActions instance = null;

    private ChangePageActions() { }

    public static ChangePageActions getInstance() {
        if (instance == null) {
            instance = new ChangePageActions();
        }
        return instance;
    }

    public void changePageMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                 Actions action, final InputData inputdata, final ArrayNode output,
                                 final ObjectMapper objectMapper) {

        // retin pagina actiunii curente de changepage
        String page = action.getPage();
        switch (page) {
            case "login":
                loginMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
                break;
            case "register":
                registerMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
                break;
            case "movies":
                moviesMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
                break;
            case "see details":
                seeDetailsMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
                break;
            case "upgrades":
                upgradesMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
                break;
            case "logout":
                logoutMethod(currinfo, pagesList, action, inputdata, output, objectMapper);
                break;
            case "default":
                break;
        }
    }

    /**
     *
     * @param currinfo - clasa care retine detaliile curente despre situatia curenta
     * @param pagesList - lista de pagini vizitate
     * @param action - actiunea curenta primita de la input
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    private static void loginMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                    Actions action, final InputData inputdata, final ArrayNode output,
                                    final ObjectMapper objectMapper) {
        /**
         * daca ma aflu pe pag corecta ma pot muta pe cea de login
         * schimb apoi numele paginii in "login"
         */
        if (currinfo.getName().equals("Homepage neautentificat")) {
            currinfo.setName(action.getPage());
            currinfo.setMoviesList(null);
            currinfo.setUser(null);
            currinfo.setMovie(null);

        } else {
            GenerateOutput.printError(output, objectMapper);
        }
    }

    /**
     * metoda ce trateaza pagina de register
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

        if (currinfo.getName().equals("Homepage neautentificat")) {
            currinfo.setName(action.getPage());
            currinfo.setMoviesList(null);
            currinfo.setUser(null);
            currinfo.setMovie(null);

        } else {
            GenerateOutput.printError(output, objectMapper);
        }
    }

    /**
     * metoda ce trateaza pagina de logout
     * @param currinfo
     * @param pagesList
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    private static void logoutMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                     Actions action, final InputData inputdata, final ArrayNode output,
                                     final ObjectMapper objectMapper) {

        String currPage = currinfo.getName();
        if (currPage.equals("Homepage autentificat") || currPage.equals("movies")
                || currPage.equals("upgrades") || currPage.equals("see details")) {
            // deloghez user ul
            // user-ul curent devine null, iar pagina neautentificata
            currinfo.setName("Homepage neautentificat");
            currinfo.setUser(null);
            currinfo.setMoviesList(null);
            currinfo.setMovie(null);

            // actiunea de logout aduce dupa sine golirea listei de pagini vizitate
            pagesList.removeAll(pagesList);
        } else {
            GenerateOutput.printError(output, objectMapper);
        }
    }

    /**
     * metoda ce trateaza pagina de movies
     * @param currinfo
     * @param pagesList
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    private static void moviesMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                     Actions action, final InputData inputdata, final ArrayNode output,
                                     final ObjectMapper objectMapper) {

        String currPage = currinfo.getName();
        if (currPage.equals("Homepage autentificat") || currPage.equals("upgrades")
            || currPage.equals("see details")) {
            currinfo.setName(action.getPage());

            ArrayList<Movie> visibleMoviesList = new ArrayList<>();

            int ok = 0;
            // refac lista de filme vizibile
            for (Movie movie : inputdata.getMovies()) {
                for (String countryBanned : movie.getCountriesBanned()) {
                    if (countryBanned.equals(currinfo.getUser().getCredentials().getCountry())) {
                        ok = 0;
                        break;
                    } else {
                        ok = 1;
                    }
                }
                // daca filmul e nebanat, il adaug
                if (ok == 1)
                    visibleMoviesList.add(movie);
            }

            // setez lista de filme vizibile pt user ul curent
            currinfo.setMoviesList(visibleMoviesList);

            // adaug in lista de pagini pagina pe care m-am mutat
            pagesList.add(new CurrInfo(currinfo));

            GenerateOutput.printFullOutput(currinfo, output, objectMapper);
        } else {
            GenerateOutput.printError(output, objectMapper);
        }
    }

    /**
     * metoda ce trateaza pagina de see details
     * @param currinfo
     * @param pagesList
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    private static void seeDetailsMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                         Actions action, final InputData inputdata, final ArrayNode output,
                                         final ObjectMapper objectMapper) {

        String currPage = currinfo.getName();
        if (currPage.equals("movies") || currPage.equals("see details")) {
            int foundMovie = 0;
            // caut filmul caruia vreau sa ii vad detaliile in baza de date
            for (Movie movie : currinfo.getMoviesList()) {
                if (movie.getName().equals(action.getMovie())) {
                    foundMovie = 1;

                    ArrayList<Movie> finalMovies = new ArrayList<Movie>();
                    finalMovies.add(movie);

                    currinfo.setMovie(movie);
                    currinfo.setMoviesList(finalMovies);

                    GenerateOutput.printFullOutput(currinfo, output, objectMapper);

                    // doar daca am gasit filmul, ma pot muta pe pagina de 'see details'
                    currinfo.setName(action.getPage());

                    // adaug pagina in lista de pagini pe care m-am mutat, facand un deep copy
                    pagesList.add(new CurrInfo(currinfo));
                }
            }

            // nu am reusit sa gasesc filmul => eroare si ma intorc sa l caut pe pagina de movies
            if (foundMovie == 0) {
                GenerateOutput.printError(output, objectMapper);
                currinfo.setName("movies");
                return;
            }

        } else {
            GenerateOutput.printError(output, objectMapper);
        }
    }

    /**
     * metoda ce trateaza pagina de upgrades
     * @param currinfo
     * @param pagesList
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    private static void upgradesMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                       Actions action, final InputData inputdata, final ArrayNode output,
                                       final ObjectMapper objectMapper) {

        String currPage = currinfo.getName();
        if (currPage.equals("Homepage autentificat") || currPage.equals("movies") || currPage.equals("see details")) {
            currinfo.setName(action.getPage());

            // adaug in lista de pagini pagina pe care m-am mutat
            pagesList.add(new CurrInfo(currinfo));

        } else {
            GenerateOutput.printError(output, objectMapper);
        }
    }
}

