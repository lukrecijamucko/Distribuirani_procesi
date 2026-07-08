package dp.election.simulator;

public class ProcessState {
    public enum Status {
        UNKNOWN,
        LEADER
    }

    private final int uid;
    private Status status;
    private boolean isCandidate;
    private int phase;
    private int repliesReceived;

    public ProcessState(int uid) {
        this.uid = uid;
        this.status = Status.UNKNOWN;
        this.isCandidate = true; 
        this.phase = 0;
        this.repliesReceived = 0;
    }

    public int getUid() { return uid; }
    
    public Status getStatus() { return status; }
    public void setLeader() { this.status = Status.LEADER; }

    public boolean isCandidate() { return isCandidate; }
    public void defeat() { this.isCandidate = false; }

    public int getPhase() { return phase; }
    public void advancePhase() { 
        this.phase++; 
        this.repliesReceived = 0; // Reset replies for the new phase
    }

    public void addReply() { this.repliesReceived++; }
    public boolean hasAllReplies() { return this.repliesReceived == 2; }
}