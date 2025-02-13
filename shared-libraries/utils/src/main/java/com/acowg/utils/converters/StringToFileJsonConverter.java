package com.acowg.utils.converters;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.io.File;
import java.nio.file.Paths;

public class StringToFileJsonConverter extends StdConverter<String, File> {
    @Override
    public File convert(String string) {
        return Paths.get(string).toFile();
    }
}
