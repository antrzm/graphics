package ru.nsu.fit.g16203.razumov.filters;

import java.awt.image.BufferedImage;

public class GaussFilter implements Filter {
    @Override
    public BufferedImage Apply(BufferedImage src) {
        int srcHeight = src.getHeight();
        int srcWidth = src.getWidth();
        BufferedImage dst = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);

        int newR, newG, newB;

        double[][] matrix = {
                {0.0625, 0.125, 0.0625},
                {0.125, 0.25, 0.125,},
                {0.0625, 0.125, 0.0625}
        };

        for (int i = 1; i < srcWidth - 1; i++) {
            for (int j = 1; j < srcHeight - 1; j++) {
                newR = (int) (
                        matrix[0][0] * ((src.getRGB(i - 1, j - 1) >> 16) & 255) +
                                matrix[0][1] * ((src.getRGB(i - 1, j) >> 16) & 255) +
                                matrix[0][2] * ((src.getRGB(i - 1, j + 1) >> 16) & 255) +
                                matrix[1][0] * ((src.getRGB(i, j - 1) >> 16) & 255) +
                                matrix[1][1] * ((src.getRGB(i, j) >> 16) & 255) +
                                matrix[1][2] * ((src.getRGB(i, j + 1) >> 16) & 255) +
                                matrix[2][0] * ((src.getRGB(i + 1, j - 1) >> 16) & 255) +
                                matrix[2][1] * ((src.getRGB(i + 1, j) >> 16) & 255) +
                                matrix[2][2] * ((src.getRGB(i + 1, j + 1) >> 16) & 255));

                newG = (int) (
                        matrix[0][0] * ((src.getRGB(i - 1, j - 1) >> 8) & 255) +
                                matrix[0][1] * ((src.getRGB(i - 1, j) >> 8) & 255) +
                                matrix[0][2] * ((src.getRGB(i - 1, j + 1) >> 8) & 255) +
                                matrix[1][0] * ((src.getRGB(i, j - 1) >> 8) & 255) +
                                matrix[1][1] * ((src.getRGB(i, j) >> 8) & 255) +
                                matrix[1][2] * ((src.getRGB(i, j + 1) >> 8) & 255) +
                                matrix[2][0] * ((src.getRGB(i + 1, j - 1) >> 8) & 255) +
                                matrix[2][1] * ((src.getRGB(i + 1, j) >> 8) & 255) +
                                matrix[2][2] * ((src.getRGB(i + 1, j + 1) >> 8) & 255));
                newB = (int) (
                        matrix[0][0] * ((src.getRGB(i - 1, j - 1)) & 255) +
                                matrix[0][1] * ((src.getRGB(i - 1, j)) & 255) +
                                matrix[0][2] * ((src.getRGB(i - 1, j + 1)) & 255) +
                                matrix[1][0] * ((src.getRGB(i, j - 1)) & 255) +
                                matrix[1][1] * ((src.getRGB(i, j)) & 255) +
                                matrix[1][2] * ((src.getRGB(i, j + 1)) & 255) +
                                matrix[2][0] * ((src.getRGB(i + 1, j - 1)) & 255) +
                                matrix[2][1] * ((src.getRGB(i + 1, j)) & 255) +
                                matrix[2][2] * ((src.getRGB(i + 1, j + 1)) & 255));
                int newRGB = ((newR << 16) | (newG << 8) | newB);
                dst.setRGB(i, j, newRGB);
            }
        }
        for (int i = 1; i < srcHeight; i++) {
            dst.setRGB(i, 0, dst.getRGB(i, 1));
            dst.setRGB(0, i, dst.getRGB(1, i));
        }
        dst.setRGB(0, 0, dst.getRGB(1, 1));

        return dst;
    }
}
