package SPRING2021.project;

import java.util.Random;

public class Hillclimbing {
    /*** The TSP Question**/
    public static Problem currentProblem;

    /*** Space for 2-opt**/
    private static int[] optimalState;
    private static int optimalDistance;

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

    /*** Get random between 0 and born -1**/
    public static int getRandom(int borne) {
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(borne);
        return randomInt;
    }

    /*** Calculate the distance**/
    public static int calDistance(Problem inputProblem, int[] state) {
        int distance = 0;
        for (int i = 0; i < inputProblem.getNbCities() - 1; i++){
            // Add edges one by one
            distance += inputProblem.getDistances()[state[i]][state[i + 1]];
        }
        // Add the Last Edge
        distance += inputProblem.getDistances()[state[0]][state[inputProblem.getNbCities() - 1]];
        return distance;
    }
    public static int calDistance(int[] state) {
        return calDistance(currentProblem, state);
    }

    /*** Check the Neighbor State in 1-Opt: Simple Approach**/
    private static boolean getNewState_1opt_simple() {
        int currentDistance;
        // Swap two nodes to get new state
        for (int i = 0; i < currentProblem.getNbCities() - 1; i++) {
            for (int j = i + 1; j < currentProblem.getNbCities(); j++) {
                elementSwitch(optimalState, i, j);
                // Calculate and compare the distance
                currentDistance = calDistance(optimalState);
                // Judge whether get the better solution
                if (currentDistance < optimalDistance) {
                    optimalDistance = currentDistance;
                    return true;
                } else {
                    // Switch Back to the Original State
                    elementSwitch(optimalState, i, j);
                }
            }
        }
        // If Current State is the Local Max/Min
        return false;
    }
    /*** Check the Neighbor State in 1-Opt: Steepest Approach**/
    private static boolean getNewState_1opt_steepest() {
        int[] currentOptimalState = optimalState;
        int currentOptimalDistance = optimalDistance;
        boolean optimalExist = false;
        int currentDistance;
        // Swap two nodes to get new state
        for (int i = 0; i < currentProblem.getNbCities() - 1; i++) {
            for (int j = i + 1; j < currentProblem.getNbCities(); j++) {
                elementSwitch(optimalState, i, j);
                // Calculate and compare the distance
                currentDistance = calDistance(optimalState);
                // Judge whether get the better solution
                if (currentDistance < currentOptimalDistance) {
                    currentOptimalDistance = currentDistance;
                    currentOptimalState = optimalState.clone();
                    optimalExist = true;
                }
                // Switch Back to the Original State
                elementSwitch(optimalState, i, j);
            }
        }
        // Do the Optimization
        if (optimalExist) {
            optimalState = currentOptimalState;
            optimalDistance = currentOptimalDistance;
            return true;
        } else {
            // If Current State is the Local Max/Min
            return false;
        }
    }

    /*** Simple Hall Climbing: Check the Neighbor State in 2-Opt: simple approach**/
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

