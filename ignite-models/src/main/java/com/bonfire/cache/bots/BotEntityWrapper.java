package com.bonfire.cache.bots;

import com.bonfire.cache.BotEntity;
import com.bonfire.cache.helpers.ProtoBinaryWrapper;
import com.google.protobuf.Parser;

public class BotEntityWrapper extends ProtoBinaryWrapper<BotEntity> {
    public BotEntityWrapper() {

    }

    public BotEntityWrapper(BotEntity entity) {
        super(entity);
    }

    @Override
    public Parser<BotEntity> parser() {
        return BotEntity.parser();
    }
}
