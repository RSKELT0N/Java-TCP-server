package company;

import java.net.*;
import java.io.*;
import java.lang.Thread;

public class ClientHandler extends Thread {

    private int uid;
    private Socket client;
    private Server server;
    private BufferedReader serverInput;
    private PrintWriter serverOutput;
    private InetAddress IP;

    public ClientHandler(int uid, Socket client, InetAddress IP, Server server, BufferedReader serverInput, PrintWriter serverOutput) {
        this.server = server;
        this.client = client;
        this.uid = uid;
        this.serverInput = serverInput;
        this.serverOutput = serverOutput;
        this.IP = IP;
    }

    public int getUID() {
        return uid;
    }

    public Socket getClientSocket() {

        return client;
    }

    public InetAddress getIP() {
        return IP;
    }

    public String sendMessage(String s) {
        if (s.equals("")) {
        } else {
            serverOutput.println(s);
        }
        return "";
    }

    @Override
    public void run() {
        try {
            String recievedUser;
            String recievedPass;
            boolean check;
            serverOutput.println("[" + this.getUID() + "] " + this.getClientSocket() + "\r");
            serverOutput.println("\n<< Login");

            while (true) {
                serverOutput.println("\n\rUsername: " + "\r");
                recievedUser = serverInput.readLine();
                serverOutput.println("\nPassword: " + "\r");
                sleep(1000);
                recievedPass = serverInput.readLine();
                sleep(1000);

                if (User.checkUser(recievedUser, recievedPass) == true) {
                    break;
                } else {
                    serverOutput.println("\n\r<<Incorrect login details!\r");
                }
            }

            serverOutput.println("\n\r[Network]" + "\r");
            serverOutput.println("\rtype \"/help\" for info" + "\n\r");
            while (true) {
                String input = serverInput.readLine();
                analyseInput(input);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void analyseInput(String input) throws IOException {
        String[] parts = input.split(" ");
        String msg = "";
        if (parts[0].equals("/all")) {
            msg = parts[1];
            for (int i = 2; i < parts.length; i++)
                msg += " " + parts[i];
            server.sendMsgAll(msg + "\r", this);
        } else if (parts[0].equals("/msg"))
            server.sendPrivate(parts[1], parts[2] + "\r", this);
        else if (parts[0].equals("/kick"))
            server.kickClient(parts[1]);
        else if (parts[0].equals("/accounts"))
            server.findAccounts();
        else if (parts[0].equals("/ip"))
            server.findIP(parts[1], this);
        else if (parts[0].equals("/calc")) {
            String expression = "";
            for (int i = 1; i < parts.length; i++) {
                expression += parts[i];
                if (i != parts.length - 1)
                    expression += " ";
            }
            server.calulator(expression, this);
        } else if (parts[0].equals("/ips")) {
            server.findBannedIPS(this);
        } else if (parts[0].equals("/motd")) {
            server.motd(this);
        } else if (parts[0].equals("/ipban")) {
            server.banIP(parts[1], this);
        } else if (parts[0].equals("/help"))
            sendMessage("\r\n<< HELP >>\n\r" +
                    "/all - Send message to clients\n\r" +
                    "/msg [UID] - Send a message to a client privately\n\r" +
                    "/kick [UID] - Kicks the client relating to the UID\n\r" +
                    "/ipban  [UID] - bans the client's ip relating to the UID\n\r" +
                    "/ip [UID] - finds the IP that is related to the UID\n\r" +
                    "/ips - finds all IP's that are banned\n\r" +
                    "/accounts - finds the user login's\n\r" +
                    "/calc [1 + 1] - solves a maths equation\n\r" +
                    "/bin [n] - finds the value of 'n' in binary\n\r" +
                    "/motd - Tells the message of the day\n\r");
        else if (parts[0].equals("/bin")) {
            int x;
            try {
                x = Integer.parseInt(parts[1]);
                server.binary(x, this);
            } catch (Exception e) {
                sendMessage("the parameter has to be an integer!!" + "\r");
            }
        } else sendMessage("<< unknown command\n\r");
    }

    @Override
    public String toString() {
        return "CLIENT UID [" + getUID() + "]";
    }
}