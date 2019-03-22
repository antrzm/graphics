package ru.nsu.fit.g16203.razumov.filters;

import java.awt.image.BufferedImage;

public class StampingFilter implements Filter{

    @Override
    public BufferedImage Apply(BufferedImage source) {
        int sourceHeight = source.getHeight();
        int sourceWidth = source.getWidth();
        BufferedImage result = new BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_INT_RGB);

        int i;
        int j;
        int redResult;
        int greenResult;
        int blueResult;

        for (i = 1; i < sourceWidth - 1; i++) {
            for (j = 1; j < sourceHeight - 1; j++) {
                redResult =
                        ((source.getRGB(i - 1, j) >> 16) & 255) -
                                ((source.getRGB(i, j - 1) >> 16) & 255) +
                                ((source.getRGB(i, j + 1) >> 16) & 255) -
                                ((source.getRGB(i + 1, j) >> 16) & 255);
                redResult += 128;
                redResult = normalizeColorPart(redResult);

                greenResult =
                        ((source.getRGB(i - 1, j) >> 8) & 255) -
                                ((source.getRGB(i, j - 1) >> 8) & 255) +
                                ((source.getRGB(i, j + 1) >> 8) & 255) -
                                ((source.getRGB(i + 1, j) >> 8) & 255);
                greenResult += 128;
                greenResult = normalizeColorPart(greenResult);

                blueResult =
                        ((source.getRGB(i - 1, j)) & 255) -
                                ((source.getRGB(i, j - 1)) & 255) +
                                ((source.getRGB(i, j + 1)) & 255) -
                                ((source.getRGB(i + 1, j)) & 255);
                blueResult += 128;
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
