package dp.election;

import dp.election.algorithms.ChangRobertsElection;
import dp.election.algorithms.HirschbergSinclairElection;
import dp.election.algorithms.PetersonElection;
import dp.election.simulator.Ring;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class ExperimentRunner {
    private static final int[] N_VALUES = {3, 5, 10, 50, 100, 500, 1000};
    private static final String[] CASES = {"ascending", "descending", "random"};

    public static void main(String[] args) {
        File resultsDirectory = new File("results");

        if (!resultsDirectory.exists()) {
            resultsDirectory.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("results/results.csv"))) {
            writer.println("algorithm,n,case,run,leader_id,expected_leader_id,message_count,phases,correct");

            for (int n : N_VALUES) {
                for (String testCase : CASES) {
                    int numberOfRuns = testCase.equals("random") ? 5 : 1;

                    for (int run = 1; run <= numberOfRuns; run++) {
                        int[] ids = generateIds(n, testCase, run);

                        runSingleAlgorithm(writer, "Chang-Roberts", ids, testCase, run);
                        runSingleAlgorithm(writer, "Hirschberg-Sinclair", ids, testCase, run);
                        runSingleAlgorithm(writer, "Peterson", ids, testCase, run);
                    }
                }
            }

            System.out.println();
            System.out.println("Results saved to results/results.csv");

        } catch (IOException e) {
            System.err.println("Error while writing results file: " + e.getMessage());
        }
    }

    private static void runSingleAlgorithm(
            PrintWriter writer,
            String algorithmName,
            int[] ids,
            String testCase,
            int run
    ) {
        Ring ring = new Ring(ids);
        Election election;

        if (algorithmName.equals("Chang-Roberts")) {
            election = new ChangRobertsElection(ring);
        } else if (algorithmName.equals("Hirschberg-Sinclair")) {
            election = new HirschbergSinclairElection(ring);
        } else if (algorithmName.equals("Peterson")) {
            election = new PetersonElection(ring);
        } else {
            throw new IllegalArgumentException("Unknown algorithm: " + algorithmName);
        }

        election.startElection();
        ElectionResult result = election.getResult();

        writer.println(
                result.getAlgorithmName() + "," +
                result.getNumberOfProcesses() + "," +
                testCase + "," +
                run + "," +
                result.getLeaderId() + "," +
                result.getExpectedLeaderId() + "," +
                result.getMessageCount() + "," +
                result.getPhases() + "," +
                result.isCorrect()
        );

        System.out.println(
                result.getAlgorithmName() +
                " | N=" + result.getNumberOfProcesses() +
                " | case=" + testCase +
                " | run=" + run +
                " | leader=" + result.getLeaderId() +
                " | expected=" + result.getExpectedLeaderId() +
                " | messages=" + result.getMessageCount() +
                " | phases=" + result.getPhases() +
                " | correct=" + result.isCorrect()
        );
    }

    private static int[] generateIds(int n, String testCase, int seed) {
        int[] ids = new int[n];

        for (int i = 0; i < n; i++) {
            ids[i] = i + 1;
        }

        if (testCase.equals("ascending")) {
            return ids;
        }

        if (testCase.equals("descending")) {
            reverse(ids);
            return ids;
        }

        if (testCase.equals("random")) {
            shuffle(ids, seed);
            return ids;
        }

        throw new IllegalArgumentException("Unknown test case: " + testCase);
    }

    private static void reverse(int[] ids) {
        int left = 0;
        int right = ids.length - 1;

        while (left < right) {
            int temp = ids[left];
            ids[left] = ids[right];
            ids[right] = temp;

            left++;
            right--;
        }
    }

    private static void shuffle(int[] ids, int seed) {
        Random random = new Random(seed);

        for (int i = ids.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);

            int temp = ids[i];
            ids[i] = ids[j];
            ids[j] = temp;
        }
    }
}