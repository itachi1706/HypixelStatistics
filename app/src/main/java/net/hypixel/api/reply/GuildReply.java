package net.hypixel.api.reply;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by AgentK on 10/11/2014, 5:50 PM
 * for Hypixel Statistics in package net.hypixel.api.reply
 */
@SuppressWarnings("unused")
public class GuildReply extends AbstractReply {
    private JsonElement guild;

    /**
     * @return The guild object, or null if one wasn't found
     */
    public JsonObject getGuild() {
        if(guild.isJsonNull()) {
            return null;
        } else {
            return guild.getAsJsonObject();
        }
    }

    @Override
    public String toString() {
        return "GuildReply{" +
                "guild=" + guild +
                ",super=" + super.toString() + "}";
    }
}
