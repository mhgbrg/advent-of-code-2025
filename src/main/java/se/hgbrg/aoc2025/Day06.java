package se.hgbrg.aoc2025;

import com.google.common.collect.Iterables;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day06 {
    public static void main(String[] args) throws IOException {
        System.out.println("Part 1");
        part1();
        System.out.println("\nPart 2");
        part2();
    }

    static void part1() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day06/input.txt"));

        List<List<Long>> numbers = new ArrayList<>();
        for (int i = 0; i < lines.size() - 1; i++) {
            String line = lines.get(i);
            List<Long> parsed = Arrays.stream(line.split(" "))
                    .filter(s -> !s.isBlank())
                    .map(Long::parseLong)
                    .toList();
            numbers.add(parsed);
        }

        List<String> operations = Arrays.stream(Iterables.getLast(lines).split(" "))
                .filter(s -> !s.isBlank())
                .toList();

        System.out.println(numbers);
        System.out.println(operations);

        List<List<Long>> transposed = transpose(numbers);

        System.out.println(transposed);

        List<Long> answers = new ArrayList<>();
        for (int i = 0; i < transposed.size(); i++) {
            List<Long> row = transposed.get(i);
            String operation = operations.get(i);
            long identity = operation.equals("+") ? 0 : 1;
            long answer = row.stream()
                    .reduce(identity, (l, r) -> {
                        if (operation.equals("+")) {
                            return l + r;
                        } else if (operation.equals("*")) {
                            return l * r;
                        }
                        throw new IllegalStateException();
                    });
            answers.add(answer);
        }

        System.out.println(answers);

        long finalAnswer = answers.stream().mapToLong(i -> i).sum();

        System.out.println(finalAnswer);
    }

    static void part2() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day06/input.txt"));

        List<Long> answers = new ArrayList<>();

        List<Long> numbers = new ArrayList<>();
        for (int x = lines.getFirst().length(); x >= 0; x--) {
            String digits = "";
            for (int y = 0; y < lines.size(); y++) {
                char c = get(lines, x, y);;
                if (c != ' ') {
                    digits += c;
                }
            }
            if (digits.isBlank()) {
                // Blank column, do nothing.
            } else if (digits.endsWith("+") || digits.endsWith("*")) {
                // Entire problem is parsed, time to do some computation.
                char operation = digits.charAt(digits.length() - 1);
                long number = Long.parseLong(digits.substring(0, digits.length() - 1));
                numbers.add(number);
                long identity = operation == '+' ? 0 : 1;
                long answer = numbers.stream()
                        .reduce(identity, (l, r) -> {
                            if (operation == '+') {
                                return l + r;
                            } else if (operation == '*') {
                                return l * r;
                            }
                            throw new IllegalStateException();
                        });
                answers.add(answer);
                System.out.println(digits);
                System.out.println("=" + answer);
                // Reset numbers for next problem.
                numbers = new ArrayList<>();
            } else {
                long number = Long.parseLong(digits);
                numbers.add(number);
                System.out.println(digits);
            }
        }

        System.out.println(answers);

        long finalAnswer = answers.stream().mapToLong(i -> i).sum();

        System.out.println(finalAnswer);
    }

    static char get(List<String> lines, int x, int y) {
        if (y >= lines.size() || x >= lines.get(y).length()) {
            return ' ';
        }
        return lines.get(y).charAt(x);
    }

    static List<List<Long>> transpose(List<List<Long>> matrix) {
        List<List<Long>> transposed = new ArrayList<>();
        for (int j = 0; j < matrix.get(0).size(); j++) {
            List<Long> row = new ArrayList<>();
            for (int i = 0; i < matrix.size(); i++) {
                row.add(matrix.get(i).get(j));
            }
            transposed.add(row);
        }
        return transposed;
    }
}
