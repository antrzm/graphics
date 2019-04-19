package ru.nsu.fit.g16203.razumov.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class OrderedDithering implements Filter {
    private int size;
    private int r, g, b;

    public OrderedDithering(int size, int r, int g, int b) {
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public BufferedImage Apply(BufferedImage src) {
        int srcHeight = src.getHeight();
        int srcWidth = src.getWidth();
        BufferedImage dst = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);
        int[] pixels = src.getRGB(0, 0, srcWidth, srcHeight, null, 0, srcWidth);

        int[] errorMatrix = initMatrix(size);
        double div = 1.0 / Math.pow(size, 2);

        for (int i = 0; i < srcHeight; i++) {
            for (int j = 0; j < srcWidth; j++) {
                Color color = new Color(pixels[i * srcWidth + j]);
                double err = (errorMatrix[(j % size) * size + i % size] * div - 0.5);
                int newR = Filter.normalizeColor((int) (color.getRed() + err * (255 / (r - 1))));
                int newG = Filter.normalizeColor((int) (color.getGreen() + err * (255 / (g - 1))));
                int newB = Filter.normalizeColor((int) (color.getBlue() + err * (255 / (b - 1))));

                Color newColor = new Color(newR, newG, newB);

                if (r > 1) {
                    newR = ((int) Math.round(newColor.getRed() * (r - 1) / 255.0)) * 255 / (r - 1);
                }
                if (g > 1) {
                    newG = ((int) Math.round(newColor.getGreen() * (g - 1) / 255.0)) * 255 / (g - 1);
                }
                if (b > 1) {
                    newB = ((int) Math.round(newColor.getBlue() * (b - 1) / 255.0)) * 255 / (b - 1);
                }

                int newRGB = ((newR << 16) | (newG << 8) | newB);
                dst.setRGB(j, i, newRGB);
            }
        }
        return dst;
    }

    private int[] initMatrix(int size) {
        int[] matrix = new int[size * size];
        int length = size / 2;

        if (size == 1) {
            matrix[0] = 0;
            return matrix;
        }
        int[] helpMatrix = initMatrix(length);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < length; j++) {
                for (int k = 0; k < length; k++) {
                    matrix[j * length * 2 + (length * i + k)] = 4 * helpMatrix[j * length + k] + 2 * i;
                    matrix[((length + j) * length) * 2 + (length * i + k)] = 4 * helpMatrix[j * length + k] + 3 - 2 * i;
                }
            }
        }

        return matrix;
    }
}
