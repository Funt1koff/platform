package com.bonfire.internal.api.extensions;

import com.bonfire.internal.api.models.Initiator;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.internal.CompositeReadableBuffer;
import io.grpc.internal.ReadableBuffers;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;

@UtilityClass
public class InitiatorExtensionHelper {

    private static final Logger log = Logger.getLogger(InitiatorExtensionHelper.class);

    public static <T extends GeneratedMessageV3> Optional<Initiator> getFromMessage(GeneratedMessageV3 protoMessage) {
        final var initiatorExtField = protoMessage.getUnknownFields().getField(InitiatorExtension.INITIATOR_FIELD_NUMBER);
        final var lengthDelimitedList = initiatorExtField.getLengthDelimitedList();
        if (lengthDelimitedList.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(Initiator.parseFrom(lengthDelimitedList.getFirst()));
        } catch (InvalidProtocolBufferException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    @SuppressWarnings("all")
    public static <T extends GeneratedMessageV3> T setToMessage(T protoMessage, Initiator initiator) {
        if (protoMessage == null) {
            return protoMessage;
        }

        final var unknownFields = protoMessage.getUnknownFields();
        final var newUnknownFields = unknownFields.toBuilder()
                .mergeLengthDelimitedField(InitiatorExtension.INITIATOR_FIELD_NUMBER, initiator.toByteString())
                .build();

        return (T) protoMessage.toBuilder().setUnknownFields(newUnknownFields).build();
    }

    public static InputStream setToMessage(InputStream message, Initiator initiator) throws IOException {
        final var compositeReadableBuffer = new CompositeReadableBuffer();
        if (message instanceof io.grpc.HasByteBuffer hbb && hbb.byteBufferSupported()) {
            val bb = hbb.getByteBuffer();
            if (bb != null) {
                compositeReadableBuffer.addBuffer(ReadableBuffers.wrap(bb));
            }
        } else {
            compositeReadableBuffer.addBuffer(ReadableBuffers.wrap(ByteBuffer.wrap(message.readAllBytes())));
        }
        compositeReadableBuffer.addBuffer(ReadableBuffers.wrap(InitiatorExtension.newBuilder().setInitiator(initiator).build().toByteArray()));
        return io.grpc.internal.ReadableBuffers.openStream(compositeReadableBuffer, true);
    }
}
