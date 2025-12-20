package se.hgbrg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day02 {
    record Range(long min, long max) {
        boolean contains(long num) {
            return min <= num && num <= max;
        }
    }

    public static void main(String[] args) throws IOException {
        List<Range> ranges = Arrays.stream(Files.readString(Paths.get("src/main/resources/day02/input.txt")).trim().split(","))
                .map(range -> {
                    String[] parts = range.split("-");
                    return new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
                })
                .toList();

        long max = ranges.stream().mapToLong(Range::max).max().getAsLong();
        int maxLength = Long.toString(max).length();
        System.out.println("max: %s".formatted(max));

        long firstHalfOfMax = Long.parseLong(Long.toString(max).substring(0, Long.toString(max).length() / 2));
        System.out.println("firstHalfOfMax: %s".formatted(firstHalfOfMax));

        Set<Long> possibleInvalidIds = new HashSet<>();
        for (long i = 1; i <= firstHalfOfMax; i++) {
            String num = Long.toString(i);
            while (true) {
                num += Long.toString(i);
                if (num.length() > maxLength) {
                    break;
                }
                possibleInvalidIds.add(Long.parseLong(num));
            }
        }
        System.out.println("possibleInvalidIds: %s".formatted(possibleInvalidIds.stream().limit(100).toList()));

        Set<Long> invalidIds = new HashSet<>();
        for (long num : possibleInvalidIds) {
            for (Range range : ranges) {
                if (range.contains(num)) {
                    invalidIds.add(num);
                }
            }
        }
        System.out.println("invalidIds: %s".formatted(invalidIds.size()));

        long sum = invalidIds.stream().mapToLong(num -> num).sum();
        System.out.println("sum: %s".formatted(sum));
    }
}
