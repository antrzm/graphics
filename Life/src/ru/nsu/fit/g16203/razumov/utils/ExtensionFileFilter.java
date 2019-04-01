package ru.nsu.fit.g16203.razumov.utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * File filter which leaves only directories and files with specific extension
 * @author Tagir F. Valeev
 */
public class ExtensionFileFilter extends FileFilter
{
    String extension, description;

    public ExtensionFileFilter(String extension, String description)
    {
        this.extension = extension;
        this.description = description;
    }

    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith("."+extension.toLowerCase());
    }

    @Override
    public String getDescription() {
        return description+" (*."+extension+")";
    }
}
