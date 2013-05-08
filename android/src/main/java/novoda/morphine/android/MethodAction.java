package novoda.morphine.android;

import java.io.Serializable;

/**
 * Defines a method action, which is either the start or end of a method. It records
 * the timestamp of the action as well as the id, which is the fully qualified method name.
 *
 * @author shaines
 */
public class MethodAction implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Is the method starting, ending, or did it throw an exception?
     */
    public enum Action {
        START, END, EXCEPTION
    }

    ;

    /**
     * The action for this event: START, END, or EXCEPTION
     */
    private Action action;

    /**
     * The id for this event, which should be the fully qualified method name
     */
    private String id;

    /**
     * The timestamp when this event occurred
     */
    private long timestamp;

    /**
     * Creates a new MethodAction
     *
     * @param action The method action: START, END, or EXCEPTION
     * @param id     The id/fully qualified name of the method
     */
    public MethodAction(Action action, String id) {
        timestamp = System.currentTimeMillis();
        this.action = action;
        this.id = id;
    }

    public MethodAction(long timestamp, Action action, String id) {
        this.timestamp = timestamp;
        this.action = action;
        this.id = id;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return timestamp + ": [" + action + "]\t" + id;
    }
}