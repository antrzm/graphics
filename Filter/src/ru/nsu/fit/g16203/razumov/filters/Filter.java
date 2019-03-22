package ru.nsu.fit.g16203.razumov.filters;

import ru.nsu.fit.g16203.razumov.panels.ImagePanel;

import java.awt.image.BufferedImage;

public interface Filter {
    BufferedImage Apply(BufferedImage source);
}
