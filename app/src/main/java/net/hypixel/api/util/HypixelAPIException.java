package net.hypixel.api.util;

/**
 * Created by AgentK on 10/11/2014, 5:52 PM
 * for Hypixel Statistics in package net.hypixel.api.util
 */
@SuppressWarnings("unused")
public class HypixelAPIException extends RuntimeException {

    public HypixelAPIException() {
    }

    public HypixelAPIException(String message) {
        super(message);
    }

    public HypixelAPIException(String message, Throwable cause) {
        super(message, cause);
    }

    public HypixelAPIException(Throwable cause) {
        super(cause);
    }

    //Removed as somehow it cant do this
    /*
    public HypixelAPIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }*/
}
