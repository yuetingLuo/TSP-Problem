package SPRING2021.project;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import static SPRING2021.project.Hillclimbing.getRandom;

public class TabuSearch {
    /*** The TSP Question**/
    public static Problem currentProblem;
    /*** Space for 2-opt**/
    private static int[] optimalState;
    private static int optimalDistance;
    private static Queue<int[]> tabulist = new LinkedList<>();

    /*** Initialize a solution : a cycle passing by all cities**/
    public static int[] generateRandom(Problem inputProblem) {
        // Container to Store the Order of the Cities
        int[] cities = new int[inputProblem.getNbCities()];
        // A Boolean Array to Prevent Duplicate
        boolean[] visited = new boolean[inputProblem.getNbCities()];
        // Int to Store Random Index
        int randomIndex = -1;
        // Trip Starts from Index 0
        cities[0] = 0;
        visited[0] = true;
        for (int i = 1; i < inputProblem.getNbCities(); i++) {
            randomIndex = getRandom(inputProblem.getNbCities());
            if (visited[randomIndex]) {
                // Repeat the process
                i--;
            } else {
                cities[i] = randomIndex;
                visited[randomIndex] = true;
            }
        }
        // Return the cities
        return cities;
    }

    /** Solve the Problem in Tabu Search with 2Opt **/
    public static void solve_tabu(boolean execAspiration, int tabuLength, int iterationLimit) {
        // Define Last Best Neighbour
        int[] lastBestNeighbour = new int[optimalState.length];
        System.arraycopy(optimalState, 0, lastBestNeighbour, 0, optimalState.length);

        // Record the Best Switch Index and Distance in the Current Iteration
        int currentBestDistance;
        Integer[] currentBestSwitch;

        // Create the Tabu List to Store the Reversing Nodes
        ArrayList<Integer[]> tabuList = new ArrayList<Integer[]>(tabuLength + 1);
        tabuList.add(new Integer[]{lastBestNeighbour[0], lastBestNeighbour[1]});

        // Loop until the End Situation
        int currentDistance;
        Integer[] currentSwitch;
        int tabuIndex;
        while (iterationLimit > 0) {
            // Init the First Neighbour of lastBestNeighbour as the Best Neighbour
            elementReverse(lastBestNeighbour, 0, 1);
            currentBestDistance = calDistance(lastBestNeighbour);
            currentBestSwitch = new Integer[]{0, 1};
            elementReverse(lastBestNeighbour, 0, 1);

            // Use 2opt_steepest to Go through All the Neighbour
            for (int i = 0; i < currentProblem.getNbCities() - 1; i++) {
                for (int j = i + 1; j < currentProblem.getNbCities(); j++) {
                    elementReverse(lastBestNeighbour, i, j);
                    // Calculate and compare the distance
                    currentDistance = calDistance(lastBestNeighbour);
                    // Judge whether in the TabuList
                    currentSwitch = new Integer[]{i, j};
                    tabuIndex = inTabuList(tabuList, new Integer[]{lastBestNeighbour[currentSwitch[0]],
                            lastBestNeighbour[currentSwitch[1]]});
                    if (tabuIndex == -1) {
                        // Check whether Better than currentBestNeighbour
                        if (currentDistance < currentBestDistance) {
                            currentBestDistance = currentDistance;
                            System.arraycopy(currentSwitch, 0, currentBestSwitch, 0, 2);
                        }
                        // Judge whether obey the aspiration criteria
                    } else if (execAspiration && (currentDistance<optimalDistance)){
                        currentBestDistance = currentDistance;
                        System.arraycopy(currentSwitch, 0, currentBestSwitch, 0, 2);
                        // Refresh the {i, j} in tabuList
                        tabuList.remove(tabuIndex);
                        tabuList.add(currentBestSwitch);
                    }
                    // Switch Back to the Original State
                    elementReverse(lastBestNeighbour, i, j);
                }
            }
            // Perform Current Best Solution
            elementReverse(lastBestNeighbour, currentBestSwitch[0], currentBestSwitch[1]);

            // Judge Whether Greater than the OptimalState
            if (currentBestDistance < optimalDistance){
                System.arraycopy(lastBestNeighbour, 0, optimalState, 0, optimalState.length);
                optimalDistance = currentBestDistance;
            }
            tabuList.add(new Integer[]{lastBestNeighbour[currentBestSwitch[0]],
                    lastBestNeighbour[currentBestSwitch[1]]});
            if(tabuList.size() > tabuLength) {
                tabuList.remove(0);
            }
            // Decrease Iteration Count
            iterationLimit--;
        }
    }
    /** Check Whether the Integer{i, j} is in the TabuList **/
    private static int inTabuList(ArrayList<Integer[]> tabuList, Integer[] switchPoint){
        int tabuIndex = 0;
        for (Integer[] item: tabuList) {
            if (switchPoint[0].equals(item[0]) && switchPoint[1].equals(item[1])) {
                return tabuIndex;
            }
            tabuIndex++;
        }
        return -1;
    }
    /*** Reverse element method**/
    public static void elementReverse(int[] cs, int a, int b) {
        while (b > a) {
            int temp = cs[b];
            cs[b] = cs[a];
            cs[a] = temp;
            b--;
            a++;
        }
    }

