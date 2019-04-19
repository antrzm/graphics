package ru.nsu.fit.g16203.razumov.volume;


import javafx.geometry.Point3D;
import javafx.util.Pair;
import ru.nsu.fit.g16203.razumov.filters.Filter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Vector;

public class VolumeRendering implements Filter {
    private int Nx, Ny, Nz;
    private Vector<CubeElement> charges;
    private Vector<Pair<Integer, Color>> emisVertices;
    private Vector<Pair<Integer, Double>> absVertices;

    private boolean isAbs, isEmis;

    private double min = Double.MAX_VALUE, max = Double.MIN_VALUE;

    public VolumeRendering() {
        isAbs = false;
        isEmis = false;
    }

    @Override
    public BufferedImage Apply(BufferedImage src) {
        if (isAbs || isEmis) findMinMax();
        else return src;
        int srcHeight = src.getHeight();
        int srcWidth = src.getWidth();

        BufferedImage dst = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);

        double scaleX = (double) srcHeight / Nx, scaleY = (double) srcWidth / Ny;

        for (int i = 0; i < Nx; i++) {
            for (int j = 0; j < Ny; j++) {

                Vector<Double> zCharges = new Vector<>();
                for (int k = 0; k < Nz; k++) {
                    zCharges.add(getVoxelInfluence(
                            i * (1.0 / Nx) + (1.0 / Nx) / 2.0,
                            j * (1.0 / Ny) + (1.0 / Ny) / 2.0,
                            k * (1.0 / Nz) + (1.0 / Nz) / 2.0
                    ));
                }
                int leftX = (int) Math.round(scaleX * i), rightX = (int) Math.round(scaleX * i + scaleX);
                int leftY = (int) Math.round(scaleY * j), rightY = (int) Math.round(scaleY * j + scaleY);
                for (int x = leftX; x < rightX && x < srcWidth; x++) {
                    for (int y = leftY; y < rightY && y < srcHeight; y++) {
                        Color prev = new Color(src.getRGB(x, y));
                        double red = prev.getRed();
                        double green = prev.getGreen();
                        double blue = prev.getBlue();
                        for (int z = 0; z < Nz; z++) {
                            if (isAbs) {
                                double exp = Math.exp(-getAbsorption(zCharges.elementAt(z)) * 1.0 / Nz);
                                red *= exp;
                                green *= exp;
                                blue *= exp;
                            }
                            if (isEmis) {
                                Color color = getEmission(zCharges.elementAt(z));
                                if (color != null) {
                                    red += color.getRed() * 1.0 / Nz;
                                    green += color.getGreen() * 1.0 / Nz;
                                    blue += color.getBlue() * 1.0 / Nz;
                                } else System.out.println("Color is null, value is " + zCharges.elementAt(z));
                            }
                        }
                        int r = Filter.normalizeColor((int) Math.round(red));
                        int g = Filter.normalizeColor((int) Math.round(green));
                        int b = Filter.normalizeColor((int) Math.round(blue));
                        int newRGB = ((r << 16) | (g << 8) | b);
                        dst.setRGB(x, y, newRGB);
                    }
                }
            }
        }
        return dst;
    }

    public void setBounds(int x, int y, int z) {
        this.Nx = x;
        this.Ny = y;
        this.Nz = z;
    }

    public void setCharges(Vector<CubeElement> charges) {
        this.charges = charges;
    }

    private double getAbsorption(double v) {
        int val = (int) ((v - min) * 100.0 / (max - min));
        val = val > 100 ? 100 : val < 0 ? 0 : val;
        Iterator<Pair<Integer, Double>> iterator = absVertices.iterator();
        Pair<Integer, Double> prev = iterator.next();
        while (iterator.hasNext()) {
            Pair<Integer, Double> next = iterator.next();
            if (prev.getKey() <= val && val <= next.getKey()) {
                int x1 = prev.getKey(), x2 = next.getKey();
                double fx1 = prev.getValue(), fx2 = next.getValue();
                return fx1 - (fx1 - fx2) * (val - x1) / (x2 - x1);      //X = f(X1)-( f(X1) - f(X2) )*(X - X1)/(X2 - X1)
            }
            prev = next;
        }
        return 0;
    }

    private Color getEmission(double v) {
        int val = (int) ((v - min) * 100.0 / (max - min));
        val = val > 100 ? 100 : val < 0 ? 0 : val;
        Iterator<Pair<Integer, Color>> iterator = emisVertices.iterator();
        Pair<Integer, Color> prev = iterator.next();
        while (iterator.hasNext()) {
            Pair<Integer, Color> next = iterator.next();
            if (prev.getKey() <= val && val <= next.getKey()) {
                int x1 = prev.getKey(), x2 = next.getKey();
                Color prevCol = prev.getValue();
                Color nextCol = next.getValue();
                double r = prevCol.getRed() - (double) (prevCol.getRed() - nextCol.getRed()) * (double) (val - x1) / (double) (x2 - x1);
                double g = prevCol.getGreen() - (double) (prevCol.getGreen() - nextCol.getGreen()) * (double) (val - x1) / (double) (x2 - x1);
                double b = prevCol.getBlue() - (double) (prevCol.getBlue() - nextCol.getBlue()) * (double) (val - x1) / (double) (x2 - x1);
                int newR = Filter.normalizeColor((int) r);
                int newG = Filter.normalizeColor((int) g);
                int newB = Filter.normalizeColor((int) b);
                return new Color((newR << 16) | (newG << 8) | newB);
            }
            prev = next;
        }
        return null;
    }

    private void findMinMax() {
        double Dx = 1.0 / Nx, Dy = 1.0 / Ny, Dz = 1.0 / Nz;
        for (int i = 0; i < Nx; i++) {
            for (int j = 0; j < Ny; j++)
                for (int k = 0; k < Nz; k++) {
                    double influence = getVoxelInfluence(i * Dx + Dx / 2.0, j * Dy + Dy / 2.0, k * Dz + Dz / 2.0);
                    if (influence > max) max = influence;
                    if (influence < min) min = influence;
                }
        }
    }

    private double getVoxelInfluence(double i, double j, double k) {
        double influence = 0;
        for (CubeElement charge : charges) {
            double distance = charge.point.distance(new Point3D(i, j, k));
            if (distance < 0.1) distance = 0.1;
            influence += charge.value / distance;
        }
        return influence;
    }

    public void setAbs(boolean abs) {
        isAbs = abs;
    }

    public void setEmis(boolean emis) {
        isEmis = emis;
    }

    public boolean isAbs() {
        return isAbs;
    }

    public boolean isEmis() {
        return isEmis;
    }

    public void setEmisVertices(Vector<Pair<Integer, Color>> emisVertices) {
        this.emisVertices = emisVertices;
    }

    public void setAbsVertices(Vector<Pair<Integer, Double>> absVertices) {
        this.absVertices = absVertices;
    }
}
