package dp.election.algorithms;

import dp.election.Election;
import dp.election.ElectionResult;
import dp.election.simulator.Message;
import dp.election.simulator.ProcessState;
import dp.election.simulator.Ring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangRobertsElection implements Election {

    private final Ring ring;
    private final Map<Integer, ProcessState> states;
    private int electedLeaderId = -1;

    private int messageCount = 0;
    private int rounds = 0; // stand-in for ElectionResult's "maxPhase" slot;
                             // Chang-Roberts has no phase concept, so this
                             // reports the number of synchronous rounds the
                             // simulation ran for instead.

    public ChangRobertsElection(Ring ring) {
        this.ring = ring;
        this.states = new HashMap<>();

        for (int i = 0; i < ring.size(); i++) {
            states.put(i, new ProcessState(ring.getId(i)));
        }
    }

    private void sendMessage(int senderIndex, Message msg) {
        ring.send(senderIndex, msg);
        messageCount++;
    }

    @Override
    public void startElection() {
        // ROUND 0: every process announces its own candidacy to its right neighbor.
        for (int i = 0; i < ring.size(); i++) {
            ProcessState state = states.get(i);
            sendMessage(i, new Message(state.getUid(), i, Message.Type.OUT, Message.Direction.RIGHT, 1));
        }

        while (electedLeaderId == -1) {
            ring.deliverMessages();
            rounds++;

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

        // Let the leader-announcement lap finish circulating so every
        // process has actually learned the result before we return.
        while (ring.hasPendingMessages()) {
            ring.deliverMessages();
            for (int i = 0; i < ring.size(); i++) {
                ProcessState myState = states.get(i);
                for (Message msg : ring.getMessages(i)) {
                    processMessage(i, myState, msg);
                }
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

        return new ElectionResult(
                "Chang-Roberts",
                ring.size(),
                electedLeaderId,
                ring.getExpectedLeaderId(),
                messageCount,
                rounds
        );
    }

    private void processMessage(int myIndex, ProcessState myState, Message msg) {
        if (msg.getType() == Message.Type.OUT) {
            int candidateId = msg.getCandidateId();

            if (candidateId == myState.getUid()) {
                // My own id survived a full trip around the ring - I win.
                myState.setLeader();
                electedLeaderId = candidateId;

                sendMessage(myIndex, new Message(
                        candidateId, myIndex, Message.Type.IN, Message.Direction.RIGHT, 1));
            } else if (candidateId > myState.getUid()) {
                sendMessage(myIndex, new Message(
                        candidateId, msg.getOriginIndex(), Message.Type.OUT, Message.Direction.RIGHT, 1));
            } else {
                // candidateId < myState.getUid(): I'm bigger, so I stay
                // silent and drop this message. I've already sent my own
                // candidacy, so nothing further to do here.
                myState.defeat();
            }
        } else if (msg.getType() == Message.Type.IN) {
            // Leader announcement lap.
            if (msg.getOriginIndex() != myIndex) {
                sendMessage(myIndex, new Message(
                        msg.getCandidateId(), msg.getOriginIndex(), Message.Type.IN, Message.Direction.RIGHT, 1));
            }
            // else: announcement has returned to the leader; stop forwarding.
        }
    }
}
