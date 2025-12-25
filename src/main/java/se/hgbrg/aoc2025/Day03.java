package se.hgbrg.aoc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Day03 {
    public static void main(String[] args) throws IOException {
        List<List<Integer>> banks = Files.readAllLines(Paths.get("src/main/resources/day03/input.txt")).stream()
                .map(line -> Arrays.stream(line.split("")).mapToInt(Integer::parseInt).boxed().toList())
                .toList();

        long sum = 0;
        for (List<Integer> bank : banks) {
            String joltage = "";

            int startIndex = 0;
            for (int numDigitsLeft = 12; numDigitsLeft > 0; numDigitsLeft--) {
                int endIndex = bank.size() - numDigitsLeft + 1;
                List<Integer> sub = bank.subList(startIndex, endIndex);

                int max = 0;
                int maxIndex = 0;
                for (int j = 0; j < sub.size(); j++) {
                    int num = sub.get(j);
                    if (num > max) {
                        max = num;
                        maxIndex = j;
                    }
                }

                joltage += max;
                startIndex += maxIndex + 1;
            }

            System.out.println(bank);
            System.out.println(joltage);

            sum += Long.parseLong(joltage);
        }

        System.out.println(sum);
    }
}
