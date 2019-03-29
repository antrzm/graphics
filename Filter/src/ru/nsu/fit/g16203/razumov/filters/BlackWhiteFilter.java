package ru.nsu.fit.g16203.razumov.filters;

import java.awt.image.BufferedImage;

public class BlackWhiteFilter implements Filter {

    @Override
    public BufferedImage Apply(BufferedImage src) {
        int srcHeight = src.getHeight();
        int srcWidth = src.getWidth();
        BufferedImage dst = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < srcWidth; i++) {
            for (int j = 0; j < srcHeight; j++) {
                int color = src.getRGB(i, j);
                int r = (color >> 16) & 255;
                int g = (color >> 8) & 255;
                int b = color & 255;
                int y = (int) (r * 0.299 + g * 0.587 + b * 0.114);
                dst.setRGB(i, j, (y << 16) | (y << 8) | y);
            }
        }
        return dst;
    }

}