    /*** Steepest-Ascent Hall Climbing: Check the Neighbor State in 2-Opt: steepest approach**/
    private static boolean getNewState_2opt_steepest() {
        // Container to Store the Tmp Optimal Solution
        int[] currentOptimalState = optimalState;
        int currentOptimalDistance = optimalDistance;
        boolean optimalExist = false;
        int currentDistance;
        // Swap two nodes to get new state
        for (int i = 0; i < currentProblem.getNbCities() - 1; i++) {
            for (int j = i + 1; j < currentProblem.getNbCities(); j++) {
                elementReverse(optimalState, i, j);
                // Calculate and compare the distance
                currentDistance = calDistance(optimalState);
                // Judge whether get the better solution
                if (currentDistance < currentOptimalDistance) {
                    currentOptimalDistance = currentDistance;
                    currentOptimalState = optimalState.clone();
                    optimalExist = true;
                }
                // Switch Back to the Original State
                elementReverse(optimalState, i, j);
            }
        }
        // Do the Optimization
        if (optimalExist) {
            optimalState = currentOptimalState;
            optimalDistance = currentOptimalDistance;
            return true;
        } else {
            // If Current State is the Local Max/Min
            return false;
        }
    }
    /*** Check the Neighbor State in 3-OptPlus: Simple Approach**/
    public static boolean getNewState_3optplus_simple() {
        for (int i = 0; i < currentProblem.getNbCities() - 5; i++) {
            for (int j = i + 2; j < currentProblem.getNbCities() - 3; j++) {
                for (int k = j + 2; k < currentProblem.getNbCities(); k++) {
                    if (findoptimal(optimalState, i, j, k)) {
                        optimalDistance = calDistance(optimalState);
                        return true;
                    }
                }

            }

        }
        return false;
    }
    /*** Check the Neighbor State in 3-Optplus: Steepest Approach**/
    public static boolean getNewState_3optplus_steepest() {
        int[] initial = new int[optimalState.length];
        int[] bestcandidate = new int[optimalState.length];
        System.arraycopy(optimalState, 0, initial, 0, initial.length);
        System.arraycopy(optimalState, 0, bestcandidate, 0, initial.length);
        int bestDistance = calDistance(bestcandidate);
        for (int i = 0; i < currentProblem.getNbCities() - 5; i++) {
            for (int j = i + 2; j < currentProblem.getNbCities() - 3; j++) {
                for (int k = j + 2; k < currentProblem.getNbCities(); k++) {
                    if (findoptimal(optimalState, i, j, k)) {
                        if (calDistance(optimalState) < bestDistance) {
                            System.arraycopy(optimalState, 0, bestcandidate, 0, initial.length);
                            bestDistance = calDistance(bestcandidate);
                        }
                        System.arraycopy(initial, 0, optimalState, 0, initial.length);
                    }
                }
            }
        }
        if (bestDistance < calDistance(optimalState)) {
            System.arraycopy(bestcandidate, 0, optimalState, 0, initial.length);
            optimalDistance = calDistance(optimalState);
            return true;
        }
        return false;
    }

