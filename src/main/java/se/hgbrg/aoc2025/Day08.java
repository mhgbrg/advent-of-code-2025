package se.hgbrg.aoc2025;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Day08 {
    record Coord(int x, int y, int z) {}

    static int iterations = 1000;

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day08/input.txt"));

        Set<Coord> coords = lines.stream()
                .map(line -> {
                    String[] parts = line.split(",");
                    return new Coord(
                            Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2])
                    );
                })
                .collect(Collectors.toSet());

        part1(coords);
        part2(coords);
    }

    static void part1(Set<Coord> coords) {
        Graph<Coord, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        coords.forEach(graph::addVertex);

        List<Set<Coord>> sortedPairs = Sets.combinations(graph.vertexSet(), 2).stream()
                .sorted(Comparator.comparingInt(pair -> {
                    Coord c1 = Iterables.get(pair, 0);
                    Coord c2 = Iterables.get(pair, 1);
                    return distance(c1, c2);
                }))
                .collect(Collectors.toCollection(LinkedList::new));

        for (int i = 0; i < iterations; i++) {
            Coord c1;
            Coord c2;
            do {
                Set<Coord> pair = sortedPairs.removeFirst();
                c1 = Iterables.get(pair, 0);
                c2 = Iterables.get(pair, 1);
            } while (graph.containsEdge(c1, c2));
            graph.addEdge(c1, c2);
        }

        Set<Set<Coord>> subgraphs = subgraphs(graph);

        System.out.println(subgraphs.size());

        int answer = subgraphs.stream()
                .map(Set::size)
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .mapToInt(i -> i)
                .reduce(1, (l, r) -> l * r);

        System.out.println(answer);
    }

    static void part2(Set<Coord> coords) {
        Graph<Coord, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        coords.forEach(graph::addVertex);

        List<Set<Coord>> sortedPairs = Sets.combinations(graph.vertexSet(), 2).stream()
                .sorted(Comparator.comparingInt(pair -> {
                    Coord c1 = Iterables.get(pair, 0);
                    Coord c2 = Iterables.get(pair, 1);
                    return distance(c1, c2);
                }))
                .collect(Collectors.toCollection(LinkedList::new));

        Coord c1 = null;
        Coord c2 = null;
        while (subgraphs(graph).size() != 1) {
            do {
                Set<Coord> pair = sortedPairs.removeFirst();
                c1 = Iterables.get(pair, 0);
                c2 = Iterables.get(pair, 1);
            } while (graph.containsEdge(c1, c2));
            graph.addEdge(c1, c2);
        }

        System.out.println(c1);
        System.out.println(c2);

        long answer = (long) c1.x * (long) c2.x;

        System.out.println(answer);
    }

    static int distance(Coord c1, Coord c2) {
        return (int) Math.sqrt(
            Math.pow(c1.x - c2.x, 2)
                + Math.pow(c1.y - c2.y, 2)
                + Math.pow(c1.z - c2.z, 2));
    }

    static Set<Set<Coord>> subgraphs(Graph<Coord, DefaultEdge> graph) {
        Set<Set<Coord>> subgraphs = new HashSet<>();
        Set<Coord> visited = new HashSet<>();
        for (Coord coord : graph.vertexSet()) {
            if (visited.contains(coord)) {
                continue;
            }
            Set<Coord> subgraph = new HashSet<>();
            dfs(graph, coord, subgraph);
            subgraphs.add(subgraph);
            visited.addAll(subgraph);
        }
        return subgraphs;
    }

    static void dfs(Graph<Coord, DefaultEdge> graph, Coord coord, Set<Coord> visited) {
        if (visited.contains(coord)) {
            return;
        }
        visited.add(coord);
        Set<Coord> neighbors = Graphs.neighborSetOf(graph, coord);
        for (Coord neighbor : neighbors) {
            dfs(graph, neighbor, visited);
        }
    }
}
