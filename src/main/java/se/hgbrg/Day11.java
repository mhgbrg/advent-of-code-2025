package se.hgbrg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day11 {
    record Instruction(String device, List<String> outputs) {}

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day11/input.txt"));

        List<Instruction> instructions = lines.stream()
                .map(line -> {
                    String[] parts = line.split(": ");
                    String device = parts[0];
                    List<String> outputs = Arrays.asList(parts[1].split(" "));
                    return new Instruction(device, outputs);
                })
                .toList();

        System.out.println(instructions);

        Map<String, List<String>> graph = instructions.stream()
                .collect(Collectors.toMap(Instruction::device, Instruction::outputs));

        System.out.println(graph);

        part1(graph);
        part2(graph);
    }

    static void part1(Map<String, List<String>> graph) {
        long answer = dfs(graph, "you", "out", new HashMap<>());
        System.out.println(answer);
    }

    static long dfs(Map<String, List<String>> graph, String node, String target, Map<String, Long> cache) {
        if (cache.containsKey(node)) {
            return cache.get(node);
        }
        if (node.equals(target)) {
            return 1;
        }
        if (!graph.containsKey(node)) {
            return 0;
        }
        List<String> outputs = graph.get(node);
        return outputs.stream()
                .mapToLong(next -> {
                    long num = dfs(graph, next, target, cache);
                    cache.put(next, num);
                    return num;
                })
                .sum();
    }

    static void part2(Map<String, List<String>> graph) {
        long svrDac = dfs(graph, "svr", "dac", new HashMap<>());
        System.out.println("svr -> dac: " + svrDac);
        long dacFtt = dfs(graph, "dac", "fft", new HashMap<>());
        System.out.println("dac -> fft: " + dacFtt);
        long fftOut = dfs(graph, "fft", "out", new HashMap<>());
        System.out.println("fft -> out: " + fftOut);

        long svrFft = dfs(graph, "svr", "fft", new HashMap<>());
        System.out.println("svr -> fft: " + svrFft);
        long fftDac = dfs(graph, "fft", "dac", new HashMap<>());
        System.out.println("fft -> dac: " + fftDac);
        long dacOut = dfs(graph, "dac", "out", new HashMap<>());
        System.out.println("dac -> out: " + dacOut);

        long answer = svrDac * dacFtt * fftOut + svrFft * fftDac * dacOut;

        System.out.println(answer);
    }
}
