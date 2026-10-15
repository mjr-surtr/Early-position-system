import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Rebuilds the 32px item sprites without interpolation or external dependencies. */
public class PotionsV2 {
    private static final int SIZE = 32;
    private static final int INK = 0xFF282335;
    private static final int GOLD = 0xFFE2B865;
    private static final int LIGHT_GOLD = 0xFFFFE5A0;
    private static final int DARK_GOLD = 0xFF866044;
    private static final int WHITE = 0xFFF3F9EB;
    private static final String[] POTIONS = {
            "early_night_vision_potion", "early_healing_potion", "early_water_breathing_potion",
            "early_strength_potion", "early_mining_potion", "early_swiftness_potion",
            "early_leaping_potion", "early_slow_falling_potion", "early_invisibility_potion",
            "early_glowing_potion", "early_luck_potion", "alchemist_cleanser",
            "alchemist_secret_brew", "early_tab_icon"
    };
    private static final int[] LIQUIDS = {
            0xFF8157B6, 0xFFDA597C, 0xFF318EC1, 0xFFC95C3F, 0xFFC39439,
            0xFF48B8BC, 0xFF6CAB51, 0xFF92A8CB, 0xFF8E91AF, 0xFFE3B942,
            0xFF41A77A, 0xFF68BCAF, 0xFF9B4F99, 0xFF7960CA
    };
    // Small effect emblems remain legible when the inventory scales sprites down.
    private static final String[][] EMBLEMS = {
            {"..###..", ".#...#.", "#..#..#", ".#...#.", "..###.."},
            {".##.##.", "#######", "#######", ".#####.", "..###..", "...#..."},
            {"...#...", "..###..", ".#####.", ".#####.", "..###.."},
            {"...#...", "..##...", ".#####.", "...##..", "...#..."},
            {".#####.", "#..#..#", "...#...", "...#...", "..#....", "..#...."},
            {"...##..", "..##...", ".#####.", "...##..", "..##...", ".##...."},
            {".#...#.", ".#...#.", ".#####.", "#######", ".#####.", "..###.."},
            {".....#.", "...###.", "..###..", ".###...", "###....", ".#....."},
            {"..###..", ".#...#.", ".#.#.#.", ".#...#.", "..#.#.."},
            {"...#...", ".#.#.#.", "..###..", "#######", "..###..", ".#.#.#.", "...#..."},
            {".##.##.", ".##.##.", "...#...", ".##.##.", ".##.##.", "...#..."},
            {"...#...", "...#...", ".#####.", "...#...", "...#..."},
            {"...#...", "..###..", ".#.#.#.", "#######", ".#.#.#.", "..###..", "...#..."},
            {"...#...", "..###..", ".#####.", "#######", ".#####.", "..###..", "...#..."}
    };

    public static void main(String[] args) throws IOException {
        Path output = Path.of(args.length == 0
                ? "src/main/resources/assets/earlypotions/textures/item" : args[0]);
        if (!Files.isDirectory(output)) {
            throw new IOException("Item texture directory does not exist: " + output.toAbsolutePath());
        }
        for (int i = 0; i < POTIONS.length; i++) {
            save(output, POTIONS[i], potion(i));
        }
        save(output, "potion_core", core());
        save(output, "alchemist_helmet", goggles());
        save(output, "alchemist_chestplate", armor(0));
        save(output, "alchemist_leggings", armor(1));
        save(output, "alchemist_boots", armor(2));
        save(output, "alchemist_blade", blade());
        System.out.println("Generated 20 item textures in " + output.toAbsolutePath());
    }

    private static BufferedImage canvas() {
        return new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
    }

    private static void save(Path output, String name, BufferedImage image) throws IOException {
        if (!ImageIO.write(image, "png", output.resolve(name + ".png").toFile())) {
            throw new IOException("PNG writer is unavailable");
        }
    }

