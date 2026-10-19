package quickchat;

import java.io.PrintStream;
import java.util.Scanner;

public class QuickChat {

    private final Scanner in;
    private final PrintStream out;
    private int messageCounter = 0;

    public QuickChat(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    public static void main(String[] args) {
        new QuickChat(new Scanner(System.in), System.out).run();
    }

    public void run() {
        Login login = new Login();
        if (!register(login) || !logIn(login)) return;
        out.println("Welcome to QuickChat.");

        boolean running = true;
        while (running) {
            out.println();
            out.println("1) Send Messages");
            out.println("2) Show recently sent messages");
            out.println("3) Quit");
            out.print("Choose an option: ");
            switch (readLine().trim()) {
                case "1": sendMessages(); break;
                case "2": out.println("Coming Soon."); break;
                case "3": out.println("Goodbye."); running = false; break;
                default: out.println("Invalid option, please enter 1, 2 or 3.");
            }
        }
    }

    private boolean register(Login login) {
        out.println("--- Register ---");
        out.print("First name: ");
        String first = readLine();
        out.print("Last name: ");
        String last = readLine();
        out.print("Username: ");
        String user = readLine();
        out.print("Password: ");
        String pass = readLine();
        out.print("Cell number (+27...): ");
        String cell = readLine();
        String result = login.registerUser(first, last, user, pass, cell);
        out.println(result);
        return result.startsWith("Username successfully captured");
    }

    private boolean logIn(Login login) {
        out.println("--- Login ---");
        out.print("Username: ");
        String user = readLine();
        out.print("Password: ");
        String pass = readLine();
        boolean ok = login.loginUser(user, pass);
        out.println(login.returnLoginStatus(ok));
        return ok;
    }

    private void sendMessages() {
        int limit = readPositiveInt("How many messages do you wish to enter? ");
        for (int i = 1; i <= limit; i++) {
            out.println("--- Message " + i + " of " + limit + " ---");

            String recipient;
            while (true) {
                out.print("Recipient cell number (with international code): ");
                recipient = readLine().trim();
                String check = new Message(messageCounter, recipient, "x").checkRecipientCell();
                out.println(check);
                if (check.startsWith("Cell phone number successfully")) break;
            }

            Message msg;
            while (true) {
                out.print("Message (max 250 characters): ");
                msg = new Message(messageCounter, recipient, readLine());
                String check = msg.checkMessageLength();
                out.println(check);
                if (check.equals("Message ready to send.")) break;
            }

            out.println("Message ID generated: " + msg.getMessageID());
            int choice = readChoice();
            out.println(msg.SentMessage(choice));
            messageCounter++;
            if (choice == Message.SEND) out.println(msg.details());
        }
        out.println("Total number of messages sent: " + Message.returnTotalMessages());
    }

    private int readChoice() {
        while (true) {
            out.println("1) Send Message");
            out.println("2) Disregard Message");
            out.println("3) Store Message to send later");
            out.print("Choose an option: ");
            String s = readLine().trim();
            if (s.equals("1") || s.equals("2") || s.equals("3")) return Integer.parseInt(s);
            out.println("Invalid option.");
        }
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            out.print(prompt);
            try {
                int n = Integer.parseInt(readLine().trim());
                if (n > 0) return n;
            } catch (NumberFormatException ignored) { }
            out.println("Please enter a whole number greater than 0.");
        }
    }

    private String readLine() {
        return in.hasNextLine() ? in.nextLine() : "3";
    }
}