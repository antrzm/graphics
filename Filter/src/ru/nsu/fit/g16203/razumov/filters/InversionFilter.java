package ru.nsu.fit.g16203.razumov.filters;

import ru.nsu.fit.g16203.razumov.panels.ImagePanel;

import java.awt.image.BufferedImage;

public class InversionFilter implements Filter {

    @Override
    public BufferedImage Apply(BufferedImage source) {
        int sourceHeight = source.getHeight();
        int sourceWidth = source.getWidth();
        BufferedImage newImage = new BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < sourceWidth; i++) {
            for (int j = 0; j < sourceHeight; j++) {
                int rgb = source.getRGB(i, j);
                int r = 255 - (rgb >> 16) & 255;
                int g = 255 - (rgb >> 8) & 255;
                int b = 255 - rgb & 255;
                newImage.setRGB(i, j, (r << 16) | (g << 8) | b);
            }
        }
        return newImage;
    }
}
