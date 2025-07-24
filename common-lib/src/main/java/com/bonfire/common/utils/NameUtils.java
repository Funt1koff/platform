package com.bonfire.common.utils;

import io.micrometer.common.util.StringUtils;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NameUtils {

    private static final int MIN_NAME_LENGTH = 1;
    private static final int MAX_NAME_LENGTH = 64;

    public void validate(String input) {
        if(StringUtils.isEmpty(input) || input.length() < MIN_NAME_LENGTH || input.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(STR."Name should be more than \{MIN_NAME_LENGTH} and less, \{MAX_NAME_LENGTH}");
        }
    }
}