    private static void pixel(BufferedImage image, int x, int y, int color) {
        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE) {
            image.setRGB(x, y, color);
        }
    }

    private static void rect(BufferedImage image, int x, int y, int w, int h, int color) {
        for (int dy = 0; dy < h; dy++) {
            for (int dx = 0; dx < w; dx++) {
                pixel(image, x + dx, y + dy, color);
            }
        }
    }

    private static void polygon(BufferedImage image, int color, int... points) {
        Polygon polygon = new Polygon();
        for (int i = 0; i < points.length; i += 2) {
            polygon.addPoint(points[i], points[i + 1]);
        }
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setColor(new Color(color, true));
            graphics.fillPolygon(polygon);
        } finally {
            graphics.dispose();
        }
    }

    private static int tint(int color, double factor) {
        int r = Math.min(255, (int) (((color >> 16) & 255) * factor));
        int g = Math.min(255, (int) (((color >> 8) & 255) * factor));
        int b = Math.min(255, (int) ((color & 255) * factor));
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private static BufferedImage potion(int type) {
        BufferedImage image = canvas();
        // Stepped flask silhouette, with an opaque edge and translucent empty glass.
        int[] left = {12, 11, 11, 11, 11, 10, 10, 11, 11, 10, 8, 7, 6, 5, 5,
                4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 6, 7, 9, 11};
        int liquid = LIQUIDS[type];
        for (int y = 1; y <= 29; y++) {
            int l = left[y - 1];
            for (int x = l; x <= 31 - l; x++) {
                boolean edge = x == l || x == 31 - l || y == 29;
                int color;
                if (edge) {
                    color = INK;
                } else if (y < 5) {
                    color = x < 15 ? 0xFFD4AA72 : 0xFF947049;
                } else if (y == 5 || y == 7) {
                    color = x < 16 ? LIGHT_GOLD : DARK_GOLD;
                } else if (y == 6) {
                    color = GOLD;
                } else if (y < 15) {
                    color = x < 13 ? 0xB9B4DADE : 0x986B8FAD;
                } else {
                    double shade = y == 15 ? 1.37 : y > 25 ? 0.65 : x > 22 ? 0.72 : x < 10 ? 1.16 : 1.0;
                    color = tint(liquid, shade);
                }
                pixel(image, x, y, color);
            }
        }
        rect(image, 13, 2, 3, 1, 0xFFF0D09A);
        rect(image, 18, 3, 1, 2, 0xFF75523C);
        rect(image, 12, 8, 2, 3, 0xFFD1E9E5);
        rect(image, 9, 12, 2, 2, 0xFFE6F5EF);
        rect(image, 7, 15, 2, 7, 0xFFCCE9E7);
        rect(image, 8, 16, 1, 4, WHITE);
        rect(image, 8, 23, 2, 2, tint(liquid, 1.45));
        rect(image, 11, 27, 8, 1, tint(liquid, 1.22));
        rect(image, 24, 18, 1, 5, tint(liquid, 0.61));
        // A single emblem replaces scattered noise; its offset shadow adds depth.
        String[] emblem = EMBLEMS[type];
        stamp(image, 13, 18, emblem, tint(liquid, 0.48));
        stamp(image, 12, 17, emblem, type == 3 || type == 4 || type >= 12 ? LIGHT_GOLD : WHITE);
        if (type == 13) {
            rect(image, 15, 19, 2, 2, 0xFFA2ECE0);
            pixel(image, 14, 20, 0xFFEFA3BC);
        }
        pixel(image, 21, 12, 0xFFBCDAD8);
        pixel(image, 22, 14, 0xFFDBEEE1);
        return image;
    }

    private static void stamp(BufferedImage image, int x, int y, String[] rows, int color) {
        for (int dy = 0; dy < rows.length; dy++) {
            for (int dx = 0; dx < rows[dy].length(); dx++) {
                if (rows[dy].charAt(dx) == '#') {
                    pixel(image, x + dx, y + dy, color);
                }
            }
        }
    }

    /** Applies hard-edged facets inside a silhouette, preserving transparent cutouts. */
    private static void metal(BufferedImage image) {
        BufferedImage mask = canvas();
        mask.setData(image.getData());
        for (int y = 1; y < SIZE - 1; y++) {
            for (int x = 1; x < SIZE - 1; x++) {
                if (mask.getRGB(x, y) == 0) {
                    continue;
                }
                boolean edge = mask.getRGB(x - 1, y) == 0 || mask.getRGB(x + 1, y) == 0
                        || mask.getRGB(x, y - 1) == 0 || mask.getRGB(x, y + 1) == 0;
                int color = edge ? INK : (x < 14 ? 0xFF9B88C9 : 0xFF63517E);
                if (!edge && mask.getRGB(x - 2 < 0 ? 0 : x - 2, y) == 0) {
                    color = 0xFFD1BDF0;
                } else if (!edge && mask.getRGB(x, y + 2 >= SIZE ? SIZE - 1 : y + 2) == 0) {
                    color = 0xFF443A59;
                }
                pixel(image, x, y, color);
            }
        }
    }

    private static BufferedImage goggles() {
        BufferedImage image = canvas();
        rect(image, 2, 11, 28, 9, INK);
        rect(image, 3, 12, 26, 3, 0xFF65516D);
        rect(image, 3, 16, 26, 3, 0xFF423744);
        polygon(image, INK, 5, 8, 13, 8, 16, 11, 19, 8, 27, 8, 30, 12, 29, 21,
                25, 24, 19, 23, 16, 20, 13, 23, 6, 23, 3, 19, 3, 12);
        for (int offset : new int[]{0, 14}) {
            rect(image, 5 + offset, 10, 9, 11, DARK_GOLD);
            rect(image, 6 + offset, 9, 6, 1, LIGHT_GOLD);
            rect(image, 5 + offset, 10, 8, 2, GOLD);
            rect(image, 4 + offset, 12, 2, 7, GOLD);
            rect(image, 6 + offset, 12, 6, 7, 0xFF463C79);
            rect(image, 6 + offset, 12, 5, 3, 0xFF977DCD);
            rect(image, 7 + offset, 12, 2, 2, 0xFFE3DAFF);
            rect(image, 6 + offset, 16, 2, 2, 0xFF6FADD1);
            rect(image, 6 + offset, 20, 6, 1, GOLD);
        }
        rect(image, 14, 13, 4, 3, GOLD);
        rect(image, 14, 13, 4, 1, LIGHT_GOLD);
        return image;
    }

    private static BufferedImage armor(int type) {
        BufferedImage image = canvas();
        if (type == 0) {
            polygon(image, INK, 9, 4, 13, 4, 14, 7, 18, 7, 19, 4, 23, 4, 29, 9,
                    27, 16, 23, 14, 24, 28, 8, 28, 9, 14, 5, 16, 3, 9);
        } else if (type == 1) {
            rect(image, 7, 4, 18, 9, INK);
            rect(image, 7, 12, 8, 16, INK);
            rect(image, 17, 12, 8, 16, INK);
        } else {
            polygon(image, INK, 7, 7, 14, 7, 14, 27, 3, 27, 3, 22, 6, 20);
            polygon(image, INK, 19, 7, 26, 7, 26, 20, 29, 22, 29, 27, 18, 27);
        }
        metal(image);
        if (type == 0) {
            rect(image, 9, 8, 3, 3, GOLD);
            rect(image, 20, 8, 3, 3, GOLD);
            polygon(image, DARK_GOLD, 11, 11, 16, 14, 21, 11, 20, 16, 16, 19, 12, 16);
            polygon(image, LIGHT_GOLD, 12, 11, 16, 13, 20, 11, 16, 16);
            polygon(image, INK, 16, 14, 19, 17, 16, 21, 13, 17);
            polygon(image, 0xFF6DD7D0, 16, 15, 18, 17, 16, 19, 14, 17);
            pixel(image, 16, 16, WHITE);
            rect(image, 9, 24, 14, 2, GOLD);
            rect(image, 9, 24, 7, 1, LIGHT_GOLD);
        } else if (type == 1) {
            rect(image, 8, 6, 16, 2, GOLD);
            rect(image, 14, 5, 4, 4, DARK_GOLD);
            rect(image, 15, 6, 2, 2, 0xFF8EE1D4);
            rect(image, 9, 17, 4, 3, GOLD);
            rect(image, 19, 17, 4, 3, GOLD);
            rect(image, 9, 18, 3, 1, LIGHT_GOLD);
            rect(image, 19, 18, 3, 1, LIGHT_GOLD);
        } else {
            rect(image, 7, 9, 6, 2, GOLD);
            rect(image, 19, 9, 6, 2, GOLD);
            rect(image, 7, 14, 5, 2, DARK_GOLD);
            rect(image, 19, 14, 6, 2, DARK_GOLD);
            rect(image, 8, 14, 2, 2, LIGHT_GOLD);
            rect(image, 21, 14, 2, 2, LIGHT_GOLD);
            rect(image, 4, 25, 9, 1, 0xFFB7A0D6);
            rect(image, 19, 25, 9, 1, 0xFFB7A0D6);
        }
        return image;
    }

    private static BufferedImage core() {
        BufferedImage image = canvas();
        polygon(image, INK, 14, 2, 20, 3, 28, 12, 28, 20, 19, 29, 11, 29, 3, 20, 3, 12);
        polygon(image, GOLD, 14, 4, 19, 5, 26, 13, 26, 19, 18, 27, 12, 27, 5, 19, 5, 13);
        polygon(image, DARK_GOLD, 25, 13, 26, 19, 18, 27, 12, 27, 10, 25, 18, 24);
        polygon(image, INK, 15, 6, 23, 14, 23, 19, 16, 25, 8, 18, 8, 14);
        polygon(image, 0xFF9977D4, 15, 8, 21, 14, 21, 18, 16, 23, 10, 18, 10, 14);
        polygon(image, 0xFFE2C9FF, 15, 8, 16, 15, 10, 17, 10, 14);
        polygon(image, 0xFF5A3C96, 16, 15, 21, 14, 21, 18, 16, 23);
        polygon(image, 0xFFC798EE, 10, 18, 16, 15, 16, 23);
        rect(image, 14, 11, 2, 3, WHITE);
        pixel(image, 6, 13, LIGHT_GOLD);
        pixel(image, 19, 6, LIGHT_GOLD);
        return image;
    }

    private static BufferedImage blade() {
        BufferedImage image = canvas();
        // Diagonal blade, bright cutting edge on the upper-left, dark bevel below.
        polygon(image, INK, 28, 2, 30, 2, 29, 10, 14, 25, 7, 18, 22, 3);
        polygon(image, 0xFFD6CBF2, 27, 4, 28, 4, 26, 9, 13, 22, 10, 19);
        polygon(image, 0xFF9C80CE, 27, 5, 27, 9, 14, 22, 13, 20);
        polygon(image, 0xFF604584, 27, 9, 28, 7, 27, 11, 14, 24, 13, 22);
        polygon(image, WHITE, 24, 5, 27, 4, 12, 19, 11, 19);
        polygon(image, INK, 5, 16, 7, 15, 17, 25, 16, 28, 13, 27, 4, 19);
        polygon(image, GOLD, 6, 17, 8, 17, 16, 25, 15, 26, 13, 25, 5, 18);
        pixel(image, 7, 17, LIGHT_GOLD);
        rect(image, 10, 20, 3, 3, DARK_GOLD);
        pixel(image, 11, 20, 0xFF9AE9DE);
        polygon(image, INK, 7, 21, 11, 25, 6, 30, 2, 30, 2, 26);
        polygon(image, 0xFF71516D, 7, 23, 9, 25, 5, 29, 3, 27);
        pixel(image, 6, 25, GOLD);
        pixel(image, 5, 26, LIGHT_GOLD);
        rect(image, 2, 28, 3, 2, GOLD);
        pixel(image, 2, 28, LIGHT_GOLD);
        return image;
    }
}
