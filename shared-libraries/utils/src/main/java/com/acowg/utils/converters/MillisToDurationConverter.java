package com.acowg.utils.converters;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.Duration;

public class MillisToDurationConverter extends StdConverter<Long, Duration> {
    @Override
    public Duration convert(Long millis) {
        return Duration.ofMillis(millis);
    }
}
