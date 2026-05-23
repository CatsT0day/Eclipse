package me.catst0day.Eclipse.Exceptions;

/**
 * @deprecated Instead of using this class, use
 * {@link me.catst0day.Eclipse.Utils.Exceptions.CommandRegistryException}
 * This will no longer update, and will be removed in 1.04.00
 */
@Deprecated(since = "1.02.95", forRemoval = true)
public class CommandRegistryException extends RuntimeException {
    /**
     * @deprecated Instead of using this class, use
     * {@link me.catst0day.Eclipse.Utils.Exceptions.CommandRegistryException}
     * This will no longer update, and will be removed in 1.04.00
     */
    public CommandRegistryException(String message, Throwable t) {
        super(message, t);
    }
}
