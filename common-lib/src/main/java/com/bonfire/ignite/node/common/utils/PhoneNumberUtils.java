package com.bonfire.ignite.node.common.utils;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PhoneNumberUtils {

    private static final String DEFAULT_REGION = "RU";
    private static final String INVALID_NUMBER_ERR = "The provided phoneNumber %s is not valid; should be in format +7xxxxxxxxxx";

    private final PhoneNumberUtil util = PhoneNumberUtil.getInstance();

    public String normalize(String input) {
        return normalize(input, DEFAULT_REGION);
    }

    public String normalize(String input, String region) {
        try {
            Phonenumber.PhoneNumber number = util.parse(input, region);
            if (util.isValidNumber(number)) {
                return util.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
            } else {
                throw new IllegalArgumentException(String.format(INVALID_NUMBER_ERR, input));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format(INVALID_NUMBER_ERR, input), e);
        }
    }
}
