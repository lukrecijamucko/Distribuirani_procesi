package dp.election.algorithms;

import dp.election.Election;
import dp.election.ElectionResult;
import dp.election.simulator.Ring;

public class PetersonElection implements Election {
    private final Ring ring;

    private int leader = -1;
    private int messageCount = 0;
    private int phases = 0;

    public PetersonElection(Ring ring) {
        this.ring = ring;
    }

    @Override
    public void startElection() {
        // TODO:
        // Ovdje ćemo implementirati Petersonov algoritam.
        // Zasad samo postavljamo očekivanog vođu da provjerimo strukturu projekta.
        leader = ring.getExpectedLeaderId();
    }

    @Override
    public int getLeader() {
        if (leader == -1) {
            startElection();
        }

        return leader;
    }

    public ElectionResult getResult() {
        return new ElectionResult(
                "Peterson",
                ring.size(),
                getLeader(),
                ring.getExpectedLeaderId(),
                messageCount,
                phases
        );
    }
}