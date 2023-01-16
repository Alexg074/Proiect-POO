import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;
class User {
    private Credentials credentials;
    private int tokensCount;
    private int numFreePremiumMovies;
    private ArrayList<Movie> purchasedMovies;
    private ArrayList<Movie> watchedMovies;
    private ArrayList<Movie> likedMovies;
    private ArrayList<Movie> ratedMovies;
    private ArrayList<Notification> notifications;
    @JsonIgnore
    private ArrayList<String> subscribedGenres;

    User() { }

    User(final String name, final String password, final String accountType,
                final String country, final String balance) {
        // copiez camp cu camp credentials urile in clasa user
        this.credentials = new Credentials(name, password, accountType, country, balance);
    }

    User(Credentials credentials) {
        // copiez camp cu camp credentials urile in clasa user
        this.credentials = new Credentials(credentials.getName(), credentials.getPassword(),
                                            credentials.getAccountType(), credentials.getCountry(),
                                            credentials.getBalance());
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(final Credentials credentials) {
        this.credentials = credentials;
    }

    public int getTokensCount() {
        return tokensCount;
    }

    public void setTokensCount(final int tokensCount) {
        this.tokensCount = tokensCount;
    }

    public int getNumFreePremiumMovies() {
        return numFreePremiumMovies;
    }

    public void setNumFreePremiumMovies(final int numFreePremiumMovies) {
        this.numFreePremiumMovies = numFreePremiumMovies;
    }

    public ArrayList<Movie> getPurchasedMovies() {
        return purchasedMovies;
    }

    public void setPurchasedMovies(final ArrayList<Movie> purchasedMovies) {
        if (purchasedMovies == null) {
            this.purchasedMovies = new ArrayList<Movie>();
        } else {
            this.purchasedMovies = new ArrayList<Movie>(purchasedMovies);
        }
    }

    public ArrayList<Movie> getWatchedMovies() {
        return watchedMovies;
    }

    public void setWatchedMovies(final ArrayList<Movie> watchedMovies) {
        if (watchedMovies == null) {
            this.watchedMovies = new ArrayList<Movie>();
        } else {
            this.watchedMovies = new ArrayList<Movie>(watchedMovies);
        }
    }

    public ArrayList<Movie> getLikedMovies() {
        return likedMovies;
    }

    public void setLikedMovies(final ArrayList<Movie> likedMovies) {
        if (likedMovies == null) {
            this.likedMovies = new ArrayList<Movie>();
        } else {
            this.likedMovies = new ArrayList<Movie>(likedMovies);
        }
    }

    public ArrayList<Movie> getRatedMovies() {
        return ratedMovies;
    }

    public void setRatedMovies(final ArrayList<Movie> ratedMovies) {
        if (ratedMovies == null) {
            this.ratedMovies = new ArrayList<Movie>();
        } else {
            this.ratedMovies = new ArrayList<Movie>(ratedMovies);
        }
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        if (notifications == null) {
            this.notifications = new ArrayList<Notification>();
        } else {
            this.notifications = new ArrayList<Notification>(notifications);
        }
    }
    public ArrayList<String> getSubscribedGenres() {
        return subscribedGenres;
    }

    public void setSubscribedGenres(ArrayList<String> subscribedGenres) {
        if (subscribedGenres == null) {
            this.subscribedGenres = new ArrayList<String>();
        } else {
            this.subscribedGenres = new ArrayList<String>(subscribedGenres);
        }
    }

    @Override
    public String toString() {
        return "User{"
                + "credentials="
                + credentials
                + ", tokensCount="
                + tokensCount
                + ", numFreePremiumMovies="
                + numFreePremiumMovies
                + ", purchasedMovies="
                + purchasedMovies
                + ", watchedMovies="
                + watchedMovies
                + ", likedMovies="
                + likedMovies
                + ", ratedMovies="
                + ratedMovies
                + ", notifications ="
                + notifications
                + '}';
    }
}