    /*** Compare and return best case in 3optPlus combination**/
    private static boolean findoptimal(int[] arr, int i, int j, int k) {
        int[] currentstate = new int[arr.length];
        int kplus = k + 1;
        if (k == arr.length - 1)
            kplus = 0;

        System.arraycopy(arr, 0, currentstate, 0, currentstate.length);

        Boolean found = false;
        int[] cases = new int[8];
        cases[0] = currentProblem.getEdgeDistance(arr[i], arr[i + 1]) +
                currentProblem.getEdgeDistance(arr[j], arr[j + 1]) +
                currentProblem.getEdgeDistance(arr[k], arr[kplus]);
        int currentmin = cases[0];

        cases[1] = currentProblem.getEdgeDistance(arr[i], arr[i + 1]) +
                currentProblem.getEdgeDistance(arr[j], arr[k]) +
                currentProblem.getEdgeDistance(arr[j + 1], arr[kplus]);

        if (currentmin > cases[1]) {
            elementReverse(currentstate, j + 1, k);
            currentmin = cases[1];
            found = true;
        }

        cases[2] = currentProblem.getEdgeDistance(arr[i], arr[j]) +
                currentProblem.getEdgeDistance(arr[i + 1], arr[j + 1]) +
                currentProblem.getEdgeDistance(arr[k], arr[kplus]);

        if (currentmin > cases[2]) {
            System.arraycopy(arr, 0, currentstate, 0, currentstate.length);
            elementReverse(currentstate, i + 1, j);
            currentmin = cases[2];
            found = true;
        }

        cases[3] = currentProblem.getEdgeDistance(arr[i], arr[j]) +
                currentProblem.getEdgeDistance(arr[i + 1], arr[k]) +
                currentProblem.getEdgeDistance(arr[j + 1], arr[kplus]);

        if (currentmin > cases[3]) {
            System.arraycopy(arr, 0, currentstate, 0, currentstate.length);
            elementReverse(currentstate, i + 1, j);
            elementReverse(currentstate, j + 1, k);
            currentmin = cases[3];
            found = true;
        }

        cases[4] = currentProblem.getEdgeDistance(arr[i], arr[j + 1]) +
                currentProblem.getEdgeDistance(arr[k], arr[i + 1]) +
                currentProblem.getEdgeDistance(arr[j], arr[kplus]);

        if (currentmin > cases[4]) {
            System.arraycopy(arr, 0, currentstate, 0, currentstate.length);

            elementReverse(currentstate, i + 1, j);
            elementReverse(currentstate, j + 1, k);
            elementReverse(currentstate, i + 1, k);

            currentmin = cases[4];
            found = true;
        }

        cases[5] = currentProblem.getEdgeDistance(arr[i], arr[j + 1]) +
                currentProblem.getEdgeDistance(arr[k], arr[j]) +
                currentProblem.getEdgeDistance(arr[i + 1], arr[kplus]);

        if (currentmin > cases[5]) {
            System.arraycopy(arr, 0, currentstate, 0, currentstate.length);
            elementReverse(currentstate, j + 1, k);
            elementReverse(currentstate, i + 1, k);
            currentmin = cases[5];
            found = true;
        }

        cases[6] = currentProblem.getEdgeDistance(arr[i], arr[k]) +
                currentProblem.getEdgeDistance(arr[j + 1], arr[i + 1]) +
                currentProblem.getEdgeDistance(arr[j], arr[kplus]);

        if (currentmin > cases[6]) {
            System.arraycopy(arr, 0, currentstate, 0, currentstate.length);
            elementReverse(currentstate, i + 1, j);
            elementReverse(currentstate, i + 1, k);
            currentmin = cases[6];
            found = true;
        }

        cases[7] = currentProblem.getEdgeDistance(arr[i], arr[k]) +
                currentProblem.getEdgeDistance(arr[j + 1], arr[j]) +
                currentProblem.getEdgeDistance(arr[i + 1], arr[kplus]);

        if (currentmin > cases[7]) {
            System.arraycopy(arr, 0, currentstate, 0, currentstate.length);
            elementReverse(currentstate, i + 1, k);
            found = true;
        }
        if (found) {
            System.arraycopy(currentstate, 0, arr, 0, currentstate.length);
        }
        return found;
    }
    /*** Reverse element method used in 2-opt**/
    public static void elementReverse(int[] cs, int a, int b) {
        while (b > a) {
            int temp = cs[b];
            cs[b] = cs[a];
            cs[a] = temp;
            b--;
            a++;
        }
    }

    /*** check the Neighbor State in Insertion: Simple Approach**/
    private static boolean getNewState_insertion_simple() {
        int currentDistance;
        // Integer to Store the Edge(index - 1, index)
        for (int insertPosition = 0; insertPosition < currentProblem.getNbCities(); insertPosition++) {
            // Integer to Store the Index of Insertion Value
            for (int insertValueIndex = 0; insertValueIndex < currentProblem.getNbCities(); insertValueIndex++) {
                // Two Special Cases:
                // #1: Skip the Adjacent Node
                if (insertPosition == insertValueIndex || insertPosition == insertValueIndex + 1) {
                    continue;
                }
                // #2: Solve the End -> Head Edge Problem
                if (insertPosition == 0 && insertValueIndex == currentProblem.getNbCities() - 1) {
                    continue;
                }

                // Get the Array after the Insertion
                int[] currentState = insertArray(insertPosition, insertValueIndex);
                // Calculate the Distance
                currentDistance = calDistance(currentState);
                // Judge whether get the better solution
                if (currentDistance < optimalDistance) {
                    // Copy to the optimalState
                    optimalState = currentState;
                    // Store the NewState's Distance
                    optimalDistance = currentDistance;
                    return true;
                }
            }
        }
        return false;
    }

