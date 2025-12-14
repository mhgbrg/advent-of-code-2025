package se.hgbrg.day01;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day01 {
    record Instruction(Direction direction, int distance) {}

    enum Direction {
        LEFT,
        RIGHT,
    }

    static Pattern pattern = Pattern.compile("^([LR])(\\d+)$");

    public static void main(String[] args) throws IOException {
        List<Instruction> instructions = Files.readAllLines(Paths.get("src/main/resources/day01/input.txt")).stream()
                .filter(Predicate.not(String::isBlank))
                .map(line -> {
                    Matcher matcher = pattern.matcher(line);
                    Preconditions.checkArgument(matcher.matches());
                    Direction direction = matcher.group(1).equals("L") ? Direction.LEFT : Direction.RIGHT;
                    int distance = Integer.parseInt(matcher.group(2));
                    return new Instruction(direction, distance);
                })
                .toList();

        int current = 50;
        int zeroCount = 0;
        for (Instruction instruction : instructions) {
            System.out.println(instruction);
            int new_ = switch (instruction.direction()) {
                case LEFT -> current - instruction.distance();
                case RIGHT -> current + instruction.distance();
            };
            System.out.println("new: %s".formatted(new_));
            if (new_ > 0) {
                zeroCount += new_ / 100;
            } else if (new_ < 0) {
                if (current != 0) {
                    zeroCount++;
                }
                zeroCount += Math.abs(new_) / 100;
            } else {
                zeroCount++;
            }
            current = Math.floorMod(new_, 100);
            System.out.println("current: %s".formatted(current));
            System.out.println("zeroCount: %s".formatted(zeroCount));
        }
    }
}
