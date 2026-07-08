package dp.election;

import dp.election.algorithms.ChangRobertsElection;
import dp.election.algorithms.HirschbergSinclairElection;
import dp.election.algorithms.PetersonElection;
import dp.election.simulator.Ring;

public class TestRunner {
    public static void main(String[] args) {
        int[][] testCases = {
                {3},
                {1, 2},
                {2, 1},
                {3, 1, 2},
                {1, 2, 3},
                {3, 2, 1},
                {7, 2, 10, 1, 5},
                {30, 20, 80, 10, 50},
                {4, 1, 3, 2},
                {11, 4, 8, 20, 15, 2}
        };

        String[] algorithms = {
                "Chang-Roberts",
                "Hirschberg-Sinclair",
                "Peterson"
        };

        int passed = 0;
        int total = testCases.length * algorithms.length;

        for (String algorithm : algorithms) {
            for (int i = 0; i < testCases.length; i++) {
                ElectionResult result = runAlgorithm(algorithm, testCases[i]);

                boolean correct = result.isCorrect();

                if (correct) {
                    passed++;
                }

                System.out.println(
                        algorithm +
                        " | test=" + (i + 1) +
                        " | expected=" + result.getExpectedLeaderId() +
                        " | leader=" + result.getLeaderId() +
                        " | messages=" + result.getMessageCount() +
                        " | phases=" + result.getPhases() +
                        " | correct=" + correct
                );
            }
        }

        System.out.println();
        System.out.println("Passed " + passed + " / " + total + " tests.");

        if (passed != total) {
            throw new IllegalStateException("Some election tests failed.");
        }
    }

    private static ElectionResult runAlgorithm(String algorithm, int[] ids) {
        Ring ring = new Ring(ids);
        Election election;

        if (algorithm.equals("Chang-Roberts")) {
            election = new ChangRobertsElection(ring);
        } else if (algorithm.equals("Hirschberg-Sinclair")) {
            election = new HirschbergSinclairElection(ring);
        } else if (algorithm.equals("Peterson")) {
            election = new PetersonElection(ring);
        } else {
            throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }

        election.startElection();
        return election.getResult();
    }
}
