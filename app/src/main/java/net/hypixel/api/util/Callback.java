package net.hypixel.api.util;

/**
 * Created by AgentK on 10/11/2014, 5:53 PM
 * for Hypixel Statistics in package net.hypixel.api.util
 */
@SuppressWarnings("unused")
public abstract class Callback<T> {
    private final Class<T> clazz;

    public Callback(Class<T> clazz) {
        this.clazz = clazz;
    }

    public abstract void callback(Throwable failCause, T result);

    public final Class<T> getClazz() {
        return clazz;
    }
}
