import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 程序化生成全部物品图标、盔甲层与炼药台方块贴图（32px 像素风，无外部依赖）。
 * 用法：java PotionsV2.java [textures 目录]
 */
public class PotionsV2 {
    private static final int SIZE = 32;

    private static final int INK = 0xFF1B1726;
    private static final int GOLD = 0xFFE2B865;
    private static final int LIGHT_GOLD = 0xFFFFE6A8;
    private static final int DARK_GOLD = 0xFF8A6240;
    private static final int WHITE = 0xFFF3F9EB;
    private static final int TEAL = 0xFF6DD7D0;

    /** 第一套：紫水晶 + 暖金 */
    private static final Palette ALCHEMIST = new Palette(
            0xFF7C63A8, 0xFFB6A3DA, 0xFF4E3B72, 0xFF3A2C55, GOLD, TEAL);

    /** 第二套：深紫黑 + 亮金 + 青绿宝石 */
    private static final Palette MASTER = new Palette(
            0xFF43325F, 0xFF715C97, 0xFF2B2142, 0xFF191227, LIGHT_GOLD, 0xFF5FE0D0);

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
    /** 小型效果徽记，物品栏缩放后仍可辨认 */
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
        Path textures = Path.of(args.length == 0
                ? "src/main/resources/assets/earlypotions/textures" : args[0]);
        Path items = textures.resolve("item");
        Path armor = textures.resolve("models").resolve("armor");
        Path block = textures.resolve("block");
        Files.createDirectories(items);
        Files.createDirectories(armor);
        Files.createDirectories(block);

        for (int i = 0; i < POTIONS.length; i++) {
            save(items, POTIONS[i], potion(i));
        }
        save(items, "potion_core", core(ALCHEMIST));
        save(items, "alchemist_helmet", helmetIcon(ALCHEMIST, false));
        save(items, "alchemist_chestplate", chestIcon(ALCHEMIST));
        save(items, "alchemist_leggings", legsIcon(ALCHEMIST));
        save(items, "alchemist_boots", bootsIcon(ALCHEMIST));
        save(items, "alchemist_blade", bladeIcon(ALCHEMIST));

        save(items, "master_alchemist_helmet", helmetIcon(MASTER, true));
        save(items, "master_alchemist_chestplate", chestIcon(MASTER));
        save(items, "master_alchemist_leggings", legsIcon(MASTER));
        save(items, "master_alchemist_boots", bootsIcon(MASTER));

        save(armor, "alchemist_layer_1", layer1(ALCHEMIST, false));
        save(armor, "alchemist_layer_2", layer2(ALCHEMIST, false));
        save(armor, "alchemist_master_layer_1", layer1(MASTER, true));
        save(armor, "alchemist_master_layer_2", layer2(MASTER, true));

