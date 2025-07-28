package com.bonfire.cache;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShortnameEntity extends BaseEntity {
    private final ShortnameEntity.Type type;

    public ShortnameEntity(Type type, long value) {
        super(value);
        this.type = type;
    }

    public enum Type {
        USER, GROUP, BOT
    }

    public Optional<Long> getUserId() {
        return type == ShortnameEntity.Type.USER ? Optional.of(getValue()) : Optional.empty();
    }

    public Optional<Long> getGroupId() {
        return type == ShortnameEntity.Type.GROUP ? Optional.of(getValue()) : Optional.empty();
    }

    public Optional<Long> getBotId() {
        return type == ShortnameEntity.Type.BOT ? Optional.of(getValue()) : Optional.empty();
    }

}
