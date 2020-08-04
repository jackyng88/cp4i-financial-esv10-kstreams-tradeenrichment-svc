package com.ibm.garage.cpat.domain;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.eclipse.microprofile.config.inject.ConfigProperty;


import io.quarkus.kafka.client.serialization.JsonbSerde;


@ApplicationScoped
public class TradeEnrichmentTopology {

    @ConfigProperty(name = "START_TOPIC_NAME")
    private String INCOMING_TOPIC;

    @ConfigProperty(name = "TARGET_TOPIC_NAME")
    private String OUTGOING_TOPIC;

    @ConfigProperty(name = "KTABLE_TOPIC_NAME")
    private String KTABLE_TOPIC;

    private static String storeName = "companySectors";

    @Produces
    public Topology buildTopology() {

        StreamsBuilder builder = new StreamsBuilder();
        //KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(storeName);

        JsonbSerde<FinancialMessage> financialMessageSerde = new JsonbSerde<>(FinancialMessage.class);
        JsonbSerde<CompanySectors> companySectorsSerde = new JsonbSerde<>(CompanySectors.class);
        JsonbSerde<EnrichedMessage> enrichedMessageSerde = new JsonbSerde<>(EnrichedMessage.class);

        KStream<String, FinancialMessage> financialMessageStream =
            builder.stream(
                INCOMING_TOPIC,
                Consumed.with(Serdes.String(), financialMessageSerde)
            );

        GlobalKTable<String, CompanySectors> companySectorsStore = 
            builder.globalTable(
                KTABLE_TOPIC,
                Consumed.with(Serdes.String(), companySectorsSerde)
                //Materialized.as(storeSupplier)
        );

        // An enrichedStream is created by chaining the filter, mapValues and lastly the leftJoin operators.
        // What occurs here is it filters out the records that only satisfy [false, false, false, false, true]
        // for the flags, then mapValues changes the trade_enrichment flag to false and finally a leftJoin 
        // is invoked by joining the financialMessageStream with the companySectors GlobalKTable. It does a join
        // on the financialMessage records that make it through and pairs the financialMessageValue.stock_symbol
        // with a matching key in the KTable. When a matched record occurs a new EnrichedMessage is instantioned
        // which is a combination of all these two object's fields.
        KStream<String, EnrichedMessage> enrichedStream = financialMessageStream.filter(
            (key, financialMessage) -> checkTradeEnrichment(financialMessage)
        )
        .mapValues(
            checkedMessage -> performTradeEnrichmentCheck(checkedMessage)
        )
        .leftJoin(
            companySectorsStore,
            (financialMessageKey, financialMessageValue) -> financialMessageValue.stock_symbol,
            (financialMessageValue, companySectorsValue) -> new EnrichedMessage(financialMessageValue, companySectorsValue)
        );

        enrichedStream.to(
            OUTGOING_TOPIC,
            Produced.with(Serdes.String(), enrichedMessageSerde));
        
        return builder.build();
    }

    public boolean checkTradeEnrichment (FinancialMessage rawMessage) {
        // Returns a boolean based on whether all previous flags are false and trade_enrichment is true
        // thus enforcing this check to only happen as the last step when all previous ones are complete.
        return (!rawMessage.compliance_services && !rawMessage.technical_validation
                && !rawMessage.schema_validation && !rawMessage.business_validation
                && rawMessage.trade_enrichment);
    }

    public FinancialMessage performTradeEnrichmentCheck(FinancialMessage checkedMessage) {
        // Perform the "check" and then return the transformed object.
        checkedMessage.trade_enrichment = false;

        return checkedMessage;
    }
}