package se.hgbrg.aoc2025;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day05 {
    static Pattern pattern = Pattern.compile("^(\\d+)-(\\d+)$");

    record Range(long min, long max) {
        boolean contains(long num) {
            return min <= num && num <= max;
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day05/input.txt"));

        Set<Range> ranges = lines.stream()
                .takeWhile(line -> !line.isEmpty())
                .map(line -> {
                    Matcher matcher = pattern.matcher(line);
                    Preconditions.checkArgument(matcher.matches());
                    long min = Long.parseLong(matcher.group(1));
                    long max = Long.parseLong(matcher.group(2));
                    return new Range(min, max);
                })
                .collect(Collectors.toSet());

        System.out.println(ranges);

        List<Long> ingredients = lines.stream()
                .dropWhile(line -> !line.isEmpty())
                .skip(1)
                .map(Long::parseLong)
                .toList();

        System.out.println(ingredients);

        List<Long> freshIngredients = ingredients.stream()
                .filter(i -> ranges.stream().anyMatch(range -> range.contains(i)))
                .toList();

        System.out.println(freshIngredients);
        System.out.println(freshIngredients.size());

        Set<Range> nonOverlappingRanges = mergeRanges(ranges);
        System.out.println(nonOverlappingRanges);

        long sum = nonOverlappingRanges.stream()
                .mapToLong(range -> range.max() - range.min() + 1)
                .sum();
        System.out.println(sum);
    }

    static Set<Range> mergeRanges(Set<Range> ranges) {
        Set<Range> mutable = new HashSet<>(ranges);

        for (Range range1 : ranges) {
            for (Range range2 : ranges) {
                if (range1 == range2) {
                    continue;
                }
                if (range1.contains(range2.min()) || range1.contains(range2.max())) {
                    Range merged = new Range(
                            Math.min(range1.min(), range2.min()),
                            Math.max(range1.max(), range2.max()));
                    mutable.remove(range1);
                    mutable.remove(range2);
                    mutable.add(merged);
                } else if (range1.contains(range2.min()) && range1.contains(range2.max())) {
                    mutable.remove(range2);
                }
            }
        }

        if (mutable.size() != ranges.size()) {
            return mergeRanges(mutable);
        }

        return mutable;
    }
}
