package dp.election.algorithms;

import dp.election.Election;
import dp.election.ElectionResult;
import dp.election.simulator.Ring;

import java.util.ArrayList;
import java.util.List;

public class PetersonElection implements Election {
    private final Ring ring;

    private int leader = -1;
    private int messageCount = 0;
    private int phases = 0;

    private boolean electionStarted = false;

    public PetersonElection(Ring ring) {
        this.ring = ring;
    }

    @Override
    public void startElection() {
        if (electionStarted) {
            return;
        }

        electionStarted = true;

        int n = ring.size();

        if (n == 1) {
            leader = ring.getId(0);
            messageCount = 0;
            phases = 0;
            return;
        }

        boolean[] active = new boolean[n];
        int[] virtualIds = new int[n];

        for (int i = 0; i < n; i++) {
            active[i] = true;
            virtualIds[i] = ring.getId(i);
        }

        while (true) {
            List<Integer> activeProcesses = getActiveProcesses(active);

            if (activeProcesses.size() == 1) {
                int winnerIndex = activeProcesses.get(0);
                leader = virtualIds[winnerIndex];

                /*
                 * Završna obavijest o vođi prolazi kroz cijeli prsten.
                 * U skripti se kod Changa i Robertsa nakon pobjede šalje poruka tipa leader
                 * kako bi svi procesi saznali tko je vođa.
                 */
                messageCount += n;
                phases++;

                return;
            }

            boolean[] newActive = new boolean[n];
            int[] newVirtualIds = virtualIds.clone();

            /*
             * Visokorazinska fazna simulacija Petersonovog algoritma.
             *
             * Ideja:
             * - aktivni procesi ostaju kandidati
             * - pasivni procesi samo prosljeđuju poruke
             * - promatramo samo aktivne procese, kao da se prsten kandidata smanjuje
             *
             * Za svaki aktivni proces gledamo:
             * - njegov trenutni virtualni ID
             * - ID prethodnog aktivnog kandidata
             * - ID kandidata prije prethodnog
             *
             * Ako prethodni kandidat ima veći ID od oba susjeda u toj usporedbi,
             * on preživljava i njegov ID se prenosi dalje.
             */
            for (int position = 0; position < activeProcesses.size(); position++) {
                int currentIndex = activeProcesses.get(position);

                int previousIndex = activeProcesses.get(
                        Math.floorMod(position - 1, activeProcesses.size())
                );

                int previousPreviousIndex = activeProcesses.get(
                        Math.floorMod(position - 2, activeProcesses.size())
                );

                int currentId = virtualIds[currentIndex];
                int previousId = virtualIds[previousIndex];
                int previousPreviousId = virtualIds[previousPreviousIndex];

                if (previousId > currentId && previousId > previousPreviousId) {
                    newActive[currentIndex] = true;
                    newVirtualIds[currentIndex] = previousId;
                } else {
                    newActive[currentIndex] = false;
                }
            }

            /*
             * U jednoj fazi računamo dva prolaza poruka kroz prsten.
             * Ovo je simulacija broja prijenosa poruka, a ne prava mrežna implementacija.
             */
            messageCount += 2 * n;
            phases++;

            if (getActiveProcesses(newActive).isEmpty()) {
                throw new IllegalStateException("Peterson simulation error: no active process remained.");
            }

            active = newActive;
            virtualIds = newVirtualIds;
        }
    }

    @Override
    public int getLeader() {
        if (!electionStarted) {
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

    private List<Integer> getActiveProcesses(boolean[] active) {
        List<Integer> activeProcesses = new ArrayList<>();

        for (int i = 0; i < active.length; i++) {
            if (active[i]) {
                activeProcesses.add(i);
            }
        }

        return activeProcesses;
    }
}