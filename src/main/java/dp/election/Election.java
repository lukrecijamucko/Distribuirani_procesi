package dp.election;

public interface Election {
    void startElection();

    int getLeader();

    ElectionResult getResult();
}