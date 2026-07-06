package dp.election;

import dp.election.algorithms.PetersonElection;
import dp.election.simulator.Ring;

public class Main {
    public static void main(String[] args) {
        int[] ids = {7, 2, 10, 1, 5};

        Ring ring = new Ring(ids);

        PetersonElection peterson = new PetersonElection(ring);
        peterson.startElection();

        System.out.println(peterson.getResult());
    }
}