package dp.election.algorithms;

import dp.election.Election;
import dp.election.ElectionResult;
import dp.election.simulator.Message;
import dp.election.simulator.ProcessState;
import dp.election.simulator.Ring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HirschbergSinclairElection implements Election {

    private final Ring ring;
    private final Map<Integer, ProcessState> states;
    private int electedLeaderId = -1;

    // Tracking variables for your ElectionResult
    private int messageCount = 0;
    private int maxPhase = 0;

    public HirschbergSinclairElection(Ring ring) {
        this.ring = ring;
        this.states = new HashMap<>();

        // Initialize state for each process in the ring
        for (int i = 0; i < ring.size(); i++) {
            states.put(i, new ProcessState(ring.getId(i)));
        }
    }

    // Helper method to keep our message counting perfectly accurate
    private void sendMessage(int senderIndex, Message msg) {
        ring.send(senderIndex, msg);
        messageCount++;
    }

    @Override
    public void startElection() {
        // ROUND 0: Initial kick-off
        for (int i = 0; i < ring.size(); i++) {
            ProcessState state = states.get(i);
            int initialHops = (int) Math.pow(2, state.getPhase());
            
            sendMessage(i, new Message(state.getUid(), i, Message.Type.OUT, Message.Direction.LEFT, initialHops));
            sendMessage(i, new Message(state.getUid(), i, Message.Type.OUT, Message.Direction.RIGHT, initialHops));
        }

        // Main Synchronous Loop
        while (electedLeaderId == -1) {
            // Deliver messages from outboxes to inboxes
            ring.deliverMessages();

            // Process inboxes
            for (int i = 0; i < ring.size(); i++) {
                ProcessState myState = states.get(i);
                List<Message> inbox = ring.getMessages(i);

                for (Message msg : inbox) {
                    processMessage(i, myState, msg);
                }
            }

            if (!ring.hasPendingMessages() && electedLeaderId == -1) {
                throw new IllegalStateException("Deadlock: Network is quiet but no leader was elected.");
            }
        }
    }

    @Override
    public int getLeader() {
        return electedLeaderId;
    }

    @Override
    public ElectionResult getResult() {
        if (electedLeaderId == -1) {
            throw new IllegalStateException("Election has not finished yet!");
        }
        
        // Returns your exact ElectionResult setup
        return new ElectionResult(
                "Hirschberg-Sinclair",
                ring.size(),
                electedLeaderId,
                ring.getExpectedLeaderId(), // Already built into your Ring class!
                messageCount,
                maxPhase
        );
    }

    private void processMessage(int myIndex, ProcessState myState, Message msg) {
        if (msg.getType() == Message.Type.OUT) {
            if (msg.getCandidateId() == myState.getUid()) {
                myState.setLeader();
                this.electedLeaderId = myState.getUid();
            } 
            else if (msg.getCandidateId() > myState.getUid()) {
                myState.defeat();

                if (msg.getRemainingHops() > 1) {
                    sendMessage(myIndex, new Message(
                            msg.getCandidateId(), msg.getOriginIndex(), 
                            Message.Type.OUT, msg.getDirection(), 
                            msg.getRemainingHops() - 1));
                } else {
                    Message.Direction replyDir = (msg.getDirection() == Message.Direction.LEFT) 
                            ? Message.Direction.RIGHT : Message.Direction.LEFT;
                    
                    sendMessage(myIndex, new Message(
                            msg.getCandidateId(), msg.getOriginIndex(), 
                            Message.Type.IN, replyDir, 0));
                }
            }
        } 
        else if (msg.getType() == Message.Type.IN) {
            if (msg.getCandidateId() == myState.getUid()) {
                myState.addReply();

                if (myState.hasAllReplies()) {
                    myState.advancePhase();
                    
                    // Track highest phase reached for ElectionResult
                    if (myState.getPhase() > maxPhase) {
                        maxPhase = myState.getPhase();
                    }

                    int nextHops = (int) Math.pow(2, myState.getPhase());

                    sendMessage(myIndex, new Message(myState.getUid(), myIndex, Message.Type.OUT, Message.Direction.LEFT, nextHops));
                    sendMessage(myIndex, new Message(myState.getUid(), myIndex, Message.Type.OUT, Message.Direction.RIGHT, nextHops));
                }
            } else {
                sendMessage(myIndex, new Message(
                        msg.getCandidateId(), msg.getOriginIndex(), 
                        Message.Type.IN, msg.getDirection(), 0));
            }
        }
    }
}