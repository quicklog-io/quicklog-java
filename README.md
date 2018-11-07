quicklog-java
=========

Java client library (SDK) for the Quicklog.io API

## Quick Start

Include the dependency.

```
<dependency>
    <groupId>io.quicklog</groupId>
    <artifactId>quicklog-client</artifactId>
    <version>0.1.0</version>
</dependency>
```

Create a simple test project. Use the sample in [examples/simple](https://github.com/quicklog-io/quicklog-java/tree/master/examples/simple).

Use your own *Project Id* and *API Key* (in place of `12345` and `my-api-key`):

```
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
```

### Build it:

- `cd examples/simple`
- `mvn package`

### Run it:

- `java -jar target/java -jar target/quicklog-client-demo-0.1.0.jar`

It should send a log entry to api.quicklog.io and print `OK: Logged`.

Verify that the entry was logged:

- `curl -si 'https://api.quicklog.io/entries?project_id=12345&api_key=my-api-key'`

Verify that the tag was associated:

- `curl -si 'https://api.quicklog.io/entries?tag=name1:value1&project_id=12345&api_key=my-api-key'`, or
- `curl -si 'https://api.quicklog.io/entries?tag=value1&project_id=12345&api_key=my-api-key'`

## Methods

### QuicklogClient.configure(Config config)

The `configure` method is used to set global settings.
Both `projectId` and `apiKey` are required.
Using a unique `source` value for each service or subsystem will make easier to follow trace logs.
An instance of a QuicklogClient is returned.

### quicklogClient.entry(Instant.now(), "a-type", "object:1", "target:2", extra, traceCtx, tags)

The `entry` method is what sends a log entry to the quicklog server. Parameters:

- `published` the timestamp of the event being sent
- `type` is an event name string, such as a user action or system event
- `object` is a string identifying the primary thing the log is about
- `target` is a string identifying a secondary thing the log is about
- `extra` is a map of application-defined string key/values
- `traceCtx` is a value as `new TraceCtx("actor:"+userId, traceId, parentSpanId);`
- `tags` is a list of tag strings, each of the form 'key:value' or 'value' or ':value:with:three:colons'

### quicklogClient.tag(String traceId, String tag)

The `tag` method is for associating an application defined value (or key:value) with a `traceId`. Normally tags are added at the same time a log entry is created. A given tag only needs to be added once per unique `traceId` by each `source`.

The `tag` parameter is a string of the form 'a value' or 'key:value'. If you want to use a value with no key but the value itself contains a colon (`:`) then you can use the form ':value:containing:colons'

Note that associating a tag with a traceId doesn't create a visible log. It's purpose is to allow searching of logged traces by tags. For instance a tag `order:5678` could mean that the logs for a trace with that tag pertain to a customer's order number 5678.

## Examples

   See the [examples](examples) directory for more.
