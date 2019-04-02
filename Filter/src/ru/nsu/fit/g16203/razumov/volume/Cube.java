package ru.nsu.fit.g16203.razumov.volume;


import javafx.geometry.Point3D;

import java.awt.*;
import java.util.Vector;

public class Cube {
    private int boundX, boundY, boundZ;
    private Vector<CubeElement> charges;

    private double min = Double.MAX_VALUE, max = Double.MIN_VALUE;

    public Cube(int Nx, int Ny, int Nz) {
        boundX = Nx;
        boundY = Ny;
        boundZ = Nz;

        charges = new Vector<>();
    }

    public void addCharge(int x, int y, int z, double value) {
        charges.add(new CubeElement(x / boundX, y / boundY, z / boundZ, value));
    }

    public void startRendering() {
    }

    public void getInfluence(int i, int j, int k) {
        double influence = getVoxelInfluence(i, j, k);
        //TODO: something with pixels and influence
    }

    private void findMinMax() {
        for (int i = 0; i < boundX; i++) {
            for (int j = 0; j < boundY; j++)
                for (int k = 0; k < boundZ; k++) {
                    double influence = getVoxelInfluence(i, j, k);
                    if (influence > max) max = influence;
                    if (influence < min) min = influence;
                }
        }
    }

    private double getVoxelInfluence(int i, int j, int k) {
        double influence = 0;
        for (CubeElement charge : charges) {
            double distance = charge.point.distance(new Point3D(i / boundX, j / boundY, k / boundZ));
            if (distance < 0.1) distance = 0.1;
            influence += charge.value / distance;
        }
        return influence;
    }
}
