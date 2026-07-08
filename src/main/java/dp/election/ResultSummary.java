package dp.election;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ResultSummary {
    public static void main(String[] args) {
        File resultsDirectory = new File("results");

        if (!resultsDirectory.exists()) {
            resultsDirectory.mkdirs();
        }

        try (
                BufferedReader reader = new BufferedReader(new FileReader("results/results.csv"));
                PrintWriter writer = new PrintWriter(new FileWriter("results/summary.csv"))
        ) {
            String header = reader.readLine();

            if (header == null) {
                throw new IllegalStateException("results/results.csv is empty.");
            }

            writer.println("algorithm,n,case,average_messages,average_phases,all_correct");

            Summary[] summaries = new Summary[1000];
            int summaryCount = 0;

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                String algorithm = parts[0];
                int n = Integer.parseInt(parts[1]);
                String testCase = parts[2];
                int messages = Integer.parseInt(parts[6]);
                int phases = Integer.parseInt(parts[7]);
                boolean correct = Boolean.parseBoolean(parts[8]);

                Summary summary = findSummary(summaries, summaryCount, algorithm, n, testCase);

                if (summary == null) {
                    summary = new Summary(algorithm, n, testCase);
                    summaries[summaryCount] = summary;
                    summaryCount++;
                }

                summary.add(messages, phases, correct);
            }

            for (int i = 0; i < summaryCount; i++) {
                Summary summary = summaries[i];

                writer.println(
                        summary.algorithm + "," +
                        summary.n + "," +
                        summary.testCase + "," +
                        summary.averageMessages() + "," +
                        summary.averagePhases() + "," +
                        summary.allCorrect
                );

                System.out.println(
                        summary.algorithm +
                        " | N=" + summary.n +
                        " | case=" + summary.testCase +
                        " | avg messages=" + summary.averageMessages() +
                        " | avg phases=" + summary.averagePhases() +
                        " | all correct=" + summary.allCorrect
                );
            }

            System.out.println();
            System.out.println("Summary saved to results/summary.csv");

        } catch (IOException e) {
            System.err.println("Error while reading or writing results: " + e.getMessage());
        }
    }

    private static Summary findSummary(
            Summary[] summaries,
            int summaryCount,
            String algorithm,
            int n,
            String testCase
    ) {
        for (int i = 0; i < summaryCount; i++) {
            Summary summary = summaries[i];

            if (
                    summary.algorithm.equals(algorithm) &&
                    summary.n == n &&
                    summary.testCase.equals(testCase)
            ) {
                return summary;
            }
        }

        return null;
    }

    private static class Summary {
        private final String algorithm;
        private final int n;
        private final String testCase;

        private int count = 0;
        private int totalMessages = 0;
        private int totalPhases = 0;
        private boolean allCorrect = true;

        private Summary(String algorithm, int n, String testCase) {
            this.algorithm = algorithm;
            this.n = n;
            this.testCase = testCase;
        }

        private void add(int messages, int phases, boolean correct) {
            count++;
            totalMessages += messages;
            totalPhases += phases;

            if (!correct) {
                allCorrect = false;
            }
        }

        private double averageMessages() {
            return ((double) totalMessages) / count;
        }

        private double averagePhases() {
            return ((double) totalPhases) / count;
        }
    }
}
