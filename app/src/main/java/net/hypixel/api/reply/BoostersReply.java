package net.hypixel.api.reply;

/**
 * Created by AgentK on 12/11/2014, 11:56 AM
 * for Hypixel Statistics in package net.hypixel.api.reply
 */

import com.google.gson.JsonArray;

@SuppressWarnings("unused")
public class BoostersReply extends AbstractReply {
    private JsonArray records;

    public JsonArray getRecords() {
        return records;
    }

    @Override
    public String toString() {
        return "BoostersReply{" +
                "records=" + records +
                ",super=" + super.toString() + "}";
    }
}

