package com.bonfire.internal.api.serde.serialize;

import com.bonfire.internal.api.commands.DialogCommand;
import org.apache.kafka.common.serialization.Serializer;

public class DialogCommandSerializer implements Serializer<DialogCommand> {
    @Override
    public byte[] serialize(String s, DialogCommand dialogCommand) {
        return dialogCommand.toByteArray();
    }
}
