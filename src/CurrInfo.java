import java.util.ArrayList;
public final class CurrInfo {
    private String name;
    private ArrayList<Movie> moviesList;
    private User user;
    private Movie movie;

    CurrInfo() { }

    CurrInfo(CurrInfo newCurrInfo) {

        if (this.getUser() == null) {
            this.setUser(new User());
        }

        if (this.getMoviesList() == null) {
            this.setMoviesList(new ArrayList<Movie>());
        }

        if (this.getMovie() == null) {
            this.setMovie(new Movie());
        }

        // actualizez numele paginii
        this.setName(newCurrInfo.getName());

        // actualizez user ul
        this.setUser(newCurrInfo.getUser());

        // actualizez lista curenta de filme
        this.setMoviesList(newCurrInfo.getMoviesList());

        // actualizez si filmul pt see details
        this.setMovie(newCurrInfo.getMovie());
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ArrayList<Movie> getMoviesList() {
        return moviesList;
    }

    /**
     * deep copy pentru array-ul de movies
     * @param moviesList
     */
    public void setMoviesList(final ArrayList<Movie> moviesList) {
        if (moviesList == null) {
            this.moviesList = new ArrayList<Movie>();
        } else {
            this.moviesList = new ArrayList<Movie>(moviesList);
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(final Movie movie) {
        this.movie = movie;
    }
}
