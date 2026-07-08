package dp.election.simulator;

public class Message {

    public enum Type {
        OUT,
        IN
    }

    public enum Direction {
        LEFT,
        RIGHT
    }


    private final int candidateId;
    private final int originIndex;

    private final Type type;
    private final Direction direction;

    private int remainingHops;


    public Message(
            int candidateId,
            int originIndex,
            Type type,
            Direction direction,
            int remainingHops
    ) {
        this.candidateId = candidateId;
        this.originIndex = originIndex;
        this.type = type;
        this.direction = direction;
        this.remainingHops = remainingHops;
    }


    public int getCandidateId() {
        return candidateId;
    }


    public int getOriginIndex() {
        return originIndex;
    }


    public Type getType() {
        return type;
    }


    public Direction getDirection() {
        return direction;
    }


    public int getRemainingHops() {
        return remainingHops;
    }


    public void decreaseHop() {
        remainingHops--;
    }
}