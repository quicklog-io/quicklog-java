package io.quicklog.client;

import java.util.Random;

public class TraceCtx {
    private static final Random rand = new Random();

	public final String actor;
    public final String traceId;
    public final String parentSpanId;
    public final String spanId;

    public TraceCtx(String actor, String traceId, String parentSpanId) {
        String spanId = generateId();
        if (traceId == null || traceId.isEmpty()) {
            traceId = spanId;
            parentSpanId = null;
        }
        this.actor = actor;
        this.traceId = traceId;
        this.parentSpanId = parentSpanId;
        this.spanId = spanId;
    }

    public static String generateId() {
        return Long.toHexString(rand.nextLong());
    }
}
