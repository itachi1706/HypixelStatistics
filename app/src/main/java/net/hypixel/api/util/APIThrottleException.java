package net.hypixel.api.util;

/**
 * Created by AgentK on 10/11/2014, 5:54 PM
 * for Hypixel Statistics in package net.hypixel.api.util
 */
@SuppressWarnings("unused")
public class APIThrottleException extends HypixelAPIException {
    public APIThrottleException() {
        super("You have passed the API throttle limit!");
    }
}
