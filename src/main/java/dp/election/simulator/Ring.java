package dp.election.simulator;

import java.util.Arrays;

public class Ring {
    private final int[] processIds;

    public Ring(int[] processIds) {
        if (processIds == null || processIds.length == 0) {
            throw new IllegalArgumentException("Ring must contain at least one process.");
        }

        this.processIds = Arrays.copyOf(processIds, processIds.length);
        checkUniqueIds();
    }

    public int size() {
        return processIds.length;
    }

    public int getId(int index) {
        return processIds[index];
    }

    public int[] getProcessIds() {
        return Arrays.copyOf(processIds, processIds.length);
    }

    public int getExpectedLeaderId() {
        int max = processIds[0];

        for (int id : processIds) {
            if (id > max) {
                max = id;
            }
        }

        return max;
    }

    private void checkUniqueIds() {
        for (int i = 0; i < processIds.length; i++) {
            for (int j = i + 1; j < processIds.length; j++) {
                if (processIds[i] == processIds[j]) {
                    throw new IllegalArgumentException("Process IDs must be unique.");
                }
            }
        }
    }
}