package com.example.app.service;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvParserService {

    public List<String[]> parseCsvData(String csvData) {
        List<String[]> parsedData = new ArrayList<>();

        try (StringReader stringReader = new StringReader(csvData);
             CSVReader csvReader = new CSVReaderBuilder(stringReader)
                     .withCSVParser(buildCsvParser())
                     .build()) {

            parsedData = csvReader.readAll();
        } catch (Exception ignored) {}

        return parsedData;
    }

    public static String serviceName() {
        return "CSV parsing service";
    }

    private CSVParser buildCsvParser() {
        return new CSVParserBuilder()
                .withSeparator(',')
                .withQuoteChar('"')
                .withEscapeChar('\\')
                .withStrictQuotes(true)
                .build();
    }
}
