package novoda.morphine.android;

import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * A Trace is a collection of method actions
 *
 * @author shaines
 */
public class Trace implements Serializable {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TraceManager.class);

    private static final long serialVersionUID = 1L;
    /**
     * The trace key, which will be assigned by the TraceManager
     */
    private String traceKey;

    /**
     * The ID of the head of the trace, which is used to identify the end of the trace
     */
    private String headId;

    /**
     * The list of MethodActions (START, END, or EXCEPTION) that participated in this trace
     */
    private List<MethodAction> actions = new LinkedList<MethodAction>();

    /**
     * Creates a new Trace
     */
    public Trace() {
    }

    /**
     * Adds a MethodAction to this trace, where a MethodAction may identify the
     * start of a method call, the end of a method call, or an exceptional exit of
     * a method call
     *
     * @param action The type of action: START, END, or EXCEPTION
     * @param id     The method id, which is the fully-qualified method name
     */
    public void addAction(MethodAction.Action action, String id) {
        if (logger.isTraceEnabled()) {
            logger.trace("Add action: [" + action + "] " + id);
        }

        if (actions.size() == 0) {
            // Save the head id of this trace
            headId = id;

            // Start the trace in the TraceManager and save the key
            traceKey = TraceManager.getInstance().startTrace(this);
        }

        // Add the trace to the
        actions.add(new MethodAction(action, id));

        if (action == MethodAction.Action.END && id.equals(headId)) {
            // Mark this trace as complete
            TraceManager.getInstance().endTrace(traceKey);
        }
    }

    /**
     * Returns the list of method actions for this trace
     *
     * @return The list of method starts, ends, and exceptions
     */
    public List<MethodAction> getActions() {
        return actions;
    }

    /**
     * Setter method, used for trace file reading
     *
     * @param actions The actions to set in this Trace
     */
    public void setActions(List<MethodAction> actions) {
        this.actions = actions;
    }

    public String getTraceKey() {
        return traceKey;
    }

    public void setTraceKey(String traceKey) {
        this.traceKey = traceKey;
    }
}
