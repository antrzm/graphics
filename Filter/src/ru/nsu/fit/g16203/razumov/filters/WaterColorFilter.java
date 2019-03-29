package ru.nsu.fit.g16203.razumov.filters;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Vector;

public class WaterColorFilter implements Filter {
    private static final int MEDIAN = 12;

    @Override
    public BufferedImage Apply(BufferedImage src) {
        int srcHeight = src.getHeight();
        int srcWidth = src.getWidth();
        BufferedImage dst = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);

        Vector<Integer> reds = new Vector<>();
        Vector<Integer> greens = new Vector<>();
        Vector<Integer> blues = new Vector<>();

        int color;
        for (int j = 2; j < srcHeight - 2; j++) {
            for (int i = 2; i < srcWidth - 2; i++) {
                reds.clear();
                greens.clear();
                blues.clear();
                for (int k = -2; k <= 2; k++) {
                    for (int m = -2; m <= 2; m++) {
                        color = src.getRGB(i + m, j + k);
                        reds.add((color >> 16) & 255);
                        greens.add((color >> 8) & 255);
                        blues.add(color & 255);
                    }
                }

                Collections.sort(reds);
                Collections.sort(greens);
                Collections.sort(blues);
                int newRGB = ((reds.get(MEDIAN) << 16) | (greens.get(MEDIAN) << 8) | blues.get(MEDIAN));
                dst.setRGB(i, j, newRGB);
            }
        }

        dst = new SharpenFilter().Apply(dst);
        return dst;
    }
}
