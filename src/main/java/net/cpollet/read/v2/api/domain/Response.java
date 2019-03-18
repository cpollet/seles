package net.cpollet.read.v2.api.domain;

import com.google.gson.GsonBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Response<IdType> {
    private final Map<IdType, Map<String, Object>> values;
    private final Collection<String> errors;
    private final Collection<String> messages;
    private final long executionTime;

    public Response(Map<IdType, Map<String, Object>> values, Collection<String> errors, Collection<String> messages, long executionTime) {
        this.values = Collections.unmodifiableMap(values);
        this.errors = Collections.unmodifiableCollection(errors);
        this.messages = Collections.unmodifiableCollection(messages);
        this.executionTime = executionTime;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    @Override
    public String toString() {
        Map<String, Object> map = new HashMap<>();
        map.put("success", !hasErrors());
        map.put("executionTime", executionTime);
        map.put("result", values);
        map.put("messages", messages);
        map.put("errors", errors);

        return new GsonBuilder().setPrettyPrinting().create().toJson(map);
    }
}