    /*** Calculate the distance**/
    public static int calDistance(int[] state) {
        int distance = 0;
        for (int i = 0; i < currentProblem.getNbCities() - 1; i++) {
            // Add edges one by one
            distance += currentProblem.getDistances()[state[i]][state[i + 1]];
        }
        // Add the last edge
        distance += currentProblem.getDistances()[state[0]][state[currentProblem.getNbCities() - 1]];
        return distance;
    }

    /*** Simple Hall Climbing: Check the Neighbor State in 2-Opt**/
    private static boolean getNewState_2opt_simple() {
        int currentDistance;
        // Swap two nodes to get new state
        for (int i = 0; i < currentProblem.getNbCities() - 1; i++) {
            for (int j = i + 1; j < currentProblem.getNbCities(); j++) {
                elementReverse(optimalState, i, j);
                // Calculate and compare the distance
                currentDistance = calDistance(optimalState);
                // Judge whether get the better solution
                if (currentDistance < optimalDistance) {
                    optimalDistance = currentDistance;
                    return true;
                } else {
                    // Switch Back to the Original State
                    elementReverse(optimalState, i, j);
                }
            }
        }
        // If Current State is the Local Max/Min
        return false;
    }


    /*** Print out the State**/
    public static void printSolution(int[] state, int distance) {
        // Print out the total distance
        System.out.print("Distance: " + distance + "Km | With Route: ");
        // Print out the route
        for (int i = 0; i < currentProblem.getNbCities(); i++) {
            System.out.print(state[i] + " -> ");
        }
        // Print out the starting point
        System.out.println(state[0]);
    }

    public static void printSolution(int[] state) {
        printSolution(state, calDistance(state));
    }

    /*** call for compare tabu search and 2opt**/
    public static void tabuCompare2opt() {
        // Start from a random solution(Order & State)
        optimalState = generateRandom(currentProblem);
        optimalDistance = calDistance(optimalState);
        int[] initialstate = new int[optimalState.length];
        System.arraycopy(optimalState, 0, initialstate, 0, optimalState.length);
        solve_tabu(true, 40, 200);
        System.out.print(optimalDistance + " ");

        System.arraycopy(initialstate, 0, optimalState, 0, optimalState.length);
        optimalDistance = calDistance(optimalState);
        while (true) {
            if (getNewState_2opt_simple()) {
            } else {
                System.out.println(optimalDistance);
                break;
            }
        }
    }


    public static void main(String[] args) {
        int nbCities = 50;
        String fileName = "src/SPRING2021/project/50.txt";
        // Define the TSP Problem
        currentProblem = new Problem(nbCities, fileName);
        currentProblem.constructFitness();
        // Start tabu algorithm
//        int timesofFindOptimal = 0;
        System.out.println("Tabu 2opt");
        int times = 0;
        while (times < 300) {
            tabuCompare2opt();
            times++;
        }
    }
}
