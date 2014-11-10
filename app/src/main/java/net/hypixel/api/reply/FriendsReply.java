package net.hypixel.api.reply;

import com.google.gson.JsonArray;

/**
 * Created by AgentK on 10/11/2014, 5:50 PM
 * for Hypixel Statistics in package net.hypixel.api.reply
 */
@SuppressWarnings("unused")
public class FriendsReply extends AbstractReply {
    private JsonArray records;

    public JsonArray getRecords() {
        return records;
    }

    @Override
    public String toString() {
        return "FriendsReply{" +
                "records=" + records +
                ",super=" + super.toString() + "}";
    }
}
