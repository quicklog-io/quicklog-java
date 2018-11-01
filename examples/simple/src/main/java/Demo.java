import io.quicklog.client.Config;
import io.quicklog.client.QuicklogClient;
import io.quicklog.client.TraceCtx;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo {
	public static void main(String[] args) {
		QuicklogClient quicklog = QuicklogClient.configure(Config.builder()
		.projectId(12345)
		.apiKey("my-api-key")
		.source("my-program")
		.build());

		TraceCtx traceCtx = new TraceCtx("user:me", null, null);

		Map<String, Object> extra = new HashMap<>();
		extra.put("key", "value");

		List<String> tags = Arrays.asList("name1:value1", "value", "name1:value:with:colons", ":value:with:colons");

		try {
			quicklog.entry(Instant.now(), "a-type", "object:1", "target:2", extra, traceCtx, tags);
			System.out.println("OK: Logged.");
            System.exit(0);
		} catch (Exception e) {
			System.out.println("Error: " + e.toString());
            System.exit(1);
		}
	}
}
