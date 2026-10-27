import java.util.Arrays;

/** Mission 3: iterative DFS, trying up, right, down, left. */
public class Escape {
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells();
        int[][] open = Graphs.open(w, broken);
        int deadEnds = 0;
        for (int[] neighbours : open) if (neighbours.length == 1) deadEnds++;
        boolean[] seen = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        int[] stack = new int[n];
        int[] direction = new int[n];
        int top = 0, visited = 1;
        stack[0] = 0;
        seen[0] = true;
        while (top >= 0 && !seen[n - 1]) {
            int u = stack[top];
            if (direction[top] == 4) { top--; continue; }
            int d = direction[top]++;
            int v;
            if (d == 0) v = u - w.cols();
            else if (d == 1) v = u % w.cols() + 1 < w.cols() ? u + 1 : -1;
            else if (d == 2) v = u + w.cols();
            else v = u % w.cols() > 0 ? u - 1 : -1;
            if (v < 0 || v >= n || seen[v]) continue;
            boolean door = false;
            for (int neighbour : open[u]) if (neighbour == v) door = true;
            if (!door) continue;
            seen[v] = true;
            parent[v] = u;
            visited++;
            stack[++top] = v;
            direction[top] = 0;
        }
        if (!seen[n - 1]) return new Result(new int[0], visited, deadEnds);
        int length = 0;
        for (int u = n - 1; u != -1; u = parent[u]) length++;
        int[] path = new int[length];
        for (int u = n - 1, i = length - 1; i >= 0; i--, u = parent[u]) path[i] = u;
        return new Result(path, visited, deadEnds);
    }
}