    /*** Switch the Position of Two Nodes, Used in 1-Opt and 3-Opt**/
    private static void elementSwitch(int[] list, int index1, int index2) {
        int tmp = list[index1];
        list[index1] = list[index2];
        list[index2] = tmp;
    }

    /*** Insert Element into the Special Position**/
    private static int[] insertArray(int insertPosition, int insertValueIndex) {
        // Generate the NewState
        int[] currentState = new int[currentProblem.getNbCities()];
        // Check the Order of Two Breakpoints
        if (insertPosition < insertValueIndex) {
            // Start from the 0
            System.arraycopy(optimalState, 0, currentState, 0, insertPosition);
            // Insert into the insertPosition
            System.arraycopy(optimalState, insertValueIndex, currentState, insertPosition, 1);
            // Skip the insertValueIndex
            System.arraycopy(optimalState, insertPosition, currentState, insertPosition + 1, insertValueIndex - insertPosition);
            System.arraycopy(optimalState, insertValueIndex + 1, currentState, insertValueIndex + 1, currentProblem.getNbCities() - insertValueIndex - 1);
        } else {
            // Skip the insertValueIndex
            System.arraycopy(optimalState, 0, currentState, 0, insertValueIndex);
            System.arraycopy(optimalState, insertValueIndex + 1, currentState, insertValueIndex, insertPosition - insertValueIndex - 1);
            // Insert into the insertPosition
            System.arraycopy(optimalState, insertValueIndex, currentState, insertPosition - 1, 1);
            System.arraycopy(optimalState, insertPosition, currentState, insertPosition, currentProblem.getNbCities() - insertPosition - 1);
        }
        return currentState;
    }

    /*** Hill Climbing with 3Opt Below* 3Opt is Equal to 1Opt with Limitation**/
    private static boolean getNewState_3opt_simple() {
        int currentDistance;
        // Swap two nodes
        // Set First Node as i, Two Successors is i+1 and i+2
        for (int i = 0; i < currentProblem.getNbCities() - 5; i++) {
            // Set Second Node as j, Two successors is j+1 and j+2
            for (int j = i + 3; j < currentProblem.getNbCities(); j++) {
                int jplusone = j + 1;
                if (j == currentProblem.getNbCities() - 1) {
                    jplusone = 0;
                }

                elementSwitch(optimalState, i + 1, jplusone);
                // Calculate and compare the distance
                currentDistance = calDistance(optimalState);
                // Judge whether get the better solution
                if (currentDistance < optimalDistance) {
                    optimalDistance = currentDistance;
                    return true;
                } else {
                    // Switch Back to the Original State
                    elementSwitch(optimalState, i, j);
                }
            }
        }
        // If Current State is the Local Max/Min
        return false;
    }

