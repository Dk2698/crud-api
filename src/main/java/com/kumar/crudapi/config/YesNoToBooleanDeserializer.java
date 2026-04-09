package com.kumar.crudapi.config;


import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class YesNoToBooleanDeserializer extends ValueDeserializer<Boolean> {

//    @Override
//    public Boolean deserialize(JsonParser p, DeserializationContext ctxt)
//            throws IOException {
//
//        String value = p.getText();
//
//        if ("YES".equalsIgnoreCase(value)) {
//            return true;
//        } else if ("NO".equalsIgnoreCase(value)) {
//            return false;
//        }
//
//        return false;
//    }

    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws tools.jackson.core.JacksonException {
        String value = p.getText();

        if ("YES".equalsIgnoreCase(value)) {
            return true;
        } else if ("NO".equalsIgnoreCase(value)) {
            return false;
        }

        return false;
    }
}