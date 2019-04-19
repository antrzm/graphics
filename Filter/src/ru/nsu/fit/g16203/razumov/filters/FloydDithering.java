package ru.nsu.fit.g16203.razumov.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class FloydDithering implements Filter {
    private int r, g, b;

    public FloydDithering(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public BufferedImage Apply(BufferedImage src) {
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        BufferedImage dst = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);

        int[] reds = new int[srcWidth * srcHeight];
        int[] greens = new int[srcWidth * srcHeight];
        int[] blues = new int[srcWidth * srcHeight];

        for (int i = 0; i < srcHeight; ++i) {
            for (int j = 0; j < srcWidth; ++j) {
                Color color = new Color(src.getRGB(j, i));
                reds[i * srcWidth + j] = color.getRed();
                greens[i * srcWidth + j] = color.getGreen();
                blues[i * srcWidth + j] = color.getBlue();
            }
        }

        for (int i = 0; i < srcHeight; i++) {
            for (int j = 0; j < srcWidth; j++) {
                Color color = new Color(reds[i * srcWidth + j], greens[i * srcWidth + j], blues[i * srcWidth + j]);

                int newR = 0, newG = 0, newB = 0;

                if (r > 1) {
                    newR = ((int) Math.round(color.getRed() * (r - 1) / 255d)) * 255 / (r - 1);
                }
                if (g > 1) {
                    newG = ((int) Math.round(color.getGreen() * (g - 1) / 255d)) * 255 / (g - 1);
                }
                if (b > 1) {
                    newB = ((int) Math.round(color.getBlue() * (b - 1) / 255d)) * 255 / (b - 1);
                }

                int newRGB = ((newR << 16) | (newG << 8) | newB);
                dst.setRGB(j, i, newRGB);

                int redDiff = color.getRed() - newR;
                int greenDiff = color.getGreen() - newG;
                int blueDiff = color.getBlue() - newB;

                if (i < srcHeight - 1) {
                    reds[(i + 1) * srcWidth + j] = Filter.normalizeColor((int) (reds[(i + 1) * srcWidth + j] + redDiff * 5.0 / 16.0));
                    greens[(i + 1) * srcWidth + j] = Filter.normalizeColor((int) (greens[(i + 1) * srcWidth + j] + greenDiff * 5.0 / 16.0));
                    blues[(i + 1) * srcWidth + j] = Filter.normalizeColor((int) (blues[(i + 1) * srcWidth + j] + blueDiff * 5f / 16f));

                    if (j < srcWidth - 1) {
                        reds[(i + 1) * srcWidth + (j + 1)] = Filter.normalizeColor((int) (reds[(i + 1) * srcWidth + (j + 1)] + redDiff * (1.0 / 16.0)));
                        greens[(i + 1) * srcWidth + (j + 1)] = Filter.normalizeColor((int) (greens[(i + 1) * srcWidth + (j + 1)] + greenDiff * (1.0 / 16.0)));
                        blues[(i + 1) * srcWidth + (j + 1)] = Filter.normalizeColor((int) (blues[(i + 1) * srcWidth + (j + 1)] + blueDiff * (1.0 / 16.0)));
                    }

                    if (j > 0) {
                        reds[(i + 1) * srcWidth + (j - 1)] = Filter.normalizeColor((int) (reds[(i + 1) * srcWidth + (j - 1)] + redDiff * (3.0 / 16.0)));
                        greens[(i + 1) * srcWidth + (j - 1)] = Filter.normalizeColor((int) (greens[(i + 1) * srcWidth + (j - 1)] + greenDiff * (3.0 / 16.0)));
                        blues[(i + 1) * srcWidth + (j - 1)] = Filter.normalizeColor((int) (blues[(i + 1) * srcWidth + (j - 1)] + blueDiff * (3.0 / 16.0)));
                    }
                }

                if (j < srcWidth - 1) {
                    reds[i * srcWidth + (j + 1)] = Filter.normalizeColor((int) (reds[i * srcWidth + (j + 1)] + redDiff * (7.0 / 16.0)));
                    greens[i * srcWidth + (j + 1)] = Filter.normalizeColor((int) (greens[i * srcWidth + (j + 1)] + greenDiff * (7.0 / 16.0)));
                    blues[i * srcWidth + (j + 1)] = Filter.normalizeColor((int) (blues[i * srcWidth + (j + 1)] + blueDiff * (7.0 / 16.0)));
                }
            }
        }
        return dst;
    }
}
