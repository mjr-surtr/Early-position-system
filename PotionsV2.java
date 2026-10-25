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
            save(items, POTIONS[i], potionAtlas(i));
        }
        save(items, "potion_core", coreAtlas(ALCHEMIST));
        save(items, "alchemist_helmet", helmetIcon(ALCHEMIST, false));
        save(items, "alchemist_chestplate", chestIcon(ALCHEMIST));
        save(items, "alchemist_leggings", legsIcon(ALCHEMIST));
        save(items, "alchemist_boots", bootsIcon(ALCHEMIST));
        save(items, "alchemist_blade", bladeAtlas());

        save(items, "master_alchemist_helmet", helmetIcon(MASTER, true));
        save(items, "master_alchemist_chestplate", chestIcon(MASTER));
        save(items, "master_alchemist_leggings", legsIcon(MASTER));
        save(items, "master_alchemist_boots", bootsIcon(MASTER));

        save(armor, "alchemist_layer_1", layer1(ALCHEMIST, false));
        save(armor, "alchemist_layer_2", layer2(ALCHEMIST, false));
        save(armor, "alchemist_master_layer_1", layer1(MASTER, true));
        save(armor, "alchemist_master_layer_2", layer2(MASTER, true));

        save(block, "early_brewing_stand", brewingStand());
        save(block, "early_brewing_stand_gold", brewingStandGold());
        save(block, "early_brewing_stand_inlay", brewingStandInlay());
        save(block, "early_brewing_stand_glow", brewingStandGlow());
        save(block, "early_brewing_stand_bottle", brewingStandBottle());
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

    /**
     * 32x32 图集，四象限分别被方块瓶身与物品 3D 瓶身按 uv 取样：
     * 左上 (0,0)-(16,16) 瓶身侧面（液体 + 徽记）-> uv [0,0,8,8]
     * 右上 (16,0)-(32,16) 液面               -> uv [8,0,16,8]
     * 左下 (0,16)-(16,32) 玻璃瓶肩/瓶颈侧面     -> uv [0,8,8,16]
     * 右下 (16,16)-(32,32) 软木塞             -> uv [8,8,16,16]
     */
    private static BufferedImage potionAtlas(int type) {
        int emblemColor = (type == 3 || type == 4 || type == 12) ? LIGHT_GOLD : WHITE;
        BufferedImage image = bottleAtlas(LIQUIDS[type], EMBLEMS[type], emblemColor);
        if (type == 13) {
            pixel(image, 29, 17, 0xFFA2ECE0);
            pixel(image, 30, 18, 0xFFA2ECE0);
            pixel(image, 28, 18, 0xFFEFA3BC);
        }
        return image;
    }

    private static BufferedImage bottleAtlas(int liquid, String[] emblem, int emblemColor) {
        BufferedImage image = canvas();
        drawBottleBodySide(image, 0, 0, liquid, emblem, emblemColor);
        drawLiquidSurface(image, 16, 0, liquid);
        drawGlassNeck(image, 0, 16, liquid);
        drawCork(image, 16, 16);
        return image;
    }

    /** 瓶身侧面：液体竖向渐变 + 左侧玻璃高光 + 中央徽记 */
    private static void drawBottleBodySide(BufferedImage image, int ox, int oy,
                                           int liquid, String[] emblem, int emblemColor) {
        for (int y = 0; y < 16; y++) {
            double factor = 1.18 - y * 0.028;
            for (int x = 0; x < 16; x++) {
                double shade = factor;
                if (x == 0) {
                    shade = factor * 1.28;
                } else if (x == 15) {
                    shade = factor * 0.66;
                } else if (x == 14) {
                    shade = factor * 0.84;
                }
                pixel(image, ox + x, oy + y, tint(liquid, shade));
            }
        }
        // 玻璃上缘、瓶底与竖向高光
        rect(image, ox, oy, 16, 1, mix(liquid, WHITE, 0.55));
        rect(image, ox, oy + 15, 16, 1, tint(liquid, 0.55));
        rect(image, ox + 2, oy + 2, 2, 11, mix(liquid, WHITE, 0.4));
        rect(image, ox + 2, oy + 2, 1, 7, mix(liquid, WHITE, 0.7));
        // 徽记：先投影再描亮
        int ex = ox + (16 - emblem[0].length()) / 2;
        int ey = oy + (16 - emblem.length) / 2;
        stamp(image, ex + 1, ey + 1, emblem, tint(liquid, 0.45));
        stamp(image, ex, ey, emblem, emblemColor);
    }

    /** 液面：受光的顶面 + 玻璃边环 + 斜向涡纹 */
    private static void drawLiquidSurface(BufferedImage image, int ox, int oy, int liquid) {
        rect(image, ox, oy, 16, 16, tint(liquid, 1.12));
        rect(image, ox, oy, 16, 1, mix(liquid, WHITE, 0.6));
        rect(image, ox, oy, 1, 16, mix(liquid, WHITE, 0.45));
        rect(image, ox, oy + 15, 16, 1, tint(liquid, 0.62));
        rect(image, ox + 15, oy, 1, 16, tint(liquid, 0.72));
        line(image, ox + 3, oy + 11, ox + 11, oy + 4, mix(liquid, WHITE, 0.3));
        line(image, ox + 4, oy + 12, ox + 12, oy + 5, tint(liquid, 1.45));
        pixel(image, ox + 4, oy + 4, mix(liquid, WHITE, 0.8));
        pixel(image, ox + 5, oy + 5, mix(liquid, WHITE, 0.45));
    }

    /** 玻璃瓶肩与瓶颈：冷色玻璃渐变 + 竖向反光 + 黄铜束环 */
    private static void drawGlassNeck(BufferedImage image, int ox, int oy, int liquid) {
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int color = 0xFFBBD2E2;
                if (x <= 1) {
                    color = 0xFFE4F1F2;
                } else if (x >= 14) {
                    color = 0xFF8FA6B8;
                } else if (x >= 12) {
                    color = 0xFFA8BFCE;
                }
                pixel(image, ox + x, oy + y, color);
            }
        }
        rect(image, ox + 3, oy, 2, 16, 0xFFF0FAFA);
        rect(image, ox + 3, oy + 2, 1, 10, WHITE);
        // 残留药液透过玻璃
        rect(image, ox + 5, oy + 10, 6, 2, tint(liquid, 1.05));
        // 肩部黄铜束环
        rect(image, ox, oy + 13, 16, 2, GOLD);
        rect(image, ox, oy + 13, 16, 1, LIGHT_GOLD);
        rect(image, ox, oy + 15, 16, 1, DARK_GOLD);
    }

    /** 软木塞：木纹 + 上下压印 */
    private static void drawCork(BufferedImage image, int ox, int oy) {
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int color = (x * 5 + y * 3) % 7 == 0 ? 0xFFC49460 : 0xFFB0814F;
                if (x == 0 || x == 15) {
                    color = 0xFF7A5636;
                }
                if (y == 0) {
                    color = 0xFFD8AC72;
                } else if (y == 15) {
                    color = 0xFF6A4A2E;
                }
                pixel(image, ox + x, oy + y, color);
            }
        }
        rect(image, ox + 3, oy + 2, 10, 1, 0xFF8A6240);
        rect(image, ox + 3, oy + 13, 10, 1, 0xFF8A6240);
    }

    /** 炼药台瓶身：默认紫色夜视药水，与方块模型共用同一图集布局 */
    private static BufferedImage brewingStandBottle() {
        return bottleAtlas(LIQUIDS[0], EMBLEMS[0], WHITE);
    }

    // ==================== 物品图标 ====================

    /**
     * 炼金核心图集（供八面水晶 3D 模型按面取样）：
     * 左上主棱面 uv[0,0,8,8]、右上受光面 uv[8,0,16,8]、
     * 左下背光面 uv[0,8,8,16]、右下金环 uv[8,8,16,16]。
     */
    private static BufferedImage coreAtlas(Palette p) {
        BufferedImage image = canvas();
        facet(image, 0, 0, p.base(), p.light(), p.dark(), true);
        facet(image, 16, 0, p.light(), mix(p.light(), WHITE, 0.5), p.base(), false);
        facet(image, 0, 16, p.dark(), p.base(), p.shadow(), false);
        // 金环
        rect(image, 16, 16, 16, 16, GOLD);
        rect(image, 16, 16, 16, 2, LIGHT_GOLD);
        rect(image, 16, 30, 16, 2, DARK_GOLD);
        rect(image, 16, 16, 2, 16, LIGHT_GOLD);
        rect(image, 30, 16, 2, 16, DARK_GOLD);
        for (int i = 0; i < 15; i += 5) {
            rect(image, 17 + i, 24, 2, 2, 0xFF6E4C24);
            pixel(image, 17 + i, 24, 0xFFF0CB80);
        }
        rect(image, 23, 19, 2, 1, TEAL);
        return image;
    }

    /** 水晶棱面：对角明暗渐变 + 两道切面线 */
    private static void facet(BufferedImage image, int ox, int oy,
                              int base, int light, int dark, boolean sparkle) {
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int color = mix(light, dark, (x + y) / 30.0);
                if ((x + y) % 11 == 0) {
                    color = mix(color, light, 0.25);
                }
                pixel(image, ox + x, oy + y, color);
            }
        }
        line(image, ox + 2, oy + 13, ox + 13, oy + 2, mix(base, WHITE, 0.35));
        line(image, ox + 4, oy + 14, ox + 14, oy + 4, mix(base, INK, 0.35));
        if (sparkle) {
            rect(image, ox + 4, oy + 4, 2, 3, WHITE);
            pixel(image, ox + 3, oy + 5, mix(WHITE, base, 0.4));
        }
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

    /**
     * 炼金之刃图集（供 3D 剑模型按部件取样）：
     * 左上刃身 uv[0,0,8,8]、右上黄铜 uv[8,0,16,8]、
     * 左下握柄 uv[0,8,8,16]、右下宝石 uv[8,8,16,16]。
     */
    private static BufferedImage bladeAtlas() {
        BufferedImage image = canvas();
        // 刃身：纵向亮脊 + 两侧压暗
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int color = 0xFFC3D4E0;
                if (x <= 1 || x >= 14) {
                    color = 0xFF5E6B79;
                } else if (x <= 4 || x >= 11) {
                    color = 0xFF8FA2B4;
                }
                if (x == 7 || x == 8) {
                    color = 0xFFEAF4FA;
                }
                if (y == 0 || y == 15) {
                    color = 0xFF8794A2;
                }
                pixel(image, x, y, color);
            }
        }
        // 黄铜构件
        rect(image, 16, 0, 16, 16, GOLD);
        rect(image, 16, 0, 16, 2, LIGHT_GOLD);
        rect(image, 16, 14, 16, 2, DARK_GOLD);
        rect(image, 16, 0, 2, 16, LIGHT_GOLD);
        rect(image, 30, 0, 2, 16, DARK_GOLD);
        rect(image, 19, 7, 10, 2, 0xFFF2D397);
        pixel(image, 25, 11, 0xFF6FA79A);
        pixel(image, 26, 12, 0xFF6FA79A);
        // 皮革握柄
        rect(image, 0, 16, 16, 16, 0xFF5B4030);
        for (int i = 0; i < 16; i += 4) {
            line(image, 0, 16 + i, 15, 16 + i + 2, 0xFF7A5A42);
            line(image, 0, 17 + i, 15, 19 + i, 0xFF43301F);
        }
        // 柄头宝石
        rect(image, 16, 16, 16, 16, INK);
        rect(image, 18, 18, 12, 12, 0xFF2F8F8A);
        rect(image, 20, 20, 8, 8, TEAL);
        polygon(image, 0xFFA2ECE0, 20, 20, 28, 20, 20, 28);
        rect(image, 21, 21, 3, 3, WHITE);
        pixel(image, 27, 26, 0xFF3FA9A4);
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
        // 盔沿金边：顶面前后沿 + 侧面下缘 + 铆钉
        rect(image, 8, 0, 8, 1, p.trim());
        rect(image, 8, 7, 8, 1, p.trim());
        rect(image, 0, 15, 8, 1, p.trim());
        rect(image, 16, 15, 8, 1, p.trim());
        rect(image, 24, 15, 8, 1, p.trim());
        pixel(image, 9, 2, p.trim());
        pixel(image, 14, 2, p.trim());
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
        // 领口金边 + 胸甲铆钉 + 侧腰束带
        rect(image, 16, 20, 4, 1, p.trim());
        rect(image, 20, 20, 8, 1, p.trim());
        rect(image, 28, 20, 4, 1, p.trim());
        rect(image, 32, 20, 8, 1, p.trim());
        pixel(image, 21, 22, p.trim());
        pixel(image, 26, 22, p.trim());
        rect(image, 16, 27, 4, 1, p.trim());
        rect(image, 28, 27, 4, 1, p.trim());
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
        // 腰带扣与侧饰
        rect(image, 23, 27, 2, 3, p.trim());
        pixel(image, 23, 27, LIGHT_GOLD);
        rect(image, 16, 28, 4, 1, p.shadow());
        rect(image, 28, 28, 4, 1, p.shadow());

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
        // 上臂金环与护肘铆钉
        rect(image, x, 21, 16, 1, p.trim());
        pixel(image, x + 2, 23, p.trim());
        pixel(image, x + 13, 23, p.trim());
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
        // 鞋尖/鞋跟金属包边 + 侧扣宝石
        rect(image, x + 4, 28, 4, 1, p.trim());
        rect(image, x + 12, 28, 4, 1, p.trim());
        pixel(image, x + 1, 27, p.gem());
        pixel(image, x + 10, 27, p.gem());
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
        // 外侧缝线金边 + 护膝宝石
        rect(image, x, 21, 1, 10, p.trim());
        rect(image, x + 15, 21, 1, 10, p.trim());
        pixel(image, x + 5, 25, p.gem());
        pixel(image, x + 10, 25, p.gem());
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

    /**
     * 石质底座：顶部 8 行是侧面用的「高光 + 石身 + 金色下缘」条带，
     * 其余区域供顶/底面取样，避免整张贴图被压进 2 像素高的侧面。
     */
    private static BufferedImage brewingStand() {
        BufferedImage image = canvas();
        int stone = 0xFF4E4A5C;
        int stoneLight = 0xFF625D72;
        int stoneDark = 0xFF3A3548;
        int stoneDeep = 0xFF2A2635;
        int highlight = 0xFF746E86;
        int amethyst = 0xFF6B4B9E;

        rect(image, 0, 0, SIZE, SIZE, stone);
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                int hash = (x * 7 + y * 13) % 13;
                if (hash == 0) {
                    pixel(image, x, y, stoneLight);
                } else if (hash == 6) {
                    pixel(image, x, y, stoneDark);
                }
            }
        }
        // 侧面条带
        rect(image, 0, 0, SIZE, 1, highlight);
        rect(image, 0, 1, SIZE, 1, stoneLight);
        rect(image, 0, 5, SIZE, 1, stoneDark);
        rect(image, 0, 6, SIZE, 1, GOLD);
        rect(image, 0, 7, SIZE, 1, DARK_GOLD);
        for (int x : new int[]{7, 15, 23}) {
            rect(image, x, 0, 1, 6, stoneDeep);
        }
        pixel(image, 3, 3, amethyst);
        pixel(image, 12, 2, amethyst);
        pixel(image, 20, 4, amethyst);
        pixel(image, 28, 3, amethyst);
        // 顶/底面区域：暗边收口
        rect(image, 0, 8, SIZE, 1, stoneDark);
        rect(image, 0, 31, SIZE, 1, stoneDeep);
        rect(image, 0, 8, 1, 24, stoneDark);
        rect(image, 31, 8, 1, 24, stoneDeep);
        return image;
    }

    /**
     * 黄铜构件：顶部 8 行是抛光渐变（供所有侧面），下部是带铆钉与铜绿的黄铜板（供顶/底面）。
     */
    private static BufferedImage brewingStandGold() {
        BufferedImage image = canvas();
        int brass = 0xFFC89243;
        int brassLight = 0xFFE9C075;
        int brassDark = 0xFF8E6229;
        int copper = 0xFFA9662F;
        int patina = 0xFF6FA79A;

        rect(image, 0, 0, SIZE, 8, brass);
        rect(image, 0, 0, SIZE, 1, 0xFFF7DA96);
        rect(image, 0, 1, SIZE, 1, brassLight);
        rect(image, 0, 5, SIZE, 1, brassDark);
        rect(image, 0, 6, SIZE, 2, copper);
        rect(image, 4, 1, 3, 5, brassLight);
        rect(image, 5, 1, 1, 5, 0xFFF2D397);

        rect(image, 0, 8, SIZE, 24, 0xFFB9873F);
        for (int y = 8; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                int hash = (x * 11 + y * 5) % 17;
                if (hash == 0) {
                    pixel(image, x, y, 0xFFCD9C52);
                } else if (hash == 8) {
                    pixel(image, x, y, 0xFFA2732F);
                }
            }
        }
        rect(image, 0, 8, SIZE, 1, 0xFFE2BC72);
        rect(image, 0, 31, SIZE, 1, 0xFF6E4C24);
        rect(image, 0, 8, 1, 24, 0xFFE2BC72);
        rect(image, 31, 8, 1, 24, 0xFF6E4C24);
        for (int[] p : new int[][]{{4, 12}, {14, 12}, {24, 12}, {9, 21}, {19, 21}, {29, 21}}) {
            rect(image, p[0], p[1], 2, 2, 0xFF6E4C24);
            pixel(image, p[0], p[1], 0xFFF0CB80);
        }
        pixel(image, 7, 16, patina);
        pixel(image, 8, 17, patina);
        pixel(image, 22, 26, patina);
        pixel(image, 26, 18, 0xFF7A5630);
        return image;
    }

    /** 紫晶台面：金框 + 切面 + 三个瓶座环，瓶座落在未被中央柱遮挡的外环上。 */
    private static BufferedImage brewingStandInlay() {
        BufferedImage image = canvas();
        int edge = 0xFF2A2140;
        int plate = 0xFF4A3568;
        int plateLight = 0xFF6B4B9E;
        int plateDark = 0xFF33254A;

        rect(image, 0, 0, SIZE, SIZE, edge);
        rect(image, 2, 2, 28, 28, plate);
        polygon(image, plateLight, 2, 2, 18, 2, 2, 18);
        polygon(image, plateDark, 30, 30, 14, 30, 30, 14);
        rect(image, 0, 0, SIZE, 2, GOLD);
        rect(image, 0, 30, SIZE, 2, DARK_GOLD);
        rect(image, 0, 0, 2, SIZE, GOLD);
        rect(image, 30, 0, 2, SIZE, DARK_GOLD);
        for (int[] p : new int[][]{{3, 3}, {26, 3}, {3, 26}, {26, 26}}) {
            rect(image, p[0], p[1], 3, 3, DARK_GOLD);
            pixel(image, p[0] + 1, p[1] + 1, TEAL);
        }
        socket(image, 16, 5, 4);
        socket(image, 6, 24, 4);
        socket(image, 26, 24, 4);
        return image;
    }

    /** 青绿晶体：用于蒸馏器顶珠与滴液，四角为暗色玻璃边。 */
    private static BufferedImage brewingStandGlow() {
        BufferedImage image = canvas();
        rect(image, 0, 0, SIZE, SIZE, 0xFF1F1830);
        rect(image, 1, 1, 30, 30, 0xFF3FA9A4);
        rect(image, 2, 2, 28, 28, TEAL);
        polygon(image, 0xFFA2ECE0, 2, 2, 20, 2, 2, 20);
        polygon(image, 0xFF4FC7C0, 29, 29, 12, 29, 29, 12);
        rect(image, 3, 3, 3, 3, WHITE);
        rect(image, 12, 2, 2, 1, 0xFFD6F7F1);
        rect(image, 2, 12, 1, 2, 0xFFD6F7F1);
        line(image, 20, 4, 28, 12, 0xFF6DE0D8);
        line(image, 4, 20, 12, 28, 0xFF3FA9A4);
        return image;
    }

    /** 瓶座环：外圈暗色，内圈青绿，左上一点高光 */
    private static void socket(BufferedImage image, int cx, int cy, int radius) {
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int d2 = dx * dx + dy * dy;
                if (d2 > radius * radius) {
                    continue;
                }
                pixel(image, cx + dx, cy + dy,
                        d2 > (radius - 1) * (radius - 1) ? INK : 0xFF93E8E0);
            }
        }
        pixel(image, cx - 1, cy - 1, WHITE);
    }
}
