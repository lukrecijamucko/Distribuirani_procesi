package dp.election;

import dp.election.algorithms.PetersonElection;
import dp.election.algorithms.HirschbergSinclairElection;
import dp.election.simulator.Ring;

public class Main {
    public static void main(String[] args) {
        int[] ids = {
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
        };

        Ring ring = new Ring(ids);

        PetersonElection peterson = new PetersonElection(ring);
        peterson.startElection();

        System.out.println(peterson.getResult());

        HirschbergSinclairElection HSElection = new HirschbergSinclairElection(ring);
        HSElection.startElection();

        System.out.println(HSElection.getResult());

    }
}