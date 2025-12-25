package se.hgbrg.aoc2025;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import one.util.streamex.EntryStream;

public class Day07 {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day07/input.txt"));

        char[][] map = lines.stream().map(String::toCharArray).toArray(char[][]::new);

        int x = lines.get(0).indexOf('S');
        int y = 1;

        Set<Coord> splitters = part1(map, x, y, new HashSet<>());

        System.out.println(splitters.size());

        long timelines = part2(map, x, y);

        System.out.println(timelines);
    }

    record Coord(int x, int y) {}

    // Counts number of unique splitters encountered.
    static Set<Coord> part1(char[][] map, int x, int y, Set<Coord> visited) {
        Coord coord = new Coord(x, y);

        if (visited.contains(coord)) {
            return Set.of();
        }
        visited.add(coord);

        if (y == map.length - 1) {
            return Set.of();
        }

        char c = map[y][x];

        if (c == '.') {
            return part1(map, x, y+1, visited);
        } else if (c == '^') {
            return Sets.union(
                    Set.of(coord),
                    Sets.union(
                            part1(map, x-1, y+1, visited),
                            part1(map, x+1, y+1, visited)));
        }
        throw new IllegalStateException();
    }

    // Counts number of unique paths.
    static long part2(char[][] map, int x, int y) {
        Coord init = new Coord(x, y);

        record QueueItem(Coord from, Coord to) {}

        Queue<QueueItem> queue = new ArrayDeque<>();
        queue.add(new QueueItem(init, new Coord(x, y+1)));

        Map<Coord, Long> visited = new HashMap<>();
        visited.put(init, 1L);

        while (!queue.isEmpty()) {
            QueueItem item = queue.poll();

            if (item.to.y == map.length) {
                continue;
            }

            long weight = visited.get(item.from);

            if (visited.containsKey(item.to)) {
                // Someone else already visited this coord, add own weight to their weight and exit loop.
                visited.compute(item.to, (k, prev) -> prev + weight);
                continue;
            } else {
                // First one visiting this coord, put own weight into lookup table so that others can add to it later.
                visited.put(item.to, weight);
            }

            char c = map[item.to.y][item.to.x];
            if (c == '.') {
                queue.add(new QueueItem(item.to, new Coord(item.to.x, item.to.y+1)));
            } else if (c == '^') {
                queue.add(new QueueItem(item.to, new Coord(item.to.x-1, item.to.y+1)));
                queue.add(new QueueItem(item.to, new Coord(item.to.x+1, item.to.y+1)));
            } else {
                throw new IllegalStateException();
            }
        }

        System.out.println(visited);

        return EntryStream.of(visited)
                .filterKeys(coord -> coord.y == map.length - 1)
                .values()
                .mapToLong(l -> l)
                .sum();
    }
}
