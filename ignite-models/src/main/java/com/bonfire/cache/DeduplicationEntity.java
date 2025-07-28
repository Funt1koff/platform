package com.bonfire.cache;

import com.bonfire.internal.api.models.Initiator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;


@Data
@RequiredArgsConstructor
public class DeduplicationEntity {
    private final Type type;
    private final long value;

    public enum Type {
        USER, GROUP, MESSAGE, BOT
    }

    @Data
    @RequiredArgsConstructor
    @FieldNameConstants
    public static class KeyV2 {
        @AffinityKeyMapped
        private final long initiator_session_id;
        private final long deduplicationId;

        public static KeyV2 fromInitiator(Initiator initiator, long deduplicationId) {
            return switch (initiator.getCallerCase()) {
                case USER: {
                    yield new KeyV2(initiator.getUser().getSessionId().hashCode(), deduplicationId);
                }
                case BOT: {
                    yield new KeyV2(initiator.getBot().getBotId(), deduplicationId);
                }
                case SYSTEM: {
                    yield new KeyV2(initiator.getSystem().getSubmoduleValue(), deduplicationId);
                }
                case CALLER_NOT_SET: {
                    yield new KeyV2(0, deduplicationId);
                }
            };
        }
    }
}
