package com.acowg.utils.converters;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.Duration;

public class DurationToMillsConverter extends StdConverter<Duration, Long> {
    @Override
    public Long convert(Duration duration) {
        return duration.toMillis();
    }
}
