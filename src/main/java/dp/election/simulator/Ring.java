package dp.election.simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ring {
    private final int[] processIds;
    
    // Synchronous network state
    private final List<List<Message>> inboxes;
    private final List<List<Message>> outboxes;

    public Ring(int[] processIds) {
        if (processIds == null || processIds.length == 0) {
            throw new IllegalArgumentException("Ring must contain at least one process.");
        }

        this.processIds = Arrays.copyOf(processIds, processIds.length);
        checkUniqueIds();

        // Initialize inboxes and outboxes for each process
        this.inboxes = new ArrayList<>(processIds.length);
        this.outboxes = new ArrayList<>(processIds.length);
        for (int i = 0; i < processIds.length; i++) {
            this.inboxes.add(new ArrayList<>());
            this.outboxes.add(new ArrayList<>());
        }
    }

    public int size() {
        return processIds.length;
    }

    public int getId(int index) {
        return processIds[index];
    }

    public int[] getProcessIds() {
        return Arrays.copyOf(processIds, processIds.length);
    }

    public int getExpectedLeaderId() {
        int max = processIds[0];
        for (int id : processIds) {
            if (id > max) {
                max = id;
            }
        }
        return max;
    }

    private void checkUniqueIds() {
        for (int i = 0; i < processIds.length; i++) {
            for (int j = i + 1; j < processIds.length; j++) {
                if (processIds[i] == processIds[j]) {
                    throw new IllegalArgumentException("Process IDs must be unique.");
                }
            }
        }
    }

    public int leftOf(int index) {
        return Math.floorMod(index - 1, processIds.length);
    }

    public int rightOf(int index) {
        return Math.floorMod(index + 1, processIds.length);
    }

    // --- NEW SYNCHRONOUS NETWORK METHODS ---

    /**
     * Reads the current messages delivered to a process for this round.
     */
    public List<Message> getMessages(int index) {
        return new ArrayList<>(inboxes.get(index));
    }

    /**
     * Queues a message to be sent by a process at the end of the round.
     */
    public void send(int senderIndex, Message message) {
        outboxes.get(senderIndex).add(message);
    }

    /**
     * Executes the network delivery phase. 
     * Moves all messages from outboxes to their target neighbors' inboxes.
     * Should be called exactly once at the end of every simulation round.
     */
    public void deliverMessages() {
        // Prepare fresh inboxes for the next round
        List<List<Message>> nextInboxes = new ArrayList<>(processIds.length);
        for (int i = 0; i < processIds.length; i++) {
            nextInboxes.add(new ArrayList<>());
        }

        // Route all outgoing messages
        for (int i = 0; i < processIds.length; i++) {
            for (Message msg : outboxes.get(i)) {
                int targetIndex;
                if (msg.getDirection() == Message.Direction.LEFT) {
                    targetIndex = leftOf(i);
                } else {
                    targetIndex = rightOf(i);
                }
                nextInboxes.get(targetIndex).add(msg);
            }
            // Clear the outbox now that messages are in transit
            outboxes.get(i).clear();
        }

        // Swap the old inboxes with the newly delivered messages
        for (int i = 0; i < processIds.length; i++) {
            inboxes.get(i).clear();
            inboxes.get(i).addAll(nextInboxes.get(i));
        }
    }
    

    public boolean hasPendingMessages() {
        for (List<Message> inbox : inboxes) {
            if (!inbox.isEmpty()) return true;
        }
        for (List<Message> outbox : outboxes) {
            if (!outbox.isEmpty()) return true;
        }
        return false;
    }
}