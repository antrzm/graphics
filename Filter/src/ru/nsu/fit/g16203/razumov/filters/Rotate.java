package ru.nsu.fit.g16203.razumov.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Rotate implements Filter {

    private int angle;

    public Rotate(int value) {
        this.angle = value;
    }

    @Override
    public BufferedImage Apply(BufferedImage src) {
        int srcHeight = src.getHeight();
        int srcWidth = src.getWidth();

        double angle = Math.toRadians(this.angle);

        int dstHeight = (int) (srcWidth * Math.abs(Math.sin(angle)) + srcHeight * Math.abs(Math.cos(angle)));
        int dstWidth = (int) (srcHeight * Math.abs(Math.sin(angle)) + srcWidth * Math.abs(Math.cos(angle)));

        BufferedImage dst = new BufferedImage(dstWidth, dstHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g1 = dst.createGraphics();
        g1.setColor(new Color(240,240,240));
        g1.fillRect(0, 0, dstWidth, dstHeight);
        g1.translate(dstWidth / 2, dstHeight / 2);
        g1.rotate(angle);
        g1.translate(-srcWidth / 2, -srcHeight / 2);
        g1.drawImage(src, 0, 0, null);
        g1.dispose();

        return dst;
    }
}
