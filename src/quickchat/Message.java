package quickchat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class Message {

    public static final int MAX_LENGTH = 250;
    public static final int SEND = 1;
    public static final int DISREGARD = 2;
    public static final int STORE = 3;

    private static final Pattern RECIPIENT = Pattern.compile("^\\+[0-9]{1,3}[0-9]{9,10}$");
    private static final List<Message> SENT = new ArrayList<>();
    private static final List<Message> STORED = new ArrayList<>();
    private static final Random RANDOM = new Random();

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String message;
    private final String messageHash;

    public Message(int messageNumber, String recipient, String message) {
        this(generateMessageID(), messageNumber, recipient, message);
    }

    public Message(String messageID, int messageNumber, String recipient, String message) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash();
    }

    public static String generateMessageID() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) sb.append(RANDOM.nextInt(10));
        return sb.toString();
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    public String checkRecipientCell() {
        if (recipient != null && RECIPIENT.matcher(recipient).matches()) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    public String checkMessageLength() {
        int length = message == null ? 0 : message.length();
        if (length <= MAX_LENGTH) return "Message ready to send.";
        return "Message exceeds " + MAX_LENGTH + " characters by " + (length - MAX_LENGTH)
                + ", please reduce the size.";
    }

    public String createMessageHash() {
        String prefix = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        String[] words = (message == null ? "" : message.trim()).split("\\s+");
        String first = words[0].replaceAll("[^A-Za-z0-9]", "");
        String last = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");
        return (prefix + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    public String SentMessage(int choice) {
        switch (choice) {
            case SEND:
                SENT.add(this);
                return "Message successfully sent.";
            case DISREGARD:
                return "Press 0 to delete the message.";
            case STORE:
                STORED.add(this);
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message m : SENT) sb.append(m.details()).append(System.lineSeparator());
        return sb.toString();
    }

    public static int returnTotalMessages() {
        return SENT.size();
    }

    public static void storeMessage() {
        storeMessage(new File("messages.json"));
    }

    public static void storeMessage(File file) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < STORED.size(); i++) {
            sb.append(STORED.get(i).toJson());
            sb.append(i < STORED.size() - 1 ? ",\n" : "\n");
        }
        sb.append("]\n");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(sb.toString());
        } catch (IOException e) {
            System.out.println("Could not store message: " + e.getMessage());
        }
    }

    public String toJson() {
        return "  {\n"
                + "    \"messageID\": \"" + esc(messageID) + "\",\n"
                + "    \"messageHash\": \"" + esc(messageHash) + "\",\n"
                + "    \"recipient\": \"" + esc(recipient) + "\",\n"
                + "    \"message\": \"" + esc(message) + "\"\n"
                + "  }";
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    public String details() {
        return "Message ID: " + messageID + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient + "\nMessage: " + message + "\n";
    }

    public static void resetState() {
        SENT.clear();
        STORED.clear();
    }

    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
}