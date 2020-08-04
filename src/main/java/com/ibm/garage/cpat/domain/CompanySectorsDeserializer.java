package com.ibm.garage.cpat.domain;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;


public class CompanySectorsDeserializer extends ObjectMapperDeserializer<CompanySectors>{
    public CompanySectorsDeserializer() {
        // pass the class to the parent.
        super(CompanySectors.class);
    }
}