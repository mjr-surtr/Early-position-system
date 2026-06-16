import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PotionsV2 {
    private static final int SIZE = 32;

    public static void main(String[] args) throws IOException {
        String basePath = "src/main/resources/assets/tutorial_mod/textures/item/";
        new File(basePath).mkdirs();
        genNightVision(basePath + "early_night_vision_potion.png");
        genHealing(basePath + "early_healing_potion.png");
        genWater(basePath + "early_water_breathing_potion.png");
        genStrength(basePath + "early_strength_potion.png");
        genOmni(basePath + "early_tab_icon.png");
        System.out.println("All textures generated!");
    }

    private static void setPix(BufferedImage img, int x, int y, int c) {
        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE) img.setRGB(x, y, c);
    }
    private static int col(int r, int g, int b) { return 0xFF000000 | (r << 16) | (g << 8) | b; }
    private static int colA(int r, int g, int b, int a) { return (a << 24) | (r << 16) | (g << 8) | b; }

    private static void drawCorkAndNeck(BufferedImage img, int neck1, int neck2) {
        int c1 = col(139, 90, 43), c2 = col(101, 67, 33), c3 = col(160, 110, 60);
        for (int x = 12; x <= 19; x++) { setPix(img, x, 1, c3); setPix(img, x, 2, c1); setPix(img, x, 3, c1); setPix(img, x, 4, c2); }
        for (int x = 13; x <= 18; x += 2) setPix(img, x, 2, c2);
        setPix(img, 13, 3, c2); setPix(img, 15, 3, c2); setPix(img, 17, 3, c2);
        for (int x = 11; x <= 20; x++) { setPix(img, x, 5, neck1); setPix(img, x, 6, neck2); }
        setPix(img, 12, 5, colA(255, 255, 255, 60)); setPix(img, 19, 5, colA(255, 255, 255, 60));
    }

    private static void drawBottleBorder(BufferedImage img, int b1, int b2, int b3) {
        for (int x = 8; x <= 23; x++) setPix(img, x, 7, b1);
        setPix(img, 7, 8, b2); setPix(img, 24, 8, b2);
        for (int y = 8; y <= 25; y++) { setPix(img, 6, y, b2); setPix(img, 25, y, b2); if (y >= 9 && y <= 24) { setPix(img, 7, y, b1); setPix(img, 24, y, b1); } }
        for (int x = 8; x <= 23; x++) setPix(img, x, 24, b1);
        for (int x = 9; x <= 22; x++) setPix(img, x, 25, b1);
        setPix(img, 8, 25, b2); setPix(img, 23, 25, b2);
        for (int x = 10; x <= 21; x++) setPix(img, x, 26, b2);
        for (int x = 11; x <= 20; x++) setPix(img, x, 27, b3);
    }

    private static void fillLiquid(BufferedImage img, int l1, int l2, int l3) {
        for (int y = 8; y <= 25; y++) {
            for (int x = 8; x <= 23; x++) {
                if ((y == 8 || y == 24) && (x == 8 || x == 23)) continue;
                if (y == 25 && (x < 10 || x > 21)) continue;
                int c = y <= 10 ? l3 : (y <= 20 ? l2 : l1);
                setPix(img, x, y, c);
            }
        }
    }

    private static void drawHighlight(BufferedImage img) {
        for (int y = 9; y <= 22; y++) { setPix(img, 8, y, colA(255, 255, 255, 70)); setPix(img, 9, y, colA(255, 255, 255, 40)); }
        setPix(img, 10, 12, colA(255, 255, 255, 30)); setPix(img, 10, 17, colA(255, 255, 255, 30)); setPix(img, 10, 22, colA(255, 255, 255, 30));
    }

    private static void drawShadow(BufferedImage img, int s) {
        for (int x = 11; x <= 20; x++) setPix(img, x, 28, s);
    }

    private static void genNightVision(String path) throws IOException {
        BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < SIZE; y++) for (int x = 0; x < SIZE; x++) setPix(img, x, y, 0);
        
        int b1 = col(180, 140, 220), b2 = col(140, 90, 180), b3 = col(200, 160, 240);
        int l1 = col(30, 10, 60), l2 = col(50, 20, 90), l3 = col(70, 40, 120);
        int s1 = col(255, 255, 200), s2 = col(200, 200, 255), s3 = colA(150, 200, 255, 200);
        int eye = col(255, 230, 100), eyeD = col(50, 30, 80);
        
        drawCorkAndNeck(img, b1, b2);
        drawBottleBorder(img, b1, b2, b3);
        fillLiquid(img, l1, l2, l3);

        // Glowing eye in center (main feature)
        int[][] eyeOuter = {{14,13},{15,13},{16,13},{17,13},{13,14},{14,14},{15,14},{16,14},{17,14},{18,14},{13,15},{14,15},{15,15},{16,15},{17,15},{18,15},{13,16},{14,16},{15,16},{16,16},{17,16},{18,16},{14,17},{15,17},{16,17},{17,17},{15,18},{16,18}};
        for (int[] p : eyeOuter) setPix(img, p[0], p[1], eye);
        setPix(img, 15, 15, eyeD); setPix(img, 16, 15, eyeD); setPix(img, 15, 16, eyeD); setPix(img, 16, 16, eyeD);
        setPix(img, 15, 14, s1);

        // Multiple stars
        int[][] star1 = {{10,9},{11,9},{9,10},{10,10},{11,10},{12,10},{10,11},{11,11}};
        for (int[] p : star1) setPix(img, p[0], p[1], s1);
        int[][] star2 = {{20,8},{21,8},{19,9},{20,9},{21,9},{22,9},{20,10},{21,10}};
        for (int[] p : star2) setPix(img, p[0], p[1], s2);
        int[][] star3 = {{9,19},{10,19},{11,19},{10,20},{11,20}};
        for (int[] p : star3) setPix(img, p[0], p[1], s1);
        int[][] star4 = {{19,18},{20,18},{21,18},{20,19},{21,19}};
        for (int[] p : star4) setPix(img, p[0], p[1], s2);
        
        // Small scattered stars
        int[][] smallStars = {{12,21},{13,21},{18,20},{19,20},{22,15},{23,15},{8,13},{9,13},{18,22},{19,22},{13,11},{14,11},{18,11},{19,11}};
        for (int[] p : smallStars) setPix(img, p[0], p[1], s3);
        int[][] tiny = {{14,20},{17,19},{11,17},{21,21},{8,16},{23,19}};
        for (int[] p : tiny) setPix(img, p[0], p[1], s1);

        drawHighlight(img);
        drawShadow(img, col(30, 10, 50));
        ImageIO.write(img, "png", new File(path));
        System.out.println("Done: " + path);
    }

    private static void genHealing(String path) throws IOException {
        BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < SIZE; y++) for (int x = 0; x < SIZE; x++) setPix(img, x, y, 0);
        
        int b1 = col(255, 150, 180), b2 = col(230, 80, 120), b3 = col(255, 200, 220);
        int l1 = col(130, 10, 30), l2 = col(180, 30, 60), l3 = col(220, 60, 100);
        int h1 = col(255, 220, 230), h2 = col(255, 180, 200), h3 = col(255, 120, 150);
        int cross = col(255, 255, 255);
        
        drawCorkAndNeck(img, b1, b2);
        drawBottleBorder(img, b1, b2, b3);
        fillLiquid(img, l1, l2, l3);

        // BIG HEART (main feature)
        int[][] heartO = {{13,10},{14,10},{15,10},{16,10},{17,10},{18,10},{12,11},{13,11},{14,11},{15,11},{16,11},{17,11},{18,11},{19,11},{12,12},{13,12},{14,12},{15,12},{16,12},{17,12},{18,12},{19,12},{12,13},{13,13},{14,13},{15,13},{16,13},{17,13},{18,13},{19,13},{14,14},{15,14},{16,14},{17,14},{15,15},{16,15}};
        for (int[] p : heartO) setPix(img, p[0], p[1], h3);
        int[][] heartI = {{14,11},{15,11},{16,11},{17,11},{13,12},{14,12},{15,12},{16,12},{17,12},{18,12},{13,13},{14,13},{15,13},{16,13},{17,13},{18,13},{14,14},{15,14},{16,14},{17,14},{15,15},{16,15}};
        for (int[] p : heartI) setPix(img, p[0], p[1], h2);
        setPix(img, 14, 11, h1); setPix(img, 15, 11, h1);

        // MEDICAL CROSS (bottom feature)
        for (int y = 19; y <= 22; y++) { setPix(img, 15, y, cross); setPix(img, 16, y, cross); }
        for (int x = 13; x <= 18; x++) { setPix(img, x, 20, cross); setPix(img, x, 21, cross); }

        // Small hearts around
        int[][] sh1 = {{9,12},{10,12},{9,13},{10,13},{10,14}}; for (int[] p : sh1) setPix(img, p[0], p[1], h2);
        int[][] sh2 = {{21,13},{22,13},{21,14},{22,14},{21,15}}; for (int[] p : sh2) setPix(img, p[0], p[1], h1);
        int[][] mh1 = {{9,18},{10,18},{11,18},{9,19},{10,19},{11,19},{10,20}}; for (int[] p : mh1) setPix(img, p[0], p[1], h3);
        int[][] mh2 = {{19,17},{20,17},{21,17},{19,18},{20,18},{21,18},{20,19}}; for (int[] p : mh2) setPix(img, p[0], p[1], h2);
        
        int[][] deco = {{12,17},{13,17},{18,16},{19,16},{11,22},{12,22},{20,22},{21,22},{13,19}};
        for (int[] p : deco) setPix(img, p[0], p[1], h1);

        drawHighlight(img);
        drawShadow(img, col(80, 10, 30));
        ImageIO.write(img, "png", new File(path));
        System.out.println("Done: " + path);
    }

    private static void genWater(String path) throws IOException {
        BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < SIZE; y++) for (int x = 0; x < SIZE; x++) setPix(img, x, y, 0);
        
        int b1 = col(150, 220, 255), b2 = col(80, 180, 230), b3 = col(200, 240, 255);
        int l1 = col(10, 60, 110), l2 = col(30, 100, 170), l3 = col(60, 150, 210);
        int w1 = col(200, 240, 255), w2 = col(150, 220, 250);
        int bub1 = col(255, 255, 255), bub2 = colA(200, 240, 255, 200);
        int fish = col(255, 200, 100);
        
        drawCorkAndNeck(img, b1, b2);
        drawBottleBorder(img, b1, b2, b3);
        fillLiquid(img, l1, l2, l3);

        // WAVE LINES (main decoration)
        int[][] wave1 = {{10,11},{11,10},{12,11},{13,10},{14,11},{15,10},{16,11},{17,10},{18,11},{19,10},{20,11},{21,10}};
        for (int[] p : wave1) setPix(img, p[0], p[1], w1);
        int[][] wave2 = {{9,16},{10,15},{11,16},{12,15},{13,16},{14,15},{15,16},{16,15},{17,16},{18,15},{19,16},{20,15},{21,16},{22,15}};
        for (int[] p : wave2) setPix(img, p[0], p[1], w2);

        // BIG BUBBLES
        int[][] bb1 = {{14,13},{15,13},{16,13},{17,13},{13,14},{14,14},{15,14},{16,14},{17,14},{18,14},{13,15},{14,15},{15,15},{16,15},{17,15},{18,15},{14,16},{15,16},{16,16},{17,16}};
        for (int[] p : bb1) setPix(img, p[0], p[1], bub2);
        setPix(img, 15, 14, bub1); setPix(img, 16, 14, bub1);
        
        int[][] bb2 = {{20,12},{21,12},{19,13},{20,13},{21,13},{22,13},{19,14},{20,14},{21,14},{22,14},{20,15},{21,15}};
        for (int[] p : bb2) setPix(img, p[0], p[1], bub2);
        setPix(img, 20, 13, bub1);
        
        int[][] bb3 = {{10,19},{11,19},{12,19},{9,20},{10,20},{11,20},{12,20},{13,20},{10,21},{11,21},{12,21}};
        for (int[] p : bb3) setPix(img, p[0], p[1], bub2);
        setPix(img, 11, 20, bub1);
        
        int[][] bb4 = {{19,19},{20,19},{21,19},{18,20},{19,20},{20,20},{21,20},{22,20},{19,21},{20,21},{21,21}};
        for (int[] p : bb4) setPix(img, p[0], p[1], bub2);
        setPix(img, 20, 20, bub1);

        // Small bubbles
        int[][] sb = {{9,10},{10,9},{22,11},{23,12},{8,14},{9,13},{13,20},{14,19},{21,22},{22,23},{14,21},{15,20},{17,22},{18,21},{11,18},{12,17},{22,18},{23,17}};
        for (int[] p : sb) setPix(img, p[0], p[1], bub2);
        int[][] tiny = {{13,9},{18,8},{8,18},{23,21},{14,23},{17,18}};
        for (int[] p : tiny) setPix(img, p[0], p[1], bub1);

        // Tiny fish
        setPix(img, 9, 17, fish); setPix(img, 10, 17, fish); setPix(img, 10, 16, fish); setPix(img, 8, 17, fish);
        setPix(img, 21, 17, fish); setPix(img, 22, 17, fish); setPix(img, 21, 16, fish); setPix(img, 23, 17, fish);

        drawHighlight(img);
        drawShadow(img, col(10, 40, 80));
        ImageIO.write(img, "png", new File(path));
        System.out.println("Done: " + path);
    }

    private static void genStrength(String path) throws IOException {
        BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < SIZE; y++) for (int x = 0; x < SIZE; x++) setPix(img, x, y, 0);
        
        int b1 = col(255, 180, 100), b2 = col(240, 120, 60), b3 = col(255, 220, 150);
        int l1 = col(100, 20, 10), l2 = col(160, 40, 20), l3 = col(200, 70, 40);
        int f1 = col(255, 255, 200), f2 = col(255, 220, 100), f3 = col(255, 150, 50), f4 = col(255, 80, 30);
        int sw1 = col(220, 220, 255), sw2 = col(180, 180, 220);
        
        drawCorkAndNeck(img, b1, b2);
        drawBottleBorder(img, b1, b2, b3);
        fillLiquid(img, l1, l2, l3);

        // BIG RISING FLAME (top center)
        int[][] fOut = {{15,8},{16,8},{14,9},{15,9},{16,9},{17,9},{13,10},{14,10},{15,10},{16,10},{17,10},{18,10},{13,11},{14,11},{15,11},{16,11},{17,11},{18,11},{14,12},{15,12},{16,12},{17,12},{15,13},{16,13}};
        for (int[] p : fOut) setPix(img, p[0], p[1], f4);
        int[][] fMid = {{15,9},{16,9},{14,10},{15,10},{16,10},{17,10},{14,11},{15,11},{16,11},{17,11},{15,12},{16,12}};
        for (int[] p : fMid) setPix(img, p[0], p[1], f3);
        int[][] fIn = {{15,10},{16,10},{15,11},{16,11}};
        for (int[] p : fIn) setPix(img, p[0], p[1], f2);
        setPix(img, 15, 10, f1);

        // SWORD (lower middle)
        for (int y = 16; y <= 21; y++) { setPix(img, 15, y, sw1); setPix(img, 16, y, sw1); }
        setPix(img, 15, 16, sw2); setPix(img, 16, 16, sw2);
        for (int x = 13; x <= 18; x++) { setPix(img, x, 21, sw1); setPix(img, x, 22, sw2); }
        setPix(img, 15, 23, sw2); setPix(img, 16, 23, sw2);
        setPix(img, 15, 24, f3); setPix(img, 16, 24, f3);

        // Side flames
        int[][] lf = {{10,13},{11,13},{9,14},{10,14},{11,14},{12,14},{9,15},{10,15},{11,15},{12,15},{10,16},{11,16}};
        for (int[] p : lf) setPix(img, p[0], p[1], f3);
        setPix(img, 10, 14, f2); setPix(img, 11, 14, f2);
        
        int[][] rf = {{20,12},{21,12},{19,13},{20,13},{21,13},{22,13},{19,14},{20,14},{21,14},{22,14},{20,15},{21,15}};
        for (int[] p : rf) setPix(img, p[0], p[1], f3);
        setPix(img, 20, 13, f2); setPix(img, 21, 13, f2);

        // Lower flames
        int[][] ll = {{9,18},{10,18},{9,19},{10,19},{11,19},{10,20}};
        for (int[] p : ll) setPix(img, p[0], p[1], f4); setPix(img, 10, 19, f3);
        int[][] lr = {{21,18},{22,18},{21,19},{22,19},{23,19},{22,20}};
        for (int[] p : lr) setPix(img, p[0], p[1], f4); setPix(img, 22, 19, f3);

        // Sparks
        int[][] sp = {{13,16},{14,15},{17,15},{18,16},{12,18},{19,18},{14,20},{17,20},{11,10},{20,10}};
        for (int[] p : sp) setPix(img, p[0], p[1], f2);
        int[][] ember = {{13,8},{18,9},{8,16},{23,16},{13,22},{18,22},{10,12},{21,21}};
        for (int[] p : ember) setPix(img, p[0], p[1], f1);

        drawHighlight(img);
        drawShadow(img, col(60, 20, 10));
        ImageIO.write(img, "png", new File(path));
        System.out.println("Done: " + path);
    }

    private static void genOmni(String path) throws IOException {
        BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < SIZE; y++) for (int x = 0; x < SIZE; x++) setPix(img, x, y, 0);
        
        int b1 = col(255, 200, 255), b2 = col(220, 150, 220), b3 = col(255, 230, 255);
        int cork1 = col(139, 90, 43), cork2 = col(101, 67, 33), cork3 = col(160, 110, 60);
        
        // Cork
        for (int x = 12; x <= 19; x++) { setPix(img, x, 1, cork3); setPix(img, x, 2, cork1); setPix(img, x, 3, cork1); setPix(img, x, 4, cork2); }
        for (int x = 13; x <= 18; x += 2) setPix(img, x, 2, cork2);
        setPix(img, 13, 3, cork2); setPix(img, 15, 3, cork2); setPix(img, 17, 3, cork2);
        for (int x = 10; x <= 21; x++) { setPix(img, x, 5, b1); setPix(img, x, 6, b2); }
        setPix(img, 11, 5, colA(255,255,255,60)); setPix(img, 20, 5, colA(255,255,255,60));

        // Circular bottle
        int cx = 15, cy = 18, r = 10;
        for (int y = 7; y <= 28; y++) {
            for (int x = 5; x <= 26; x++) {
                double d = Math.sqrt((x-cx)*(x-cx) + (y-cy)*(y-cy));
                if (d <= r+0.5 && d >= r-0.5) setPix(img, x, y, x < cx ? b2 : b1);
            }
        }
        // Neck connection
        for (int x = 11; x <= 19; x++) setPix(img, x, 7, b1);
        setPix(img, 10, 7, b2); setPix(img, 21, 7, b2); setPix(img, 9, 8, b2); setPix(img, 22, 8, b2);
        for (int x = 11; x <= 20; x++) setPix(img, x, 28, b2);

        // 4-color swirl fill
        int purple = col(150, 50, 200), purpleL = col(200, 100, 255);
        int red = col(255, 80, 120), redL = col(255, 150, 180);
        int blue = col(80, 150, 255), blueL = col(150, 200, 255);
        int green = col(100, 220, 130), greenL = col(170, 240, 180);
        int white = col(255, 255, 255);
        
        for (int y = 8; y <= 27; y++) {
            for (int x = 7; x <= 23; x++) {
                double d = Math.sqrt((x-cx)*(x-cx) + (y-cy)*(y-cy));
                if (d < r-0.5) {
                    int color;
                    if (d < 2) color = white;
                    else if (d < 4) {
                        int avgR = (200+255+150+170)/4, avgG = (100+150+200+240)/4, avgB = (255+180+255+180)/4;
                        color = col(avgR, avgG, avgB);
                    }
                    else if (x < cx && y < cy) color = purple;
                    else if (x >= cx && y < cy) color = red;
                    else if (x < cx && y >= cy) color = blue;
                    else color = green;
                    
                    if (d > 5 && d < 8) {
                        if (x < cx && y < cy && x > 11 && y > 12) color = purpleL;
                        if (x >= cx && y < cy && x < 19 && y > 12) color = redL;
                        if (x < cx && y >= cy && x > 11 && y < 24) color = blueL;
                        if (x >= cx && y >= cy && x < 19 && y < 24) color = greenL;
                    }
                    setPix(img, x, y, color);
                }
            }
        }

        // Stars/hearts/bubbles/flames in each quadrant
        int[][] starP = {{12,11},{11,12},{12,12},{13,12},{12,13}}; for (int[] p : starP) setPix(img, p[0], p[1], white);
        int[][] heartR = {{18,11},{19,11},{18,12},{19,12},{19,13}}; for (int[] p : heartR) setPix(img, p[0], p[1], white);
        int[][] bubB = {{11,21},{12,21},{11,22},{12,22}}; for (int[] p : bubB) setPix(img, p[0], p[1], white);
        int[][] flmG = {{19,21},{20,21},{19,22},{20,22}}; for (int[] p : flmG) setPix(img, p[0], p[1], white);

        // Sparkle ring
        int[][] sparkles = {{8,11},{10,8},{22,9},{24,13},{23,22},{21,25},{13,26},{8,22},{7,15},{25,18}};
        for (int[] p : sparkles) setPix(img, p[0], p[1], col(255, 230, 150));
        
        // Highlight
        for (int y = 10; y <= 25; y++) { setPix(img, 7, y, colA(255,255,255,50)); if (y%3==0) setPix(img, 8, y, colA(255,255,255,30)); }
        
        // Bottom shadow
        for (int x = 11; x <= 20; x++) setPix(img, x, 29, col(60,30,80));
        
        ImageIO.write(img, "png", new File(path));
        System.out.println("Done: " + path);
    }
}
