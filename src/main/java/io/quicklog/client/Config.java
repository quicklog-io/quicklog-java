package io.quicklog.client;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Config {
    final int projectId;
    final String source;
    final String apiKey;
    final String apiUrl;
}
