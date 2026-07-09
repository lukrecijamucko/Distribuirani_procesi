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
                {11, 4, 8, 20, 15, 2},
                {
                    34, 7, 42, 19, 50,
                    3, 28, 11, 46, 15,
                    37, 22, 5, 31, 48,
                    14, 40, 9, 26, 1,
                    45, 18, 33, 12, 29,
                    6, 21, 47, 16, 39,
                    24, 10, 44, 2, 35,
                    49, 17, 30, 8, 41,
                    13, 27, 36, 20, 4,
                    32, 23, 38, 25, 43
                },
                {
                    1, 2, 3, 4, 5,
                    6, 7, 8, 9, 10,
                    11, 12, 13, 14, 15,
                    16, 17, 18, 19, 20,
                    21, 22, 23, 24, 25,
                    26, 27, 28, 29, 30,
                    31, 32, 33, 34, 35,
                    36, 37, 38, 39, 40,
                    41, 42, 43, 44, 45,
                    46, 47, 48, 49, 50
                },
                {
                    83, 17, 56, 92, 4, 71, 38, 99, 12, 65,
                    28, 77, 46, 9, 54, 31, 88, 20, 73, 15,
                    61, 97, 43, 6, 80, 35, 68, 25, 90, 52,
                    11, 75, 49, 2, 84, 29, 63, 96, 18, 57,
                    41, 70, 8, 94, 23, 66, 37, 81, 14, 59,
                    98, 30, 76, 45, 10, 86, 33, 69, 21, 53,
                    95, 5, 79, 27, 62, 39, 87, 16, 58, 72,
                    34, 91, 7, 50, 82, 24, 67, 13, 47, 89,
                    19, 55, 78, 3, 64, 36, 85, 22, 60, 42,
                    100, 26, 74, 48, 93, 1, 51, 40, 32, 44
                },
                {
                    1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                    11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                    21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
                    31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                    41, 42, 43, 44, 45, 46, 47, 48, 49, 50,
                    51, 52, 53, 54, 55, 56, 57, 58, 59, 60,
                    61, 62, 63, 64, 65, 66, 67, 68, 69, 70,
                    71, 72, 73, 74, 75, 76, 77, 78, 79, 80,
                    81, 82, 83, 84, 85, 86, 87, 88, 89, 90,
                    91, 92, 93, 94, 95, 96, 97, 98, 99, 100
                }

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
