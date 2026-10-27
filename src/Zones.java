import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** Missions 4 and 5: SCCs and dynamic programming on their DAG. */
public class Zones {
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] from = Graphs.slidesFrom(s), into = Graphs.slidesInto(s);
        boolean[] seen = new boolean[n];
        int[] stack = new int[n], next = new int[n], finished = new int[n];
        int finishCount = 0;
        for (int start = 0; start < n; start++) {
            if (seen[start]) continue;
            int top = 0;
            stack[0] = start;
            seen[start] = true;
            while (top >= 0) {
                int u = stack[top];
                if (next[u] == from[u].length) {
                    finished[finishCount++] = u;
                    top--;
                } else {
                    int v = s.to()[from[u][next[u]++]];
                    if (!seen[v]) { seen[v] = true; stack[++top] = v; }
                }
            }
        }
        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        int zone = 0;
        for (int i = finishCount - 1; i >= 0; i--) {
            int start = finished[i];
            if (zoneOf[start] != -1) continue;
            int top = 0;
            stack[0] = start;
            zoneOf[start] = zone;
            while (top >= 0) {
                int u = stack[top--];
                for (int edge : into[u]) {
                    int v = s.from()[edge];
                    if (zoneOf[v] == -1) {
                        zoneOf[v] = zone;
                        stack[++top] = v;
                    }
                }
            }
            zone++;
        }
        return zoneOf;
    }

    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        java.util.List<Map<Integer, Integer>> graph = new java.util.ArrayList<>();
        for (int z = 0; z < zoneCount; z++) graph.add(new HashMap<>());
        for (int i = 0; i < s.count(); i++) {
            int a = zoneOf[s.from()[i]], b = zoneOf[s.to()[i]];
            if (a != b) graph.get(a).merge(b, s.time()[i], Math::min);
        }
        int[] stages = new int[zoneCount];
        Arrays.fill(stages, 1);
        long[] distance = new long[zoneCount];
        Arrays.fill(distance, Long.MAX_VALUE);
        distance[zoneOf[0]] = 0;
        int longest = 0;
        for (int a = 0; a < zoneCount; a++) {
            longest = Math.max(longest, stages[a]);
            for (Map.Entry<Integer, Integer> edge : graph.get(a).entrySet()) {
                int b = edge.getKey();
                stages[b] = Math.max(stages[b], stages[a] + 1);
                if (distance[a] != Long.MAX_VALUE) {
                    distance[b] = Math.min(distance[b], distance[a] + edge.getValue());
                }
            }
        }
        return new long[]{longest, distance[zoneOf[s.n() - 1]]};
    }
}
