package dp.election;

public class ElectionResult {
    private final String algorithmName;
    private final int numberOfProcesses;
    private final int leaderId;
    private final int expectedLeaderId;
    private final int messageCount;
    private final int phases;
    private final boolean correct;

    public ElectionResult(
            String algorithmName,
            int numberOfProcesses,
            int leaderId,
            int expectedLeaderId,
            int messageCount,
            int phases
    ) {
        this.algorithmName = algorithmName;
        this.numberOfProcesses = numberOfProcesses;
        this.leaderId = leaderId;
        this.expectedLeaderId = expectedLeaderId;
        this.messageCount = messageCount;
        this.phases = phases;
        this.correct = leaderId == expectedLeaderId;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public int getNumberOfProcesses() {
        return numberOfProcesses;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public int getExpectedLeaderId() {
        return expectedLeaderId;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public int getPhases() {
        return phases;
    }

    public boolean isCorrect() {
        return correct;
    }

    @Override
    public String toString() {
        return algorithmName +
                " | N=" + numberOfProcesses +
                " | leader=" + leaderId +
                " | expected=" + expectedLeaderId +
                " | messages=" + messageCount +
                " | phases=" + phases +
                " | correct=" + correct;
    }
}