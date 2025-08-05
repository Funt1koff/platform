package com.bonfire.ignite;

import com.bonfire.cache.SequenceNames;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.client.ClientAtomicConfiguration;
import org.apache.ignite.client.ClientAtomicLong;
import org.apache.ignite.client.IgniteClient;

public class Sequences {

    public static final class IdSequences {
        private final ClientAtomicLong atomicLong;

        private IdSequences(ClientAtomicLong atomicLong) {
            this.atomicLong = atomicLong;
        }

        public long getAndIncrement() {
            return atomicLong.getAndIncrement();
        }
    }

    public static IdSequences userIdSequence(IgniteClient ignite) {
        return new IdSequences(ignite.atomicLong(SequenceNames.USER_ID_SEQ_NAME,
                new ClientAtomicConfiguration().setCacheMode(CacheMode.REPLICATED).setAtomicSequenceReserveSize(10),
                1,
                true));
    }

    public static IdSequences groupIdSequence(IgniteClient ignite) {
        return new IdSequences(ignite.atomicLong(SequenceNames.GROUP_ID_SEQ_NAME,
                new ClientAtomicConfiguration().setCacheMode(CacheMode.REPLICATED).setAtomicSequenceReserveSize(10),
                1,
                true));
    }

    public static IdSequences botIdSequence(IgniteClient ignite) {
        return new IdSequences(ignite.atomicLong(SequenceNames.BOT_ID_SEQ_NAME,
                new ClientAtomicConfiguration().setCacheMode(CacheMode.REPLICATED).setAtomicSequenceReserveSize(10),
                1,
                true));
    }
}
