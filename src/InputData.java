import java.util.ArrayList;

public final class InputData {
    private ArrayList<User> users;
    private ArrayList<Movie> movies;
    private ArrayList<Actions> actions;

    InputData() { }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(final ArrayList<User> users) {
        if (users == null) {
            this.users = new ArrayList<User>();
        } else {
            this.users = new ArrayList<User>(users);
        }
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    /**
     * deep copy pentru array-ul de movies
     * @param movies
     */

    public void setMovies(final ArrayList<Movie> movies) {
        if (movies == null) {
            this.movies = new ArrayList<Movie>();
        } else {
            this.movies = new ArrayList<Movie>(movies);
        }
    }

    public ArrayList<Actions> getActions() {
        return actions;
    }

    /**
     * deep copy pentru array-ul de actions
     * @param actions
     */
    public void setActions(final ArrayList<Actions> actions) {
        if (actions == null) {
            this.actions = new ArrayList<Actions>();
        } else {
            this.actions = new ArrayList<Actions>(actions);
        }
    }
}
