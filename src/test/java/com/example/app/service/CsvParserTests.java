package com.example.app.service;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import org.junit.jupiter.api.Test;

public class CsvParserTests {
    @Test
    public void unitTestHelloHacker() throws Exception {
    }

    @FuzzTest
    public void fuzzTestHello(FuzzedDataProvider data) {
        CsvParserService service = new CsvParserService();
        service.parseCsvData(data.consumeRemainingAsString());
    }

}
