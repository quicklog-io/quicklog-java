# quicklog-java examples/simple

To build and run the simple example:

1. `git clone https://github.com/quicklog-io/quicklog-java.git`
2. `cd quicklog-java/examples/simple`
3. Edit `Demo.java` replacing `12345` and `my-api-key` with your assigned Project ID and API Key.
4. `mvn clean package`
5. `java -jar target/quicklog-client-demo-1.0.0.jar`

To verify that it logged an entry:

- `curl -si 'https://api.quicklog.io/entries?project_id=12345&api_key=my-api-key'

Verify that the tag was associated:

- `curl -si 'https://api.quicklog.io/entries?tag=name1:value1&project_id=12345&api_key=my-api-key', or
- `curl -si 'https://api.quicklog.io/entries?tag=value1&project_id=12345&api_key=my-api-key'
