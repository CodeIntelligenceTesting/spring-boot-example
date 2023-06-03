package com.example.app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class CsvParserTests {
    @Test
    public void testParseCsvData() {
        String csvData = "John,Doe,25\nJane,Smith,30\nTom,Hanks,40";

        CsvParserService csvParserService = new CsvParserService();
        List<String[]> parsedData = csvParserService.parseCsvData(csvData);

        // Assertions
        Assertions.assertEquals(3, parsedData.size());

        String[] firstRow = parsedData.get(0);
        Assertions.assertEquals("John", firstRow[0]);
        Assertions.assertEquals("Doe", firstRow[1]);
        Assertions.assertEquals("25", firstRow[2]);

        String[] secondRow = parsedData.get(1);
        Assertions.assertEquals("Jane", secondRow[0]);
        Assertions.assertEquals("Smith", secondRow[1]);
        Assertions.assertEquals("30", secondRow[2]);

        String[] thirdRow = parsedData.get(2);
        Assertions.assertEquals("Tom", thirdRow[0]);
        Assertions.assertEquals("Hanks", thirdRow[1]);
        Assertions.assertEquals("40", thirdRow[2]);
    }
}