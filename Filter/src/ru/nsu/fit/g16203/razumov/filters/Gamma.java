package ru.nsu.fit.g16203.razumov.filters;

import java.awt.image.BufferedImage;

public class Gamma implements Filter {

    public int coef;

    public Gamma(int coef){
        this.coef = coef;
    }

    @Override
    public BufferedImage Apply(BufferedImage src) {
        int srcHeight = src.getHeight();
        int srcWidth = src.getWidth();
        BufferedImage dst = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < srcWidth; i++) {
            for (int j = 0; j < srcHeight; j++) {
                int rgb = src.getRGB(i, j);
                int r = (rgb >> 16) & 255;
                int g = (rgb >> 8) & 255;
                int b = rgb & 255;

                r = (int) (Math.pow((double) r / (double) 255, coef) * 255);
                g = (int) (Math.pow((double) g / (double) 255, coef) * 255);
                b = (int) (Math.pow((double) b / (double) 255, coef) * 255);

                r = Filter.normalizeColor(r);
                g = Filter.normalizeColor(g);
                b = Filter.normalizeColor(b);

                int newRGB = ((r << 16) | (g << 8) | b);
                dst.setRGB(i, j, newRGB);
            }
        }
        return dst;
    }
}
