package ru.nsu.fit.g16203.razumov.filters;

import ru.nsu.fit.g16203.razumov.panels.ImagePanel;

import java.awt.image.BufferedImage;

public class InversionFilter implements Filter {

    @Override
    public BufferedImage Apply(BufferedImage src) {
        int srcHeight = src.getHeight();
        int srcWidth = src.getWidth();
        BufferedImage dst = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < srcWidth; i++) {
            for (int j = 0; j < srcHeight; j++) {
                int color = src.getRGB(i, j);
                int r = 255 - (color >> 16) & 255;
                int g = 255 - (color >> 8) & 255;
                int b = 255 - color & 255;
                dst.setRGB(i, j, (r << 16) | (g << 8) | b);
            }
        }
        return dst;
    }
}
