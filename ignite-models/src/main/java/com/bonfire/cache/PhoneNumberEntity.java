package com.bonfire.cache;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
public class PhoneNumberEntity extends BaseEntity {
    public PhoneNumberEntity(int value) {
        super(value);
    }

    public Optional<Long> getUserId() {
        return Optional.of(getValue());
    }
}
