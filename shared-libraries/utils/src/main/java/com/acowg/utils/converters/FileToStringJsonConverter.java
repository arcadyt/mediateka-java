package com.acowg.utils.converters;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.io.File;

public class FileToStringJsonConverter extends StdConverter<File, String> {
    @Override
    public String convert(File file) {
        return file.toString();
    }
}
