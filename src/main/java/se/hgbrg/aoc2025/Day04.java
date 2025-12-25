package se.hgbrg.aoc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Day04 {
    public static void main(String[] args) throws IOException {
        char[][] map = Files.readAllLines(Paths.get("src/main/resources/day04/input.txt")).stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);

        int sum = 0;
        boolean removed;
        do {
            removed = false;
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    char c = get(map, i, j);
                    if (c == '.') {
                        continue;
                    }
                    int adjacent =
                            toInt(get(map, i - 1, j - 1))
                                    + toInt(get(map, i - 1, j))
                                    + toInt(get(map, i - 1, j + 1))
                                    + toInt(get(map, i, j - 1))
                                    + toInt(get(map, i, j + 1))
                                    + toInt(get(map, i + 1, j - 1))
                                    + toInt(get(map, i + 1, j))
                                    + toInt(get(map, i + 1, j + 1));
                    if (adjacent >= 4) {
                        continue;
                    }
                    sum++;
                    map[i][j] = '.';
                    removed = true;
                }
            }
        } while (removed);

        System.out.println(sum);
    }

    static char get(char[][] map, int i, int j) {
        if (i < 0 || i > map.length - 1 || j < 0 || j > map[i].length - 1) {
            return '.';
        }
        return map[i][j];
    }

    static int toInt(char c) {
        return c == '@' ? 1 : 0;
    }
}
