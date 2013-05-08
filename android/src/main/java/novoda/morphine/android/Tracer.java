package novoda.morphine.android;

public class Tracer extends ThreadLocal<Trace> {

    private static Tracer instance = new Tracer();

    public static Tracer getInstance() {
        return instance;
    }

    private Tracer() {
    }

    @Override
    public Trace initialValue() {
        return new Trace();
    }
}