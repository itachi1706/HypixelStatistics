package net.hypixel.api.reply;

import java.util.UUID;

/**
 * Created by AgentK on 10/11/2014, 5:50 PM
 * for Hypixel Statistics in package net.hypixel.api.reply
 */
@SuppressWarnings("unused")
public class KeyReply extends AbstractReply {
    private Key record;

    public Key getRecord() {
        return record;
    }

    @Override
    public String toString() {
        return "KeyReply{" +
                "record=" + record +
                ",super=" + super.toString() + "}";
    }

    public class Key {
        private String key;
        private String owner;
        private int queriesInPastMin;

        public UUID getKey() {
            return UUID.fromString(key);
        }

        public String getOwner() {
            return owner;
        }

        public int getQueriesInPastMin() {
            return queriesInPastMin;
        }

        @Override
        public String toString() {
            return "Key{" +
                    "key='" + key + '\'' +
                    ", owner='" + owner + '\'' +
                    ", queriesInPastMin=" + queriesInPastMin +
                    '}';
        }
    }
}
