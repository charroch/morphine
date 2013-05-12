package novoda.morphine.android;

import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all traces
 *
 * @author shaines
 */
public class TraceManager implements Serializable, Runnable {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TraceManager.class);

    private static final long serialVersionUID = 1L;

    /**
     * The instance of the TraceManager
     */
    private static TraceManager instance = new TraceManager();

    /**
     * Maintains all active traces
     */
    private Map<String, Trace> activeTraces = new HashMap<String, Trace>();

    /**
     * Maintains all traces that have been completed
     */
    private Map<String, Trace> completedTraces = new HashMap<String, Trace>();

    /**
     * True if the TraceManager thread is running false otherwise
     */
    private boolean running = false;

    private boolean started = false;

    /**
     * Counter used for ensuring that trace ids are unique
     */
    private long counter = 0;

    /**
     * Singleton accessor method
     *
     * @return Returns the instance of the TraceManager
     */
    public static TraceManager getInstance() {
        return instance;
    }

    /**
     * Called by the Trace class when a trace is started
     *
     * @param trace A reference to the Trace that is starting
     * @return A unique identifier for this trace
     */
    public synchronized String startTrace(Trace trace) {
        // Generate a key
        String key = Long.toString(System.currentTimeMillis()) + "-" + counter++;

        // Put the trace in the active traces map
        activeTraces.put(key, trace);

        // We've received a trace so put the TraceManager into a started state
        if (!started) {
            started = true;
        }

        // Return the key to the caller
        return key;
    }

    /**
     * Signals the end of this trace
     *
     * @param key The key identifying the trace that is ending
     */
    public void endTrace(String key) {
        if (activeTraces.containsKey(key)) {
            // Move the trace to the completed trace map
            completedTraces.put(key, activeTraces.remove(key));
        }
    }

    /**
     * Persists all traces gathered by the TraceManager
     */
    private void saveTraces() {
        if (logger.isInfoEnabled()) {
            logger.info("Saving traces");
        }

        try {
            TraceWriter.write(completedTraces, new FileOutputStream("trace-file.xml"));
        } catch (Exception e) {
            logger.error("An error occurred while saving trace file: " + e.getMessage(), e);
        }

        for (String key : completedTraces.keySet()) {
            Trace trace = completedTraces.get(key);

            StringBuilder sb = new StringBuilder("Trace[").append(key).append("]:\n");
            for (MethodAction ma : trace.getActions()) {
                sb.append("\t" + ma + "\n");
            }
            System.out.println(sb.toString());
        }
    }

    /**
     * Start    the trace manager
     */
    public void startTraceManager() {
        if (logger.isInfoEnabled()) {
            logger.info("Starting TraceManager");
        }

        // Set out state variables
        running = true;
        started = false;

        // Start the TraceManager in its own thread
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * The TraceManager is configured to run in its own thread
     */
    @Override
    public void run() {
        if (logger.isInfoEnabled()) {
            logger.info("TraceManager started");
        }

        // Run while running is true (control to be able to manually stop the TraceManager)
        // and while he have active traces (and we have received at least one so far)
        while ((!started || activeTraces.size() > 0) && running) {
            try {
                // 100ms sleep
                Thread.sleep(100);
            } catch (Exception e) {

            }
        }

        // Save our traces
        saveTraces();

        if (logger.isInfoEnabled()) {
            logger.info("TraceManager shutting down");
        }
    }

    /**
     * Private constructor to ensure singleton pattern
     */
    private TraceManager() {
    }
}