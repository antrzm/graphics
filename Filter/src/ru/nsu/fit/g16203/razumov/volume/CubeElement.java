package ru.nsu.fit.g16203.razumov.volume;

import javafx.geometry.Point3D;

public class CubeElement {
    Point3D point;
    double value;

    public CubeElement(double x, double y, double z, double value) {
        point = new Point3D(x,y,z);
        this.value = value;
    }
}
