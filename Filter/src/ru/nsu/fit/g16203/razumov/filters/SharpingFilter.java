package ru.nsu.fit.g16203.razumov.filters;

import java.awt.image.BufferedImage;

public class SharpingFilter implements Filter {
    @Override
    public BufferedImage Apply(BufferedImage source) {
        int sourceHeight = source.getHeight();
        int sourceWidth = source.getWidth();
        BufferedImage result = new BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_INT_RGB);

        int i;
        int j;
        int redResult = 0;
        int greenResult = 0;
        int blueResult = 0;
        double[][] matrix = {{0, -1, 0},
                {-1, 5, -1,},
                {0, -1, 0}};

        for (i = 1; i < sourceWidth - 1; i++) {
            for (j = 1; j < sourceHeight - 1; j++) {
                redResult = (int) (
                        matrix[0][1] * ((source.getRGB(i - 1, j) >> 16) & 255) +
                                matrix[1][0] * ((source.getRGB(i, j - 1) >> 16) & 255) +
                                matrix[1][1] * ((source.getRGB(i, j) >> 16) & 255) +
                                matrix[1][2] * ((source.getRGB(i, j + 1) >> 16) & 255) +
                                matrix[2][1] * ((source.getRGB(i + 1, j) >> 16) & 255));

                redResult = normalizeColorPart(redResult);

                greenResult = (int) (
                        matrix[0][1] * ((source.getRGB(i - 1, j) >> 8) & 255) +
                                matrix[1][0] * ((source.getRGB(i, j - 1) >> 8) & 255) +
                                matrix[1][1] * ((source.getRGB(i, j) >> 8) & 255) +
                                matrix[1][2] * ((source.getRGB(i, j + 1) >> 8) & 255) +
                                matrix[2][1] * ((source.getRGB(i + 1, j) >> 8) & 255));
                greenResult = normalizeColorPart(greenResult);

                blueResult = (int) (
                        matrix[0][1] * (source.getRGB(i - 1, j) & 255) +
                                matrix[1][0] * (source.getRGB(i, j - 1) & 255) +
                                matrix[1][1] * (source.getRGB(i, j) & 255) +
                                matrix[1][2] * (source.getRGB(i, j + 1) & 255) +
                                matrix[2][1] * (source.getRGB(i + 1, j) & 255));
                blueResult = normalizeColorPart(blueResult);

                int newRGB = ((redResult << 16) | (greenResult << 8) | blueResult);
                result.setRGB(i, j, newRGB);
            }
        }
        if (sourceWidth < sourceHeight)
            for (i = 1; i < sourceWidth; i++) {
                result.setRGB(i, 0, result.getRGB(i, 1));
                result.setRGB(0, i, result.getRGB(1, i));
            }
        else
            for (i = 1; i < sourceHeight; i++) {
                result.setRGB(i, 0, result.getRGB(i, 1));
                result.setRGB(0, i, result.getRGB(1, i));
            }
        result.setRGB(0, 0, result.getRGB(1, 1));

        return result;
    }

    private int normalizeColorPart(int a) {
        if (a > 255) {
            a = 255;
        }
        if (a < 0) {
            a = 0;
        }

        return a;
    }
}
