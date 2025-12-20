package se.hgbrg;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Day10 {
    static {
        Loader.loadNativeLibraries();
    }

    static Pattern pattern = Pattern.compile("^\\[(?<diagram>[\\.#]+)\\] (?<buttons>.*) \\{(?<joltages>(\\d,?)+)\\}");

    record Instruction(String diagram, List<List<Integer>> buttons, List<Integer> joltages) {}

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day10/input.txt"));

        List<Instruction> instructions = lines.stream()
                .map(line -> {
                    Matcher matcher = pattern.matcher(line);
                    Preconditions.checkArgument(matcher.matches());
                    String diagram = matcher.group("diagram");
                    List<List<Integer>> buttons = Arrays.stream(matcher.group("buttons").split(" "))
                            .map(button -> Arrays.stream(button.substring(1, button.length() - 1)
                                    .split(","))
                                    .map(Integer::parseInt)
                                    .toList())
                            .toList();
                    List<Integer> joltages = Arrays.stream(matcher.group("joltages").split(","))
                            .map(Integer::parseInt)
                            .toList();
                    return new Instruction(
                            diagram,
                            buttons,
                            joltages);
                })
                .toList();

        System.out.println(instructions);

        part1(instructions);
        part2(instructions);
    }

    static void part1(List<Instruction> instructions) {
        int answer = instructions.stream()
                .mapToInt(instruction -> {
                    String init = ".".repeat(instruction.diagram.length());
                    Set<String> visited = Sets.newHashSet(init);
                    Set<String> options = Sets.newHashSet(init);
                    int i = 1;
                    outer:
                    while (true) {
                        Set<String> newOptions = Sets.newHashSet();
                        for (String lights : options) {
                            for (List<Integer> button : instruction.buttons) {
                                String newLights = apply(lights, button);
                                if (newLights.equals(instruction.diagram)) {
                                    break outer;
                                }
                                if (!visited.contains(newLights)) {
                                    visited.add(newLights);
                                    newOptions.add(newLights);
                                }
                            }
                        }
                        options = newOptions;
                        i++;
                    }
                    return i;
                })
                .peek(System.out::println)
                .sum();
        System.out.println(answer);
    }

    static String apply(String lights, List<Integer> button) {
        char[] chars = lights.toCharArray();
        for (Integer i : button) {
            chars[i] = chars[i] == '.' ? '#' : '.';
        }
        return String.valueOf(chars);
    }

    static void part2(List<Instruction> instructions) {
        int answer = instructions.stream()
                .map(instruction -> {
                    MPSolver solver = MPSolver.createSolver("SAT");
                    MPObjective objective = solver.objective();
                    objective.setMinimization();
                    // Create one variable per button
                    for (int i = 0; i < instruction.buttons.size(); i++) {
                        MPVariable var = solver.makeIntVar(0, Double.POSITIVE_INFINITY, "x" + i);
                        objective.setCoefficient(var, 1);
                    }
                    // Create one constraint per light
                    for (int i = 0; i < instruction.joltages.size(); i++) {
                        int joltage = instruction.joltages.get(i);
                        MPConstraint constraint = solver.makeConstraint(joltage, joltage, "c" + i);
                        for (int j = 0; j < instruction.buttons.size(); j++) {
                            // Add coefficients for the buttons that affect the given light
                            Set<Integer> button = Set.copyOf(instruction.buttons.get(j));
                            if (button.contains(i)) {
                                MPVariable var = solver.variable(j);
                                constraint.setCoefficient(var, 1);
                            }
                        }
                    }
                    // Solve
                    MPSolver.ResultStatus result = solver.solve();
                    System.out.println("variables: " + solver.numVariables());
                    System.out.println("constraints: " + solver.numConstraints());
                    System.out.println("result: " + objective.value());
                    System.out.println(result);
                    return (int) objective.value();
                })
                .mapToInt(i -> i)
                .sum();

        System.out.println(answer);
    }
}