        save(block, "early_brewing_stand", brewingStand());
        System.out.println("Generated textures in " + textures.toAbsolutePath());
    }

    private record Palette(int base, int light, int dark, int shadow, int trim, int gem) {
    }

    private static BufferedImage canvas() {
        return new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
    }

    private static BufferedImage armorCanvas() {
        return new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);
    }

    private static void save(Path dir, String name, BufferedImage image) throws IOException {
        if (!ImageIO.write(image, "png", dir.resolve(name + ".png").toFile())) {
            throw new IOException("PNG writer is unavailable");
        }
    }

    private static void pixel(BufferedImage image, int x, int y, int color) {
        if (x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight()) {
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

    private static void line(BufferedImage image, int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        while (true) {
            pixel(image, x0, y0, color);
            if (x0 == x1 && y0 == y1) {
                return;
            }
            int e2 = err * 2;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
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

    /** 为已绘制的不透明区域补 1px 深色描边 */
    private static void outline(BufferedImage image) {
        BufferedImage mask = copy(image);
        int w = image.getWidth();
        int h = image.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (mask.getRGB(x, y) == 0) {
                    continue;
                }
                boolean nearEmpty = false;
                for (int[] d : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                    int nx = x + d[0];
                    int ny = y + d[1];
                    if (nx < 0 || ny < 0 || nx >= w || ny >= h || mask.getRGB(nx, ny) == 0) {
                        nearEmpty = true;
                        break;
                    }
                }
                if (nearEmpty) {
                    image.setRGB(x, y, INK);
                }
            }
        }
    }

    private static BufferedImage copy(BufferedImage source) {
        BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        copy.setData(source.getData());
        return copy;
    }

    private static int tint(int color, double factor) {
        int r = Math.min(255, (int) (((color >> 16) & 255) * factor));
        int g = Math.min(255, (int) (((color >> 8) & 255) * factor));
        int b = Math.min(255, (int) ((color & 255) * factor));
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private static int mix(int a, int b, double t) {
        int r = (int) (((a >> 16) & 255) * (1 - t) + ((b >> 16) & 255) * t);
        int g = (int) (((a >> 8) & 255) * (1 - t) + ((b >> 8) & 255) * t);
        int bl = (int) ((a & 255) * (1 - t) + (b & 255) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | bl;
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

    // ==================== 药水 ====================

    private static BufferedImage potion(int type) {
        BufferedImage image = canvas();
        int liquid = LIQUIDS[type];
        // 每一行的左边界（y = 1..29），构成圆肩玻璃瓶
        int[] left = {12, 12, 12, 10, 10, 12, 12, 12, 12, 9, 8, 7, 7, 6,
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 7, 8, 9};

        for (int i = 0; i < left.length; i++) {
            int y = i + 1;
            int l = left[i];
            int r = 31 - l;
            for (int x = l; x <= r; x++) {
                boolean edge = x == l || x == r || y == 29;
                int color;
                if (edge) {
                    color = INK;
                } else if (y <= 3) {
                    color = y == 1 ? 0xFFD8AC72 : x < 16 ? 0xFFB0814F : 0xFF7A5636;
                } else if (y <= 5) {
                    color = y == 4 ? LIGHT_GOLD : GOLD;
                    if (x == l + 1 || x == r - 1) {
                        color = DARK_GOLD;
                    }
                } else if (y <= 8) {
                    color = x < 15 ? 0xFFBFD4E2 : 0xFF8FA6B8;
                } else if (y == 9) {
                    color = GOLD;
                } else if (y < 15) {
                    color = x < 13 ? 0xCCBBD2E2 : 0xB08FA6B8;
                } else {
                    double shade = 1.0;
                    if (y == 15) {
                        shade = 1.35;
                    } else if (y > 25) {
                        shade = 0.72;
                    } else if (x > 22) {
                        shade = 0.78;
                    } else if (x < 10) {
                        shade = 1.15;
                    }
                    color = tint(liquid, shade);
                }
                pixel(image, x, y, color);
            }
        }

        // 玻璃高光与瓶底阴影
        rect(image, 8, 16, 2, 8, 0xFFE4F1F2);
        rect(image, 8, 16, 1, 4, WHITE);
        pixel(image, 23, 12, 0xFFD8E6EE);
        pixel(image, 24, 14, 0xFFBCCBD8);
        rect(image, 11, 27, 9, 1, tint(liquid, 1.2));
        rect(image, 23, 20, 1, 5, tint(liquid, 0.6));
        // 液面高光
        rect(image, 11, 15, 7, 1, tint(liquid, 1.5));

        String[] emblem = EMBLEMS[type];
        int ex = 16 - emblem[0].length() / 2;
        int ey = 21 - emblem.length / 2;
        stamp(image, ex + 1, ey + 1, emblem, tint(liquid, 0.45));
        int emblemColor = (type == 3 || type == 4 || type == 12) ? LIGHT_GOLD : WHITE;
        stamp(image, ex, ey, emblem, emblemColor);

        if (type == 13) {
            rect(image, 15, 19, 2, 2, 0xFFA2ECE0);
            pixel(image, 14, 20, 0xFFEFA3BC);
        }
        return image;
    }

    // ==================== 物品图标 ====================

    private static BufferedImage core(Palette p) {
        BufferedImage image = canvas();
        polygon(image, INK, 14, 2, 20, 3, 28, 12, 28, 20, 19, 29, 11, 29, 3, 20, 3, 12);
        polygon(image, p.trim(), 14, 4, 19, 5, 26, 13, 26, 19, 18, 27, 12, 27, 5, 19, 5, 13);
        polygon(image, DARK_GOLD, 25, 13, 26, 19, 18, 27, 12, 27, 10, 25, 18, 24);
        polygon(image, INK, 15, 6, 23, 14, 23, 19, 16, 25, 8, 18, 8, 14);
        polygon(image, p.base(), 15, 8, 21, 14, 21, 18, 16, 23, 10, 18, 10, 14);
        polygon(image, p.light(), 15, 8, 16, 15, 10, 17, 10, 14);
        polygon(image, p.dark(), 16, 15, 21, 14, 21, 18, 16, 23);
        polygon(image, mix(p.base(), p.gem(), 0.45), 10, 18, 16, 15, 16, 23);
        rect(image, 14, 11, 2, 3, WHITE);
        pixel(image, 6, 13, LIGHT_GOLD);
        pixel(image, 19, 6, LIGHT_GOLD);
        return image;
    }

    private static BufferedImage helmetIcon(Palette p, boolean master) {
        BufferedImage image = canvas();
        if (master) {
            // 宗师头盔：穹顶 + 护颊 + 额心宝石 + 双侧小角
            polygon(image, p.base(), 8, 8, 24, 8, 27, 13, 25, 24, 7, 24, 5, 13);
            rect(image, 8, 6, 16, 4, p.light());
            rect(image, 8, 6, 16, 1, p.trim());
            rect(image, 11, 4, 10, 2, p.trim());
            rect(image, 13, 3, 6, 1, LIGHT_GOLD);
            // 面甲开口
            rect(image, 10, 13, 12, 7, p.shadow());
            rect(image, 11, 14, 10, 5, p.dark());
            rect(image, 12, 15, 3, 2, mix(p.dark(), p.gem(), 0.7));
            rect(image, 17, 15, 3, 2, mix(p.dark(), p.gem(), 0.7));
            pixel(image, 12, 15, WHITE);
            pixel(image, 17, 15, WHITE);
            rect(image, 9, 11, 14, 2, p.trim());
            // 额心宝石
            rect(image, 15, 8, 2, 3, p.gem());
            pixel(image, 15, 8, WHITE);
            // 双侧犄角
            rect(image, 5, 9, 3, 3, p.trim());
            rect(image, 24, 9, 3, 3, p.trim());
            rect(image, 6, 8, 1, 1, LIGHT_GOLD);
            rect(image, 25, 8, 1, 1, LIGHT_GOLD);
            // 下颌与颈甲
            rect(image, 9, 21, 14, 3, p.base());
            rect(image, 10, 22, 12, 1, p.dark());
            rect(image, 7, 24, 18, 2, p.trim());
            rect(image, 8, 26, 16, 1, p.shadow());
        } else {
            // 炼金护目镜：宽镜框 + 皮革头带
            rect(image, 4, 12, 24, 8, p.base());
            rect(image, 4, 12, 24, 2, p.light());
            rect(image, 4, 19, 24, 1, p.dark());
            // 头带两侧
            rect(image, 2, 13, 2, 6, p.shadow());
            rect(image, 28, 13, 2, 6, p.shadow());
            // 双镜片
            for (int offset : new int[]{0, 14}) {
                rect(image, 6 + offset, 10, 8, 10, p.shadow());
                rect(image, 6 + offset, 10, 8, 2, p.trim());
                rect(image, 8 + offset, 12, 4, 5, p.dark());
                rect(image, 8 + offset, 12, 3, 2, mix(p.dark(), p.gem(), 0.6));
                pixel(image, 9 + offset, 13, WHITE);
                pixel(image, 10 + offset, 16, mix(p.dark(), p.gem(), 0.35));
            }
            // 鼻梁
            rect(image, 14, 13, 4, 4, p.trim());
            rect(image, 15, 13, 2, 1, LIGHT_GOLD);
            // 下颌带与金边
            rect(image, 4, 20, 24, 2, p.trim());
            rect(image, 5, 22, 22, 1, p.shadow());
        }
        outline(image);
        return image;
    }

    private static BufferedImage chestIcon(Palette p) {
        BufferedImage image = canvas();
        // 肩甲（左右）
        polygon(image, p.light(), 5, 10, 11, 7, 14, 9, 13, 13, 7, 14);
        polygon(image, p.light(), 21, 7, 27, 10, 25, 14, 18, 13, 17, 9);
        // 躯干主体（收腰）
        polygon(image, p.base(), 9, 8, 23, 8, 25, 13, 23, 27, 9, 27, 7, 13);
        // 领口
        polygon(image, p.shadow(), 13, 8, 19, 8, 19, 12, 16, 15, 13, 12);
        rect(image, 13, 8, 6, 1, p.dark());
        // 肩线金边
        line(image, 6, 10, 13, 9, p.trim());
        line(image, 26, 10, 19, 9, p.trim());
        // 胸甲分片
        rect(image, 10, 16, 12, 1, p.dark());
        rect(image, 10, 17, 12, 6, p.base());
        rect(image, 9, 18, 1, 5, p.light());
        rect(image, 22, 18, 1, 5, p.dark());
        // 腰带与下摆
        rect(image, 9, 23, 14, 2, p.trim());
        rect(image, 9, 25, 14, 2, p.dark());
        rect(image, 10, 27, 12, 1, p.shadow());
        // 中央宝石
        rect(image, 14, 17, 4, 6, p.shadow());
        rect(image, 15, 18, 2, 4, p.gem());
        pixel(image, 15, 18, WHITE);
        // 点缀铆钉
        pixel(image, 7, 10, p.trim());
        pixel(image, 25, 10, p.trim());
        outline(image);
        return image;
    }

    private static BufferedImage legsIcon(Palette p) {
        BufferedImage image = canvas();
        // 腰带
        rect(image, 7, 5, 18, 5, p.base());
        rect(image, 7, 5, 18, 1, p.light());
        rect(image, 7, 9, 18, 1, p.trim());
        rect(image, 8, 10, 16, 1, p.shadow());
        // 双腿（上宽下窄）
        polygon(image, p.base(), 8, 11, 14, 11, 14, 27, 9, 28, 8, 24);
        polygon(image, p.base(), 18, 11, 24, 11, 24, 24, 23, 28, 18, 27);
        // 侧光与内侧阴影
        rect(image, 8, 12, 2, 14, p.light());
        rect(image, 18, 12, 2, 14, p.light());
        rect(image, 13, 12, 1, 15, p.dark());
        rect(image, 24, 12, 1, 15, p.dark());
        // 护膝
        rect(image, 8, 18, 6, 2, p.trim());
        rect(image, 18, 18, 6, 2, p.trim());
        rect(image, 8, 20, 6, 1, p.shadow());
        rect(image, 18, 20, 6, 1, p.shadow());
        // 裤脚
        rect(image, 9, 27, 5, 1, p.dark());
        rect(image, 18, 27, 5, 1, p.dark());
        // 腰扣
        rect(image, 15, 6, 3, 3, p.trim());
        pixel(image, 15, 7, p.gem());
        outline(image);
        return image;
    }

    private static BufferedImage bootsIcon(Palette p) {
        BufferedImage image = canvas();
        // 左靴（脚尖朝左）
        polygon(image, p.base(), 7, 7, 14, 7, 14, 23, 10, 27, 3, 27, 3, 23, 7, 22);
        // 右靴（脚尖朝右）
        polygon(image, p.base(), 18, 7, 25, 7, 25, 22, 29, 23, 29, 27, 22, 27, 18, 23);
        // 靴口
        rect(image, 6, 6, 9, 3, p.trim());
        rect(image, 17, 6, 9, 3, p.trim());
        rect(image, 7, 6, 7, 1, LIGHT_GOLD);
        rect(image, 18, 6, 7, 1, LIGHT_GOLD);
        // 高光
        rect(image, 8, 10, 2, 10, p.light());
        rect(image, 19, 10, 2, 10, p.light());
        rect(image, 13, 12, 1, 9, p.dark());
        rect(image, 25, 12, 1, 8, p.dark());
        // 鞋面与鞋底
        rect(image, 4, 25, 7, 1, p.light());
        rect(image, 22, 25, 7, 1, p.light());
        rect(image, 3, 26, 10, 2, p.shadow());
        rect(image, 22, 26, 10, 2, p.shadow());
        pixel(image, 3, 26, p.trim());
        pixel(image, 29, 26, p.trim());
        // 宝石扣
        rect(image, 9, 14, 3, 3, p.trim());
        rect(image, 21, 14, 3, 3, p.trim());
        pixel(image, 10, 15, p.gem());
        pixel(image, 22, 15, p.gem());
        outline(image);
        return image;
    }

    private static BufferedImage bladeIcon(Palette p) {
        BufferedImage image = canvas();
        // 斜向刀刃：上缘高光、下缘暗面
        polygon(image, INK, 28, 2, 30, 2, 29, 10, 14, 25, 7, 18, 22, 3);
        polygon(image, p.light(), 27, 4, 28, 4, 26, 9, 13, 22, 10, 19);
        polygon(image, p.base(), 27, 5, 27, 9, 14, 22, 13, 20);
        polygon(image, p.dark(), 27, 9, 28, 7, 27, 11, 14, 24, 13, 22);
        polygon(image, WHITE, 24, 5, 27, 4, 12, 19, 11, 19);
        // 护手
        polygon(image, INK, 5, 16, 7, 15, 17, 25, 16, 28, 13, 27, 4, 19);
        polygon(image, p.trim(), 6, 17, 8, 17, 16, 25, 15, 26, 13, 25, 5, 18);
        rect(image, 10, 20, 3, 3, DARK_GOLD);
        pixel(image, 11, 20, p.gem());
        // 握柄与柄头
        polygon(image, INK, 7, 21, 11, 25, 6, 30, 2, 30, 2, 26);
        polygon(image, 0xFF71516D, 7, 23, 9, 25, 5, 29, 3, 27);
        pixel(image, 6, 25, GOLD);
        rect(image, 2, 28, 3, 2, p.trim());
        pixel(image, 2, 28, LIGHT_GOLD);
        return image;
    }

    // ==================== 盔甲层（64x32） ====================

    private static BufferedImage layer1(Palette p, boolean master) {
        BufferedImage image = armorCanvas();

        // 头盔：头顶/侧面/背面覆盖，正面留出面部开口
        face(image, 8, 0, 8, 8, p.light(), p.base(), p.dark());
        face(image, 16, 0, 8, 8, p.dark(), p.shadow(), p.shadow());
        face(image, 0, 8, 8, 8, p.base(), p.light(), p.dark());
        face(image, 16, 8, 8, 8, p.base(), p.light(), p.dark());
        face(image, 24, 8, 8, 8, p.dark(), p.base(), p.shadow());
        // 面部：只保留顶带、两侧与下颌
        rect(image, 8, 8, 8, 2, p.base());
        rect(image, 8, 8, 8, 1, p.light());
        rect(image, 8, 10, 1, 6, p.base());
        rect(image, 15, 10, 1, 6, p.base());
        rect(image, 9, 15, 6, 1, p.dark());
        rect(image, 8, 10, 8, 1, p.trim());
        rect(image, 11, 11, 2, 2, p.gem());
        if (master) {
            rect(image, 9, 5, 6, 2, p.trim());
            pixel(image, 12, 4, LIGHT_GOLD);
            pixel(image, 13, 4, LIGHT_GOLD);
        }

        // 胸甲：躯干 + 手臂
        face(image, 20, 16, 8, 4, p.light(), p.base(), p.dark());
        face(image, 28, 16, 8, 4, p.dark(), p.shadow(), p.shadow());
        face(image, 16, 20, 4, 12, p.base(), p.light(), p.dark());
        face(image, 20, 20, 8, 12, p.base(), p.light(), p.dark());
        face(image, 28, 20, 4, 12, p.base(), p.light(), p.dark());
        face(image, 32, 20, 8, 12, p.dark(), p.base(), p.shadow());
        // 胸甲镶边与宝石
        rect(image, 20, 29, 8, 2, p.trim());
        rect(image, 32, 29, 8, 2, p.trim());
        rect(image, 23, 23, 3, 5, p.dark());
        rect(image, 24, 24, 1, 3, p.gem());
        if (master) {
            rect(image, 20, 20, 8, 1, p.trim());
            rect(image, 32, 20, 8, 1, p.trim());
            rect(image, 21, 21, 6, 1, p.shadow());
        }
        // 手臂（含袖口）
        armFaces(image, 40, p, master);
        // 靴子：仅覆盖腿部下半段
        bootFaces(image, 0, p, master);

        return image;
    }

    private static BufferedImage layer2(Palette p, boolean master) {
        BufferedImage image = armorCanvas();
        // 护腿：腰部下半 + 双腿完整覆盖
        face(image, 20, 26, 8, 6, p.base(), p.light(), p.dark());
        face(image, 32, 26, 8, 6, p.dark(), p.base(), p.shadow());
        face(image, 16, 26, 4, 6, p.base(), p.light(), p.dark());
        face(image, 28, 26, 4, 6, p.base(), p.light(), p.dark());
        rect(image, 20, 26, 8, 1, p.trim());
        rect(image, 32, 26, 8, 1, p.trim());

        legFaces(image, 0, p, master, true);
        return image;
    }

    private static void armFaces(BufferedImage image, int x, Palette p, boolean master) {
        face(image, x + 4, 16, 4, 4, p.light(), p.base(), p.dark());
        face(image, x + 8, 16, 4, 4, p.dark(), p.shadow(), p.shadow());
        face(image, x, 20, 4, 12, p.base(), p.light(), p.dark());
        face(image, x + 4, 20, 4, 12, p.base(), p.light(), p.dark());
        face(image, x + 8, 20, 4, 12, p.base(), p.light(), p.dark());
        face(image, x + 12, 20, 4, 12, p.dark(), p.base(), p.shadow());
        // 袖口
        rect(image, x, 29, 4, 2, p.trim());
        rect(image, x + 4, 29, 4, 2, p.trim());
        rect(image, x + 8, 29, 4, 2, p.trim());
        rect(image, x + 12, 29, 4, 2, p.trim());
        if (master) {
            rect(image, x, 20, 16, 1, p.trim());
            pixel(image, x + 6, 24, p.gem());
            pixel(image, x + 10, 24, p.gem());
        }
    }

    private static void bootFaces(BufferedImage image, int x, Palette p, boolean master) {
        // 行 24-31：靴筒 + 靴底
        face(image, x, 24, 4, 8, p.base(), p.light(), p.dark());
        face(image, x + 4, 24, 4, 8, p.base(), p.light(), p.dark());
        face(image, x + 8, 24, 4, 8, p.base(), p.light(), p.dark());
        face(image, x + 12, 24, 4, 8, p.dark(), p.base(), p.shadow());
        rect(image, x, 24, 16, 2, p.trim());
        rect(image, x, 30, 16, 2, p.shadow());
        if (master) {
            rect(image, x, 26, 16, 1, p.dark());
            pixel(image, x + 6, 27, p.gem());
        }
    }

    private static void legFaces(BufferedImage image, int x, Palette p, boolean master, boolean full) {
        face(image, x + 4, 16, 4, 4, p.light(), p.base(), p.dark());
        face(image, x + 8, 16, 4, 4, p.dark(), p.shadow(), p.shadow());
        face(image, x, 20, 4, 12, p.base(), p.light(), p.dark());
        face(image, x + 4, 20, 4, 12, p.base(), p.light(), p.dark());
        face(image, x + 8, 20, 4, 12, p.base(), p.light(), p.dark());
        face(image, x + 12, 20, 4, 12, p.dark(), p.base(), p.shadow());
        // 护膝
        rect(image, x + 4, 24, 4, 2, p.trim());
        rect(image, x + 8, 24, 4, 2, p.trim());
        rect(image, x + 12, 24, 4, 1, p.trim());
        if (master) {
            rect(image, x + 4, 27, 4, 1, p.dark());
            rect(image, x + 8, 27, 4, 1, p.dark());
            pixel(image, x + 6, 26, p.gem());
        }
        if (full) {
            rect(image, x, 31, 16, 1, p.shadow());
        }
    }

    /** 绘制一个面：左上亮、右下暗、边缘加深 */
    private static void face(BufferedImage image, int x, int y, int w, int h,
                             int base, int light, int dark) {
        for (int dy = 0; dy < h; dy++) {
            for (int dx = 0; dx < w; dx++) {
                int color = base;
                if (dy == 0 || dx == 0) {
                    color = light;
                }
                if (dy == h - 1 || dx == w - 1) {
                    color = dark;
                }
                pixel(image, x + dx, y + dy, color);
            }
        }
    }

    // ==================== 方块贴图 ====================

    private static BufferedImage brewingStand() {
        BufferedImage image = canvas();
        int stone = 0xFF4E4A5C;
        int stoneLight = 0xFF625D72;
        int stoneDark = 0xFF363243;
        int inlay = 0xFF6B4B9E;
        int inlayLight = 0xFF8B6BC4;
        int inlayDark = 0xFF4A3270;

        // 石板底
        rect(image, 0, 0, SIZE, SIZE, stone);
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                int hash = (x * 7 + y * 13) % 11;
                if (hash == 0) {
                    pixel(image, x, y, stoneLight);
                } else if (hash == 5) {
                    pixel(image, x, y, stoneDark);
                }
            }
        }
        // 外框
        rect(image, 0, 0, SIZE, 1, stoneDark);
        rect(image, 0, 31, SIZE, 1, stoneDark);
        rect(image, 0, 0, 1, SIZE, stoneDark);
        rect(image, 31, 0, 1, SIZE, stoneDark);
        // 紫晶台面
        rect(image, 4, 4, 24, 24, inlayDark);
        rect(image, 5, 5, 22, 22, inlay);
        rect(image, 5, 5, 22, 2, inlayLight);
        rect(image, 5, 5, 2, 22, inlayLight);
        rect(image, 25, 7, 2, 20, inlayDark);
        rect(image, 7, 25, 20, 2, inlayDark);
        // 金属包边与铆钉
        rect(image, 4, 4, 24, 1, GOLD);
        rect(image, 4, 27, 24, 1, DARK_GOLD);
        rect(image, 4, 4, 1, 24, GOLD);
        rect(image, 27, 4, 1, 24, DARK_GOLD);
        for (int[] pos : new int[][]{{5, 5}, {26, 5}, {5, 26}, {26, 26}}) {
            pixel(image, pos[0], pos[1], LIGHT_GOLD);
        }
        // 中央蒸馏口与两侧瓶位
        rect(image, 13, 13, 6, 6, INK);
        rect(image, 14, 14, 4, 4, 0xFF2C2440);
        rect(image, 15, 15, 2, 2, TEAL);
        pixel(image, 15, 15, WHITE);
        for (int[] pos : new int[][]{{9, 20}, {21, 20}}) {
            rect(image, pos[0], pos[1], 3, 4, 0xFF9FC4D8);
            rect(image, pos[0] + 1, pos[1] + 1, 1, 2, WHITE);
            pixel(image, pos[0], pos[1] + 3, DARK_GOLD);
        }
        return image;
    }
}
