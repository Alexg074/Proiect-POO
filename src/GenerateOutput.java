import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public final class GenerateOutput {

    static void printError(final ArrayNode output,
                           final ObjectMapper objectMapper) {
        // creez un nod nou in output pt a adauga ce se afiseaza in cazul de eroare
        ObjectNode errorOutput = objectMapper.createObjectNode();
        output.addPOJO(errorOutput);

        ArrayList<String> emptyMoviesList = new ArrayList<>();

        errorOutput.putPOJO("error", "Error");
        errorOutput.putPOJO("currentMoviesList", emptyMoviesList);
        errorOutput.putPOJO("currentUser", null);
    }

    static void printFullOutput(final CurrInfo currinfo, final ArrayNode output,
                                final ObjectMapper objectMapper) {
        // creez un nod nou in output pt a adauga ce se afiseaza pentru o actiune
        // realizata cu succes
        ObjectNode fullOutput = objectMapper.createObjectNode();
        output.addPOJO(fullOutput);

        ArrayList<Movie> outputMoviesList = new ArrayList<>();
        for (Movie movie : currinfo.getMoviesList()) {
            Movie outputMovie = new Movie(movie);

            // calculez rating-ul pt a afisa corect
            if (outputMovie.getNumRatings() > 0) {
                outputMovie.setRating(outputMovie.getRating() / outputMovie.getNumRatings());
            } else {
                outputMovie.setRating(0);
            }

            outputMoviesList.add(outputMovie);
        }

        // fac deep copy la user-ul curent, pentru a il afisa corect pe parcursul actiunilor
        User outputUser = new User(currinfo.getUser().getCredentials());
        outputUser.setTokensCount(currinfo.getUser().getTokensCount());
        outputUser.setNumFreePremiumMovies(currinfo.getUser().getNumFreePremiumMovies());

        if (outputUser.getCredentials().getAccountType().equals("premium")) {
            outputUser.setNotifications(currinfo.getUser().getNotifications());
        }

        // deep copy pentru fiecare lista de movies a unui user,
        // pentru a afisa corect informatiile finale
        ArrayList<Movie> newPurchasedMoviesList = new ArrayList<>();
        for (Movie movie : currinfo.getUser().getPurchasedMovies()) {
            Movie newMovie = new Movie(movie);

            // calculez rating-ul pentru a afisa corect
            if (newMovie.getNumRatings() > 0) {
                newMovie.setRating(newMovie.getRating() / newMovie.getNumRatings());
            } else {
                newMovie.setRating(0);
            }

            newPurchasedMoviesList.add(newMovie);
        }

        ArrayList<Movie> newWatchedMoviesList = new ArrayList<>();
        for (Movie movie : currinfo.getUser().getWatchedMovies()) {
            Movie newMovie = new Movie(movie);

            if (newMovie.getNumRatings() > 0) {
                newMovie.setRating(newMovie.getRating() / newMovie.getNumRatings());
            } else {
                newMovie.setRating(0);
            }

            newWatchedMoviesList.add(newMovie);
        }

        ArrayList<Movie> newLikedMoviesList = new ArrayList<>();
        for (Movie movie : currinfo.getUser().getLikedMovies()) {
            Movie newMovie = new Movie(movie);

            if (newMovie.getNumRatings() > 0) {
                newMovie.setRating(newMovie.getRating() / newMovie.getNumRatings());
            } else {
                newMovie.setRating(0);
            }

            newLikedMoviesList.add(newMovie);
        }

        ArrayList<Movie> newRatedMoviesList = new ArrayList<>();
        for (Movie movie : currinfo.getUser().getRatedMovies()) {
            Movie newMovie = new Movie(movie);

            if (newMovie.getNumRatings() > 0) {
                newMovie.setRating(newMovie.getRating() / newMovie.getNumRatings());
            } else {
                newMovie.setRating(0);
            }

            newRatedMoviesList.add(newMovie);
        }

        // afisare notifications
        ArrayList<Notification> newNotificationsList = new ArrayList<>();

        if (currinfo.getUser().getNotifications() != null) {
            for (Notification notification : currinfo.getUser().getNotifications()) {
                Notification newNotification = new Notification(notification.getMovieName(),
                                                                notification.getMessage());

                // la finalul actiunilor afisez o recomandare pt user ul premium
            if (currinfo.getUser().getCredentials().getAccountType().equals("premium")) {
                // daca nu a cumparat filmul => nicio recomandare
                if (currinfo.getUser().getPurchasedMovies() == null) {
                    Notification premiumNotification = new Notification();
                    premiumNotification.setMovieName("No recommendation");
                    premiumNotification.setMessage("Recommendation");

                    currinfo.getUser().getNotifications().add(premiumNotification);
                }

                // sortez descrescator filmele in functie de nr de like-uri

                Notification premiumNotification = new Notification();
                premiumNotification.setMessage("Recommendation");
                currinfo.getUser().getNotifications().add(premiumNotification);

            }
                newNotificationsList.add(newNotification);
            }
        }

        // adaug listele de movies in user
        outputUser.setPurchasedMovies(newPurchasedMoviesList);
        outputUser.setWatchedMovies(newWatchedMoviesList);
        outputUser.setLikedMovies(newLikedMoviesList);
        outputUser.setRatedMovies(newRatedMoviesList);
        outputUser.setNotifications(newNotificationsList);

        fullOutput.putPOJO("error", null);
        fullOutput.putPOJO("currentMoviesList", outputMoviesList);
        fullOutput.putPOJO("currentUser", outputUser);
    }
}
