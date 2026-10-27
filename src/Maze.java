/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet sets = new DisjointSet(w.cells());
        int[] broken = new int[w.cells() - 1];
        int count = 0;
        for (int k = 0; k < w.count(); k++) {
            if (sets.union(w.a()[k], w.b()[k])) broken[count++] = k;
        }
        return java.util.Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        Integer[] order = new Integer[w.count()];
        for (int k = 0; k < order.length; k++) order[k] = k;
        java.util.Arrays.sort(order, java.util.Comparator.comparingInt(k -> w.cost()[k]));
        DisjointSet sets = new DisjointSet(w.cells());
        int[] broken = new int[w.cells() - 1];
        int count = 0;
        for (int k : order) {
            if (sets.union(w.a()[k], w.b()[k])) {
                broken[count++] = k;
                if (count == broken.length) break;
            }
        }
        return java.util.Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        int[] degree = new int[n];
        for (int k = 0; k < w.count(); k++) {
            degree[w.a()[k]]++; degree[w.b()[k]]++;
        }
        int[][] edges = new int[n][];
        for (int u = 0; u < n; u++) edges[u] = new int[degree[u]];
        int[] filled = new int[n];
        for (int k = 0; k < w.count(); k++) {
            edges[w.a()[k]][filled[w.a()[k]]++] = k;
            edges[w.b()[k]][filled[w.b()[k]]++] = k;
        }
        boolean[] used = new boolean[n];
        java.util.PriorityQueue<Integer> queue = new java.util.PriorityQueue<>(
                java.util.Comparator.comparingInt(k -> w.cost()[k]));
        used[0] = true;
        for (int k : edges[0]) queue.add(k);
        long total = 0;
        int reached = 1;
        while (!queue.isEmpty() && reached < n) {
            int k = queue.remove();
            int a = w.a()[k], b = w.b()[k];
            if (used[a] && used[b]) continue;
            int next = used[a] ? b : a;
            used[next] = true;
            reached++;
            total += w.cost()[k];
            for (int edge : edges[next]) {
                int other = w.a()[edge] == next ? w.b()[edge] : w.a()[edge];
                if (!used[other]) queue.add(edge);
            }
        }
        if (reached != n) throw new IllegalArgumentException("Disconnected maze");
        return total;
    }
}
