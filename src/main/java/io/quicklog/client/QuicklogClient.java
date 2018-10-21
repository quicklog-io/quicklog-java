package io.quicklog.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.*;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class QuicklogClient {
	private final Config config;
	private final ObjectMapper objectMapper;
	private final MediaType applicationJson;
	private final OkHttpClient okHttpClient;

    /**
     *
     * @param config
     * @return the configured QuicklogClient
     */
	public static QuicklogClient configure(Config config) {
        ObjectMapper objectMapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MediaType applicationJson = MediaType.get("application/json");

		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.connectionPool(new ConnectionPool(5, 30, TimeUnit.SECONDS))
				.connectTimeout(1_000, TimeUnit.MILLISECONDS)
				.writeTimeout(1_000, TimeUnit.MILLISECONDS)
				.readTimeout(1_000, TimeUnit.MILLISECONDS)
				.build();

		QuicklogClient client = new QuicklogClient(config, objectMapper, applicationJson, okHttpClient);
		return client;
	}

	private QuicklogClient(Config config, ObjectMapper objectMapper, MediaType applicationJson, OkHttpClient okHttpClient) {
	    if (config.getApiUrl() != null) {
            this.config = config;
        } else {
		    this.config = new Config(config.getProjectId(), config.getSource(), config.getApiKey(), "https://api.quicklog.io");
        }
		this.objectMapper = objectMapper;
		this.applicationJson = applicationJson;
		this.okHttpClient = okHttpClient;
	}

    /**
     *
     * @param published
     * @param type
     * @param object
     * @param target
     * @param context
     * @param traceCtx
     * @param tags
     * @return the HTTP Status Code
     * @throws IOException
     */
	public int entry(Instant published, String type, String object, String target, Map<String, Object> context, TraceCtx traceCtx, List<String> tags) throws IOException {
        String url = config.getApiUrl() + "/entries?api_key=" + config.getApiKey();

        Map<String, Object> entryBody = new HashMap<>(11);
        entryBody.put("project_id", config.getProjectId());
        entryBody.put("published", published);
        entryBody.put("source", config.getSource());
        entryBody.put("actor", traceCtx.actor);
        entryBody.put("type", type);
        entryBody.put("object", object);
        entryBody.put("target", target);
        entryBody.put("context", context);
        entryBody.put("trace_id", traceCtx.traceId);
        entryBody.put("parent_span_id", traceCtx.parentSpanId);
        entryBody.put("span_id", traceCtx.spanId);
        byte[] content = objectMapper.writeValueAsBytes(entryBody);

		Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(applicationJson, content))
				.build();
		Response response = okHttpClient.newCall(request).execute();
		String body = response.body().string();

		for (String tag : tags) {
			tag(traceCtx.traceId, tag);
		}

		return response.code();
	}

    /**
     *
     * @param published
     * @param actor
     * @param object
     * @param target
     * @param context
     * @param traceCtx
     * @param tags
     * @return the HTTP Status Code
     * @throws IOException
     */
	public int entry(Instant published, String actor, String object, String target, Map<String, Object> context, TraceCtx traceCtx, String... tags) throws IOException {
		return entry(published, actor, object, target, context, traceCtx, asList(tags));
	}

    /**
     *
     * @param traceId
     * @param tag
     * @return
     * @throws IOException
     */
	public int tag(String traceId, String tag) throws IOException {
		String url = config.getApiUrl() + "/tags?api_key=" + config.getApiKey();

		Map<String, Object> tagBody = new HashMap<>(3);
		tagBody.put("project_id", config.getProjectId());
		tagBody.put("trace_id", traceId);
		tagBody.put("tag", tag);

		byte[] content = objectMapper.writeValueAsBytes(tagBody);

		Request request = new Request.Builder()
				.url(url)
				.post(RequestBody.create(applicationJson, content))
				.build();
		Response response = okHttpClient.newCall(request).execute();
		String body = response.body().string();

		return response.code();
	}
}
