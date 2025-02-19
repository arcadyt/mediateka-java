package com.acowg.shared.models.enums;

import java.util.Optional;

public enum CategoryType {
    TV,
    MOVIES,
    TUTORIALS,
    WORKOUTS,
    XRATED;

    public static Optional<CategoryType> tryParse(String name) {
        try {
            return Optional.of(CategoryType.valueOf(name));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }
}
