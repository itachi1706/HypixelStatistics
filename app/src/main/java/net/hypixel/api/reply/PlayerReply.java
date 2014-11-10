package net.hypixel.api.reply;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by AgentK on 10/11/2014, 5:50 PM
 * for Hypixel Statistics in package net.hypixel.api.reply
 */
@SuppressWarnings("unused")
public class PlayerReply extends AbstractReply {
    private JsonElement player;

    public JsonObject getPlayer() {
        if(player.isJsonNull()) {
            return null;
        } else {
            return player.getAsJsonObject();
        }
    }

    @Override
    public String toString() {
        return "PlayerReply{" +
                "player=" + player +
                ",super=" + super.toString() + "}";
    }
}
