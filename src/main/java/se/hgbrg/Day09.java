package se.hgbrg;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Day09 {
    record Coord(int x, int y) {}

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day09/input.txt"));

        List<Coord> coords = lines.stream()
                .map(line -> {
                    String[] parts = line.split(",");
                    return new Coord(
                            Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1]));
                })
                .toList();

        part1(Set.copyOf(coords));
        part2(coords);
    }

    static void part1(Set<Coord> coords) {
        long max = Sets.combinations(coords, 2).stream()
                .mapToLong(pair -> {
                    Coord c1 = Iterables.get(pair, 0);
                    Coord c2 = Iterables.get(pair, 1);
                    return (1 + Math.abs((long) c1.x - c2.x)) * (1 + Math.abs((long) c1.y - c2.y));
                })
                .max()
                .orElseThrow();

        System.out.println(max);
    }

    static void part2(List<Coord> coords) {
        Path2D path = path(coords);
        Rectangle2D rect = rect(path, coords);

        System.out.println(((long) rect.getWidth() + 1) * ((long) rect.getHeight() + 1));

        JFrame frame = new JFrame("Advent of Code day 9");
        Panel panel = new Panel(100, coords);
        frame.add(panel);
        frame.setSize(1025, 1025);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
    }

    static Path2D path(List<Coord> coords) {
        Path2D path = new Path2D.Double();
        Coord first = coords.get(0);
        path.moveTo(first.x, first.y);
        for (int i = 1; i < coords.size(); i++) {
            Coord coord = coords.get(i);
            path.lineTo(coord.x, coord.y);
        }
        path.lineTo(first.x, first.y);
        path.closePath();
        return path;
    }

    static Rectangle2D rect(Path2D path, List<Coord> coords) {
        return Sets.combinations(Set.copyOf(coords), 2).stream()
                .map(pair -> {
                    Coord c1 = Iterables.get(pair, 0);
                    Coord c2 = Iterables.get(pair, 1);
                    return new Rectangle2D.Double(
                            Math.min(c1.x, c2.x),
                            Math.min(c1.y, c2.y),
                            Math.abs(c1.x - c2.x),
                            Math.abs(c1.y - c2.y));
                })
                .filter(path::contains)
                .max(Comparator.comparing(rect -> rect.width * rect.height))
                .orElseThrow();
    }

    static class Panel extends JPanel {
        List<Coord> coords;

        public Panel(int scale, List<Coord> coords) {
            super();
            this.coords = coords.stream()
                    .map(c -> new Coord(c.x / scale, c.y / scale))
                    .toList();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;

            Path2D path = path(coords);
            g2d.setColor(Color.GREEN);
            g2d.fill(path);

            Rectangle2D rect = rect(path, coords);
            g2d.setColor(Color.YELLOW);
            g2d.fill(rect);

            g2d.setColor(Color.RED);
            for (Coord coord : coords) {
                g2d.fillRect(coord.x - 2, coord.y - 2, 4, 4);
            }
        }
    }
}
