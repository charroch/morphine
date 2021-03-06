package novoda.morphine.android;

import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.Map;

/**
 * Utility class that writes trace files
 *
 * @author shaines
 */
public class TraceWriter {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TraceWriter.class);

    /**
     * Writes the specified trace map to the specified output stream
     *
     * @param traces The trace map to write
     * @param os     The output stream to which to write the trace map
     */
    public static void write(Map<String, Trace> traces, OutputStream os) {
        Element root = new Element("trace-file");
        for (String key : traces.keySet()) {
            // Load the trace
            Trace trace = traces.get(key);

            // Build the trace element
            Element traceElement = new Element("trace");
            traceElement.setAttribute("key", key);

            // Build the content of the node
            StringBuilder sb = new StringBuilder();
            for (MethodAction ma : trace.getActions()) {
                sb.append(ma.getTimestamp() + "," + ma.getAction() + "," + ma.getId() + "|");
            }
            sb.deleteCharAt(sb.length() - 1);
            traceElement.addContent(sb.toString());

            // Add the trace element to the document
            root.addContent(traceElement);
        }

        try {
            // Write the trace file to the output stream using JDOM
            XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
            out.output(root, os);
        } catch (Exception e) {
            logger.error("An error occurred while saving trace file: " + e.getMessage(), e);
        }
    }
}