package company;

import java.util.ArrayList;

public class User {

    private String Username;
    private String Password;

    public User(String Username, String Password) {
        this.Username = Username;
        this.Password = Password;
    }

    public String getUsername() {

        return Username;
    }

    public String getPassword() {

        return Password;
    }

    public void setUsername(String Username) {

        this.Username = Username;
    }

    public void setPassword(String Password) {

        this.Password = Password;
    }

    public static ArrayList<User> getAccounts() {

        return Server.accounts;
    }


    public static boolean checkUser(String recievedUser, String recievedPass) {
        for (int i = 0; i < Server.accounts.size(); i += 1) {
            if (getAccounts().get(i).getUsername().equals(recievedUser) && getAccounts().get(i).getPassword().equals(recievedPass)) {
                return true;
            } else {
            }
        }
        return false;
    }
}






