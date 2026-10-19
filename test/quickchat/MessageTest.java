package quickchat;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MessageTest {

    private static final String MSG1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String MSG2 = "Hi Keegan, did you receive the payment?";

    @Before
    public void setUp() {
        Message.resetState();
    }

    @Test
    public void messageLengthSuccess() {
        Message m = new Message("0012345678", 0, "+27718693002", MSG1);
        assertEquals("Message ready to send.", m.checkMessageLength());
    }

    @Test
    public void messageLengthFailure() {
        Message m = new Message("0012345678", 0, "+27718693002", "a".repeat(260));
        assertEquals("Message exceeds 250 characters by 10, please reduce the size.",
                m.checkMessageLength());
    }

    @Test
    public void recipientSuccess() {
        Message m = new Message("0012345678", 0, "+27718693002", MSG1);
        assertEquals("Cell phone number successfully captured.", m.checkRecipientCell());
    }

    @Test
    public void recipientFailure() {
        Message m = new Message("0012345678", 1, "08575975889", MSG2);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                m.checkRecipientCell());
    }

    @Test
    public void messageHashTestCase1() {
        Message m = new Message("0012345678", 0, "+27718693002", MSG1);
        assertEquals("00:0:HITONIGHT", m.createMessageHash());
    }

    @Test
    public void messageHashesInLoop() {
        String[] texts = {MSG1, MSG2, "Thanks", "Hi, mate. Please call me back thanks!"};
        String[] expected = {"00:0:HITONIGHT", "00:1:HIPAYMENT", "00:2:THANKSTHANKS", "00:3:HITHANKS"};
        for (int i = 0; i < texts.length; i++) {
            Message m = new Message("0012345678", i, "+27718693002", texts[i]);
            assertEquals(expected[i], m.createMessageHash());
        }
    }

    @Test
    public void messageIdCreated() {
        Message m = new Message(0, "+27718693002", MSG1);
        assertTrue(m.checkMessageID());
        assertEquals(10, m.getMessageID().length());
    }

    @Test
    public void messageSentSend() {
        Message m = new Message("0012345678", 0, "+27718693002", MSG1);
        assertEquals("Message successfully sent.", m.SentMessage(Message.SEND));
    }

    @Test
    public void messageSentDisregard() {
        Message m = new Message("0098765432", 1, "08575975889", MSG2);
        assertEquals("Press 0 to delete the message.", m.SentMessage(Message.DISREGARD));
    }

    @Test
    public void messageSentStore() {
        Message m = new Message("0012345678", 0, "+27718693002", MSG1);
        assertEquals("Message successfully stored.", m.SentMessage(Message.STORE));
    }

    @Test
    public void totalMessagesAfterSendAndDisregard() {
        new Message("0012345678", 0, "+27718693002", MSG1).SentMessage(Message.SEND);
        new Message("0098765432", 1, "08575975889", MSG2).SentMessage(Message.DISREGARD);
        assertEquals(1, Message.returnTotalMessages());
    }
}