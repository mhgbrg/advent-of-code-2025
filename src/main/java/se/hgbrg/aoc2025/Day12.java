package se.hgbrg.aoc2025;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class Day12 {
    static boolean DEBUG = false;
    static boolean REAL_INPUT = true;

    record Instruction(int width, int height, List<Integer> requirements) {}

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day12/%s.txt".formatted(REAL_INPUT ? "input" : "example")));

        List<Instruction> instructions = lines.stream()
                .map(line -> {
                    String[] parts = line.split(": ");
                    String[] dimensions = parts[0].split("x");
                    String[] requirements = parts[1].split(" ");
                    return new Instruction(
                            Integer.parseInt(dimensions[0]),
                            Integer.parseInt(dimensions[1]),
                            Arrays.stream(requirements)
                                    .map(Integer::parseInt)
                                    .toList());
                })
                .toList();

        List<boolean[][]> pieces = new ArrayList<>();

         if (REAL_INPUT) {
            pieces.add(parse("""
                    #.#
                    ###
                    #.#
                    """));
            pieces.add(parse("""
                    ###
                    ##.
                    .##
                    """));
            pieces.add(parse("""
                    ###
                    ..#
                    ###
                    """));
            pieces.add(parse("""
                    .##
                    .##
                    ###
                    """));
            pieces.add(parse("""
                    ..#
                    .##
                    ##.
                    """));
            pieces.add(parse("""
                    ###
                    ##.
                    #..
                    """));
        } else {
            pieces.add(parse("""
                    ###
                    ##.
                    ##.
                    """));
            pieces.add(parse("""
                    ###
                    ##.
                    .##
                    """));
            pieces.add(parse("""
                    .##
                    ###
                    ##.
                    """));
            pieces.add(parse("""
                    ##.
                    ###
                    ##.
                    """));
            pieces.add(parse("""
                    ###
                    #..
                    ###
                    """));
            pieces.add(parse("""
                    ###
                    .#.
                    ###
                    """));
        }

        long answer;

        if (REAL_INPUT) {
            // Simple solution...
            answer = instructions.stream()
                    .filter(instruction -> {
                        int totalCount = IntStream.range(0, pieces.size())
                                .map(i -> instruction.requirements.get(i) * count(pieces.get(i)))
                                .sum();
                        return totalCount <= instruction.width * instruction.height;
                    })
                    .count();
        } else {
            // Hard solution...
            List<List<boolean[][]>> allPieces = pieces.stream()
                    .map(Day12::variants)
                    .toList();
            answer = instructions.stream()
                    .peek(System.out::println)
                    .filter(instruction -> {
                        boolean isSolvable = new Solver(instruction.width, instruction.height, allPieces).isSolvable(
                                instruction.requirements.get(0),
                                instruction.requirements.get(1),
                                instruction.requirements.get(2),
                                instruction.requirements.get(3),
                                instruction.requirements.get(4),
                                instruction.requirements.get(5));
                        System.out.println(isSolvable);
                        return isSolvable;
                    })
                    .count();
        }

        System.out.println(answer);
    }

    static class State {
        final boolean[][] board;

        State(boolean[][] board) {
            this.board = board;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof State other)) return false;
            return Arrays.deepEquals(this.board, other.board);
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(board);
        }
    }

    static class Solver {
        final boolean[][] board;
        final List<List<boolean[][]>> allPieces;
        final Set<State> seen;

        Solver(int width, int height, List<List<boolean[][]>> allPieces) {
            this.board = new boolean[height][width];
            this.allPieces = allPieces;
            this.seen = new HashSet<>();
        }

        boolean isSolvable(int r0, int r1, int r2, int r3, int r4, int r5) {
            State state = new State(board);
            if (seen.contains(state)) {
                return false;
            }
            seen.add(state);

            if (r0 == 0 && r1 == 0 && r2 == 0 && r3 == 0 && r4 == 0 && r5 == 0) {
                print(board);
                return true;
            }

            int i;
            if (r0 > 0) {
                i = 0;
                r0--;
            } else if (r1 > 0) {
                i = 1;
                r1--;
            } else if (r2 > 0) {
                i = 2;
                r2--;
            } else if (r3 > 0) {
                i = 3;
                r3--;
            } else if (r4 > 0) {
                i = 4;
                r4--;
            } else if (r5 > 0) {
                i = 5;
                r5--;
            } else {
                throw new IllegalArgumentException();
            }

            List<boolean[][]> pieces = allPieces.get(i);
            for (boolean[][] piece : pieces) {
                for (int y = 0; y < board.length - 2; y++) {
                    for (int x = 0; x < board[y].length - 2; x++) {
                        if (!canPlace(board, piece, x, y)) {
                            continue;
                        }
                        place(board, piece, x, y);
                        if (DEBUG) {
                            print(board);
                            System.out.println();
                        }
                        boolean isSolvable = isSolvable(r0, r1, r2, r3, r4, r5);
                        if (isSolvable) {
                            return true;
                        }
                        undo(board, piece, x, y);
                    }
                }
            }

            return false;
        }
    }

    static boolean canPlace(boolean[][] board, boolean[][] piece, int x, int y) {
        for (int py = 0; py < 3; py++) {
            for (int px = 0; px < 3; px++) {
                if (!piece[py][px]) {
                    continue;
                }
                if (board[y+py][x+px]) {
                    return false;
                }
            }
        }
        return true;
    }

    static void place(boolean[][] board, boolean[][] piece, int x, int y) {
        for (int py = 0; py < 3; py++) {
            for (int px = 0; px < 3; px++) {
                if (!piece[py][px]) {
                    continue;
                }
                board[y+py][x+px] = true;
            }
        }
    }

    static void undo(boolean[][] board, boolean[][] piece, int x, int y) {
        for (int py = 0; py < 3; py++) {
            for (int px = 0; px < 3; px++) {
                if (!piece[py][px]) {
                    continue;
                }
                board[y+py][x+px] = false;
            }
        }
    }

    static boolean[][] parse(String piece) {
        char[][] rows = Arrays.stream(piece.split("\\n"))
                .map(String::toCharArray)
                .toArray(char[][]::new);
        int n = rows.length;
        boolean[][] parsed = new boolean[n][n];
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                parsed[y][x] = rows[y][x] == '#';
            }
        }
        return parsed;
    }

    static int count(boolean[][] matrix) {
        int count = 0;
        for (boolean[] row : matrix) {
            for (boolean cell : row) {
                if (cell) {
                    count++;
                }
            }
        }
        return count;
    }

    static boolean[][] rotate(boolean[][] matrix) {
        int n = matrix.length;
        boolean[][] rotated = new boolean[n][n];
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                rotated[y][x] = matrix[n - x - 1][y];
            }
        }
        return rotated;
    }

    static boolean[][] flip(boolean[][] matrix) {
        int n = matrix.length;
        boolean[][] flipped = new boolean[n][n];
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                flipped[y][x] = matrix[x][y];
            }
        }
        return flipped;
    }

    static List<boolean[][]> variants(boolean[][] piece) {
        return ImmutableList.<boolean[][]>builder()
                .addAll(rotations(piece))
                .addAll(rotations(flip(piece)))
                .build();
    }

    static List<boolean[][]> rotations(boolean[][] piece) {
        boolean[][] rotate90 = rotate(piece);
        boolean[][] rotate180 = rotate(rotate90);
        boolean[][] rotate270 = rotate(rotate180);
        return java.util.List.of(
                piece,
                rotate90,
                rotate180,
                rotate270);
    }

    static void print(boolean[][] matrix) {
        for (boolean[] row : matrix) {
            for (boolean cell : row) {
                System.out.print(cell ? "#" : ".");
            }
            System.out.println();
        }
    }
}
