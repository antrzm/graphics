package ru.nsu.fit.g16203.razumov.filters;

import java.awt.image.BufferedImage;

public class BlackWhiteFilter implements Filter {

    @Override
    public BufferedImage Apply(BufferedImage source) {
        int sourceHeight = source.getHeight();
        int sourceWidth = source.getWidth();
        BufferedImage newImage = new BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < sourceWidth; i++) {
            for (int j = 0; j < sourceHeight; j++) {
                int rgb = source.getRGB(i, j);
                int r = (rgb >> 16) & 255;
                int g = (rgb >> 8) & 255;
                int b = rgb & 255;
                int y = (int) (r * 0.299 + g * 0.587 + b * 0.114);

                newImage.setRGB(i, j, (y << 16) | (y << 8) | y);
            }
        }

        return newImage;
    }

}
