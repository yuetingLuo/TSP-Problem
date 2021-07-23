package SPRING2021.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Problem {
    private int nbCities;
    private String fileName;
    private int[][] distances;

    public Problem(int nbCities, String fileName) {
        this.nbCities = nbCities;
        this.fileName = fileName;
        this.distances = new int[nbCities][nbCities];
    }

    public void constructFitness() {
        for (int i = 0; i < nbCities; i++) {
            distances[i] = new int[nbCities];
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String sCurrentLine;
            int i = -1;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] res = sCurrentLine.split("\\s+");
                int h = 0;
                i++;
                for (int j = i + 1; j < nbCities; j++) {
                    distances[i][j] = Integer.parseInt(res[h]);
                    distances[j][i] = distances[i][j];
                    h++;
                }

            }
            for (int k = 0; k < nbCities; k++) {
                distances[k][k] = -10;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*** Get random between 0 and born -1**/
    public static int getRandom(int start, int end) {
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(end-start) + start;
        return randomInt;
    }

    public void printDistances() {
        for (int a = 0; a < nbCities; a++) {
            for (int b = 0; b < nbCities; b++) {
                System.out.print(distances[a][b] + " ");
            }
            System.out.println();
        }
    }

    public int getNbCities() {
        return nbCities;
    }

    public int[][] getDistances() {
        return distances;
    }

    public int getEdgeDistance(int i, int j) {
        return distances[i][j];
    }
}
