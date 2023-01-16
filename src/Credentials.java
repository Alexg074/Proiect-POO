//import com.fasterxml.jackson.databind;
class Credentials {
    private String name;
    private String password;
    private String accountType;
    private String country;
    private String balance;

    // default constructor
    Credentials() { }

    Credentials(final String name, final String password, final String accountType,
                       final String country, final String balance) {
        this.name = name;
        this.password = password;
        this.accountType = accountType;
        this.country = country;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(final String accountType) {
        this.accountType = accountType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(final String balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Credentials{"
                + "name='"
                + name
                + '\''
                + ", password='"
                + password
                + '\''
                + ", accountType='"
                + accountType
                + '\''
                + ", country='"
                + country
                + '\''
                + ", balance="
                + balance
                + '}';
    }
}


