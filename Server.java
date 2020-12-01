package company;

import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class Server {

    private static final String FILE_NAME_CLIENTS = "clients.txt";
    private static final String FILE_NAME_IP = "ipBan.txt";

    private ArrayList<ClientHandler> clientJoined;
    public static ArrayList<User> accounts;
    public static ArrayList<String> ips;
    private ServerSocket server;
    public BufferedReader serverInput;
    public PrintWriter serverOutput;
    private final int MAX_CLIENTS = 20;
    private InetAddress IP = null;
    private int uid;

    private void readClientsFromFile(String fileName) {
        File file = new File(fileName);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            String[] parts;
            int index = 0;


            while ((line = br.readLine()) != null) {
                if (line.equals(""))
                    continue;
                parts = line.split(";");
                if (parts.length != 2)
                    continue;
                accounts.add(index++, new User(parts[0], parts[1]));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readIpFromFile(String fileName) {
        File file = new File(fileName);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = ("");

            while ((line = br.readLine()) != null) {
                if (!line.equals(" ") && line.contains(".")) {
                    ips.add(line);
                } else {
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeIpToFile(String fileName, InetAddress ip) throws IOException {
        File file = new File(fileName);
        try {
            boolean appendToFile = true;
            PrintWriter pw = null;
            String line = "";
            if (appendToFile) {
                pw = new PrintWriter(new FileWriter(file, true));
            }

            if (!line.equals(null)) {
                pw.println(ip.toString());
            }
            String IP = ip.toString();

            ips.add(IP);
            pw.flush();
            pw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server(int port) {

        try {
            clientJoined = new ArrayList<>();
            accounts = new ArrayList<>();
            ips = new ArrayList<>();

            readClientsFromFile(FILE_NAME_CLIENTS);
            readIpFromFile(FILE_NAME_IP);

            server = new ServerSocket(port);
            System.out.println(">> Server started <<\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptClient() {
        try {
            while (true) {
                Socket client = server.accept();
                for (int i = 0; i < ips.size(); i++) {
                    if (client.getInetAddress().toString().equals(ips.get(i).toString())) {
                        System.out.println("<< Rejected IP Address" + client.getInetAddress());
                        client.close();
                    }

                }

                if (uid <= MAX_CLIENTS) {
                    uid++;
                    IP = client.getInetAddress();
                    System.out.println(">> Client connected << " + "[" + uid + "] " + client.toString());
                    serverOutput = new PrintWriter(client.getOutputStream(), true);
                    System.out.println(">> InputStream << " + "[" + client.getInputStream().toString() + "]");
                    serverInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    System.out.println(">> OutputStream << " + "[" + client.getOutputStream().toString() + "]\n");
                    ClientHandler clientHandler = new ClientHandler(uid, client, IP, this, serverInput, this.serverOutput);
                    clientHandler.start();
                    Thread.sleep(1);
                    clientJoined.add(clientHandler);
                } else {
                    System.out.println("<< Rejected" + client.getInetAddress());
                    client.close();
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ClientHandler> collectClient() {

        return clientJoined;
    }


    public void sendMsgAll(String msg, ClientHandler sender) {
        synchronized (collectClient()) {
            msg = "<< [" + sender.getUID() + "] " + msg;
            for (int i = 0; i < collectClient().size(); i++)
                if (collectClient().get(i).getUID() != sender.getUID())
                    collectClient().get(i).sendMessage(msg);
        }
    }

    public void sendPrivate(String id, String msg, ClientHandler sender) {
        synchronized (collectClient()) {
            int ID = Integer.parseInt(id);
            msg = "<< [PM] [" + sender.getUID() + "] " + msg;
            for (int i = 0; i < collectClient().size(); i++)
                if (collectClient().get(i).getUID() == ID) {
                    collectClient().get(i).sendMessage(msg);
                    break;
                }
        }
    }

    public void kickClient(String id) throws IOException {
        synchronized (collectClient()) {
            int ID = Integer.parseInt(id);
            for (int i = 0; i < clientJoined.size(); i++)
                if (collectClient().get(i).getUID() == ID) {
                    collectClient().get(i).getClientSocket().close();
                }


        }
    }

    public void findIP(String id, ClientHandler sender) throws IOException {
        synchronized (collectClient()) {
            InetAddress IP = null;
            String msg;
            int ID = Integer.parseInt(id);
            for (int i = 0; i < clientJoined.size(); i++)
                if (collectClient().get(i).getUID() == ID) {
                    IP = collectClient().get(i).getIP();
                    msg = "\n\rClient: " + "[" + ID + "]\n\r" + "<< IP: " + "[" + IP + "]\n\r";
                    sender.sendMessage(msg);
                }


        }
    }

    // /calc 5 + 2
    public void calulator(String sum, ClientHandler sender) {
        try {
            double result = Calculator.calculate(sum);
            sender.sendMessage("result = " + result + "\r");
        } catch (InvalidParameterException e) {
            sender.sendMessage(e.getMessage() + "\r");
        }
    }

    public void binary(int x, ClientHandler sender) {
        try {
            String result = Calculator.getBinary(x);
            sender.sendMessage(result + "\r");
        } catch (InvalidParameterException e) {
            sender.sendMessage(e.getMessage() + "\r");
        }
    }

    public void findAccounts() {
        synchronized (collectClient()) {
            for (int i = 0; i < accounts.size(); i++)
                serverOutput.println("\n\rLogin " + "[" + i + "]\n\r" + "Username: " + accounts.get(i).getUsername() + "\n\r" + "Password: " + accounts.get(i).getPassword() + "\n\r");
        }
    }

    public void findBannedIPS(ClientHandler sender) {
        synchronized (collectClient()) {
            String data = "";
            sender.sendMessage("\n\r" + "<< Banned IP'S ");
            for (int i = 0; i < ips.size(); i++) {
                data = "\n\r[" + i + "] " + ips.get(i) + "\n\r";
                sender.sendMessage(data);
            }
        }
    }

    public void banIP(String id, ClientHandler sender) throws IOException {
        synchronized (collectClient()) {
            int ID = Integer.parseInt(id);
            InetAddress ip;
            String IP;
            for (int i = 0; i < clientJoined.size(); i++) {
                if (collectClient().get(i).getUID() == ID) {
                    ip = collectClient().get(i).getClientSocket().getInetAddress();
                    IP = ip.toString();
                    ips.add(IP);
                    writeIpToFile(FILE_NAME_IP, ip);
                    collectClient().get(i).getClientSocket().close();

                }
            }
        }
    }

    public void motd(ClientHandler sender) {
        synchronized (collectClient()) {
            for (int i = 0; i < clientJoined.size(); i++) {
                if (collectClient().get(i).getUID() == sender.getUID())
                    sender.sendMessage("\n\r<< Welcome to the server!" +
                            "\n\rToday is a tuesday\n\r");
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5000);
        server.acceptClient();
    }
}