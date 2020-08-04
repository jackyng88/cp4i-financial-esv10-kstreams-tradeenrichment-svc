package com.ibm.garage.cpat.infrastructure;

import com.ibm.garage.cpat.domain.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;


@ApplicationScoped
public class MockCompanySectorProducer {
    
    private static final Logger LOG = Logger.getLogger(MockCompanySectorProducer.class);

    public List<CompanySectors> companies = Collections.unmodifiableList(
        Arrays.asList(
            new CompanySectors("1", "ABBOTT LABORATORIES", "ABT", "Health Care"),
            new CompanySectors("2", "ACCENTURE PLC", "ACN", "Miscellaneous"),
            new CompanySectors("3", "ALIBABA GROUP HOLDING LTD", "BABA", "Miscellaneous"),
            new CompanySectors("4", "ALTRIA GROUP INC", "MO", "Consumer Non-Durables"),
            new CompanySectors("5", "AMERICAN EXPRESS CO", "AXP", "Finance"),
            new CompanySectors("6", "AMERICAN TOWER CORP", "AMT", "Consumer Services"),
            new CompanySectors("7", "AT&T INC", "T", "Public Utilities"),
            new CompanySectors("8", "CANADIAN NATIONAL RAILWAY CO", "CNI", "Transportation"),
            new CompanySectors("9", "CATERPILLAR INC", "CAT", "Capital Goods"),
            new CompanySectors("10", "CHINA PETROLEUM CHEMICAL CORP", "SNP", "Energy")
        )
    );

    @Outgoing("companies-topic")
    public Flowable<KafkaRecord<String, String>> sendCompaniesToTopic() {
        List<KafkaRecord<String, String>> companiesAsJson = companies.stream()
            .map(companyEntry -> KafkaRecord.of(
                    //companyEntry.company_id,
                    companyEntry.tckr,
                    "{ \"company_id\" : \"" + companyEntry.company_id +
                    "\", \"company_name\" : \"" + companyEntry.company_name +
                    "\", \"tckr\" : \"" + companyEntry.tckr +
                    "\", \"sector_cd\" : \"" + companyEntry.sector_cd + "\" }"))
            .collect(Collectors.toList());

        return Flowable.fromIterable(companiesAsJson);
    }
}