    /**
     * Stochastic Hall Climbing: Check the Neighbor State in 2-Opt**/
    private static boolean getNewState_2opt_random(int limit) {
        // Limit is Necessary: Counter will Increase 1 When Get a Bad Neighbour Solution
        int currentDistance;
        // Randomly Reverse Elements Between two nodes to get new state
        int counter = 0;
        // Container of Random Position Switching Variable
        int i, j;
        while (counter < limit) {
            i = getRandom(currentProblem.getNbCities() - 1);
            // Prevent j == i Situation
            do {
                j = getRandom(currentProblem.getNbCities() - 1);
            } while (i == j);

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
                // Increase the Fail Counter
                counter++;
            }
        }
        // If Current State is the Local Max/Min
        return false;
    }

    /*** Stochastic Hall Climbing With Default Iteration Limit*/
    private static boolean getNewState_2opt_random() {
        // Here, I Set the limit as Half Amount of the Possible Solutions
        int limit = (int) Math.pow(currentProblem.getNbCities(), 2) / 4;
        return getNewState_2opt_random(limit);
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

    /** Used in Generic Algorithm **/
    public static route solve_invoke(Problem inputProblem, route startPoint){
        // Start from a random solution(Order & State)
        currentProblem = inputProblem;
        optimalState = startPoint.route;
        optimalDistance = startPoint.distance;

        // Find the Neighbour Solution
        while(getNewState_2opt_simple()){
            continue;
        }
        return new route(optimalState, optimalDistance);
    }

    /*** Call to compare three types of hill climbing algorithms (simple, steepest, random)**/
    public static void typecomparesolve() {
        // Start from a random solution(Order & State)
        optimalState = generateRandom(currentProblem);
        optimalDistance = calDistance(optimalState);
        int[] initialState = new int[optimalState.length];
        System.arraycopy(optimalState, 0, initialState, 0, optimalState.length);

        while (true) {
            if (getNewState_2opt_simple()) {
            } else {
                System.out.print(optimalDistance + " ");
                break;
            }
        }
        System.arraycopy(initialState, 0, optimalState, 0, optimalState.length);
        optimalDistance = calDistance(optimalState);
        while (true) {
            if (getNewState_2opt_steepest()) {
            } else {
                System.out.print(optimalDistance + " ");
                break;
            }
        }
        System.arraycopy(initialState, 0, optimalState, 0, optimalState.length);
        optimalDistance = calDistance(optimalState);
        while (true) {
            if (getNewState_2opt_random()) {
            } else {
                System.out.print(optimalDistance + " ");
                break;
            }
        }
    }

    /*** Call to compare five neighborhood structure**/
    public static void optcomparesolve() {
        // Start from a random solution(Order & State)
        long startTime = System.nanoTime();
        long endTime = System.nanoTime();
        optimalState = generateRandom(currentProblem);
        optimalDistance = calDistance(optimalState);
        // Print out the Initial State
        int[] initialstate = new int[optimalState.length];
        System.arraycopy(optimalState, 0, initialstate, 0, optimalState.length);
        while (true) {
            startTime = System.nanoTime();
            if (getNewState_1opt_simple()) {
            } else {
                endTime = System.nanoTime();
//                System.out.print(endTime - startTime+" ");
                System.out.print(optimalDistance + " ");
                break;
            }
        }

        System.arraycopy(initialstate, 0, optimalState, 0, optimalState.length);
        optimalDistance = calDistance(optimalState);

        while (true) {
            startTime = System.nanoTime();
            if (getNewState_2opt_simple()) {
            } else {
                System.out.print(optimalDistance + " ");
                endTime = System.nanoTime();
//                System.out.print(endTime - startTime+" ");
                break;
            }
        }
        System.arraycopy(initialstate, 0, optimalState, 0, optimalState.length);
        optimalDistance = calDistance(optimalState);

        while (true) {
            startTime = System.nanoTime();
            if (getNewState_3opt_simple()) {
            } else {
                System.out.print(optimalDistance + " ");
                endTime = System.nanoTime();
//                System.out.print(endTime - startTime+" ");
                break;
            }
        }
        System.arraycopy(initialstate, 0, optimalState, 0, optimalState.length);
        optimalDistance = calDistance(optimalState);

        while (true) {
            startTime = System.nanoTime();
            if (getNewState_3optplus_simple()) {
            } else {
                System.out.print(optimalDistance + " ");
                endTime = System.nanoTime();
//                System.out.print(endTime - startTime+" ");
                break;
            }
        }
        System.arraycopy(initialstate, 0, optimalState, 0, optimalState.length);
        optimalDistance = calDistance(optimalState);

        while (true) {
            startTime = System.nanoTime();
            if (getNewState_insertion_simple()) {
            } else {
                System.out.print(optimalDistance + " ");
                endTime = System.nanoTime();
//                System.out.print(endTime - startTime+" ");
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
        System.out.println("1opt 2opt 3opt 3optPlus Insertion");
//        System.out.println("Simple Steepest Stochastic");

        int times = 0;
        while (times < 100) {
            optcomparesolve();
//            typecomparesolve();
            System.out.println();
            times++;
        }
    }
}
