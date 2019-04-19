package ru.nsu.fit.g16203.razumov.filters;

import java.awt.image.BufferedImage;

public class StampingFilter implements Filter {
    @Override
    public BufferedImage Apply(BufferedImage src) {
        int srcHeight = src.getHeight();
        int srcWidth = src.getWidth();
        BufferedImage dst = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);

        int redResult;
        int greenResult;
        int blueResult;

        for (int i = 1; i < srcWidth - 1; i++) {
            for (int j = 1; j < srcHeight - 1; j++) {
                redResult = (
                        (src.getRGB(i - 1, j) >> 16) & 255) -
                        ((src.getRGB(i, j - 1) >> 16) & 255) +
                        ((src.getRGB(i, j + 1) >> 16) & 255) -
                        ((src.getRGB(i + 1, j) >> 16) & 255);
                redResult += 128;
                redResult = Filter.normalizeColor(redResult);

                greenResult = (
                        (src.getRGB(i - 1, j) >> 8) & 255) -
                        ((src.getRGB(i, j - 1) >> 8) & 255) +
                        ((src.getRGB(i, j + 1) >> 8) & 255) -
                        ((src.getRGB(i + 1, j) >> 8) & 255);
                greenResult += 128;
                greenResult = Filter.normalizeColor(greenResult);

                blueResult = (
                        (src.getRGB(i - 1, j)) & 255) -
                        ((src.getRGB(i, j - 1)) & 255) +
                        ((src.getRGB(i, j + 1)) & 255) -
                        ((src.getRGB(i + 1, j)) & 255);
                blueResult += 128;
                blueResult = Filter.normalizeColor(blueResult);

                int newRGB = ((redResult << 16) | (greenResult << 8) | blueResult);
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
