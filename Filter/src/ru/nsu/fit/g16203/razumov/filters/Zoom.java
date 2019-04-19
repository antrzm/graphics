package ru.nsu.fit.g16203.razumov.filters;

import java.awt.image.BufferedImage;

public class Zoom implements Filter {
    @Override
    public BufferedImage Apply(BufferedImage src) {
        int srcHeight = src.getHeight();
        int srcWidth = src.getWidth();

        BufferedImage dst = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);
        int r, r1, r2, r3, r4, g, g1, g2, g3, g4, b, b1, b2, b3, b4;

        for (int i = 0; i < srcWidth; i++) {
            for (int j = 0; j < srcHeight; j++) {
                if ((i - 1) % 2 == 0 && (j - 1) % 2 == 0) {
                    int x = srcWidth / 4 + (i - 1) / 2;
                    int y = srcHeight / 4 + (j - 1) / 2;

                    int rgb = src.getRGB(x, y);
                    r = (rgb >> 16) & 255;
                    g = (rgb >> 8) & 255;
                    b = rgb & 255;
                } else if ((i - 1) % 2 == 0) {
                    int x = srcWidth / 4 + (i - 1) / 2;
                    int y = srcHeight / 4 + (j - 2) / 2;

                    int rgb1 = src.getRGB(x, y);
                    int rgb2 = src.getRGB(x, y + 1);

                    r1 = (rgb1 >> 16) & 255;
                    g1 = (rgb1 >> 8) & 255;
                    b1 = rgb1 & 255;
                    r2 = (rgb2 >> 16) & 255;
                    g2 = (rgb2 >> 8) & 255;
                    b2 = rgb2 & 255;

                    r = (r1 + r2) / 2;
                    g = (g1 + g2) / 2;
                    b = (b1 + b2) / 2;
                } else if ((j - 1) % 2 == 0) {
                    int x = srcWidth / 4 + (i - 2) / 2;
                    int y = srcHeight / 4 + (j - 1) / 2;

                    int rgb1 = src.getRGB(x, y);
                    int rgb2 = src.getRGB(x + 1, y);

                    r1 = (rgb1 >> 16) & 255;
                    g1 = (rgb1 >> 8) & 255;
                    b1 = rgb1 & 255;
                    r2 = (rgb2 >> 16) & 255;
                    g2 = (rgb2 >> 8) & 255;
                    b2 = rgb2 & 255;

                    r = (r1 + r2) / 2;
                    g = (g1 + g2) / 2;
                    b = (b1 + b2) / 2;
                } else {
                    int x = srcWidth / 4 + (i - 2) / 2;
                    int y = srcHeight / 4 + (j - 2) / 2;

                    int rgb1 = src.getRGB(x, y);
                    int rgb2 = src.getRGB(x + 1, y);
                    int rgb3 = src.getRGB(x, y + 1);
                    int rgb4 = src.getRGB(x + 1, y + 1);

                    r1 = (rgb1 >> 16) & 255;
                    g1 = (rgb1 >> 8) & 255;
                    b1 = rgb1 & 255;
                    r2 = (rgb2 >> 16) & 255;
                    g2 = (rgb2 >> 8) & 255;
                    b2 = rgb2 & 255;
                    r3 = (rgb3 >> 16) & 255;
                    g3 = (rgb3 >> 8) & 255;
                    b3 = rgb3 & 255;
                    r4 = (rgb4 >> 16) & 255;
                    g4 = (rgb4 >> 8) & 255;
                    b4 = rgb4 & 255;

                    r = (r1 + r2 + r3 + r4) / 4;
                    g = (g1 + g2 + g3 + g4) / 4;
                    b = (b1 + b2 + b3 + b4) / 4;
                }

                int newRGB = ((r << 16) | (g << 8) | b);
                dst.setRGB(i, j, newRGB);
            }
        }
        return dst;
    }
}
