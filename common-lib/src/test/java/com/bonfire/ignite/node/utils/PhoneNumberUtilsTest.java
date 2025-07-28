package com.bonfire.ignite.node.utils;

import com.bonfire.ignite.node.common.utils.PhoneNumberUtils;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Log
public class PhoneNumberUtilsTest {

    @Test
    public void testPhoneNumberNormalize() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> PhoneNumberUtils.normalize("+71239999999"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> PhoneNumberUtils.normalize("+61239999999"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> PhoneNumberUtils.normalize("+790499999999"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> PhoneNumberUtils.normalize("+7904999999"));
        Assertions.assertEquals("+79999999999", PhoneNumberUtils.normalize("9999999999"));
        Assertions.assertEquals("+79049999999", PhoneNumberUtils.normalize("9049999999"));
        Assertions.assertEquals("+79049999999", PhoneNumberUtils.normalize("79049999999"));
        Assertions.assertEquals("+79049999999", PhoneNumberUtils.normalize("+7 904 9999999"));
        Assertions.assertEquals("+79049999999", PhoneNumberUtils.normalize("+7   904 99 99999"));
        Assertions.assertEquals("+79049999999", PhoneNumberUtils.normalize("8 904 9999999"));
        Assertions.assertEquals("+79049999999", PhoneNumberUtils.normalize("8 (904) 9999999"));
        Assertions.assertEquals("+79049999999", PhoneNumberUtils.normalize("8 904 999 9 9 9 9"));
        Assertions.assertEquals("+79049999999", PhoneNumberUtils.normalize("8 904 999-99-99"));
        Assertions.assertEquals("+919999999999", PhoneNumberUtils.normalize("91 99 9999 9999", "IN"));
    }
}
