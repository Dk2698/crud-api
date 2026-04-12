package com.kumar.crudapi.messaging.serializer;


import tools.jackson.databind.ObjectMapper;

public class JsonSerializer {

    private final ObjectMapper objectMapper;

    public JsonSerializer() {
        this.objectMapper = new ObjectMapper();
//        this.objectMapper.findAndRegisterModules();
//        this.objectMapper.(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    // ================= SERIALIZE =================

    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    public byte[] toBytes(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON byte serialization failed", e);
        }
    }

    // ================= DESERIALIZE =================

    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialization failed", e);
        }
    }

    public <T> T fromBytes(byte[] data, Class<T> clazz) {
        try {
            return objectMapper.readValue(data, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON byte deserialization failed", e);
        }
    }

    public <T> T convert(Object payload, Class<T> type) {

        if (payload == null) return null;

        if (type.isInstance(payload)) {
            return type.cast(payload);
        }

        return objectMapper.convertValue(payload, type);
    }
}