package com.jensreinhart;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.sun.scenario.effect.impl.sw.java.JSWBlend_SRC_OUTPeer;
import org.w3c.dom.ls.LSOutput;

import javax.accessibility.AccessibleValue;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvParser {

    private static final String PAYMENT_EC = "EC";
    private static final String PAYMENT_CASH = "BAR";

    private File csvFile;

    public CsvParser(File csvFile) {
        this.csvFile = csvFile;
    }

    public Order parsEcOrder() throws IOException, CsvValidationException {
        return parsCvs(PAYMENT_EC);
    }

    public Order parsCashOrder() throws IOException, CsvValidationException {
        return parsCvs(PAYMENT_CASH);
    }

    private Order parsCvs(String payment) throws IOException, CsvValidationException {
        List<List<String>> csvContent = new ArrayList<>();
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(csvFile)).withCSVParser(parser).build()) {
             String[] values;
            while ((values = csvReader.readNext()) != null) {
                csvContent.add(Arrays.asList(values));
            }
        }

        // get date from first entry
        String date = csvContent.get(1).get(3).substring(0 ,9).concat("T00:00:00+00:00"); // convert to desired format
        Order order = new Order(date);

        csvContent.stream()
                .skip(1)
                .filter(item -> item.get(10).equals(payment))
                .filter(item -> !item.get(13).isEmpty()) // ignore if article id is empty
                .forEach(item -> order.getOrderItemList().add(processOrderItem(item)));

        return order;
    }

    private OrderItem processOrderItem(List<String> item){

        int articleId;
        String description = item.get(14);
        double quantity;
        double unitPrice;
        double tax = (Double.parseDouble(item.get(28)) / 100); // tax in percent

        /**
         * If the article number starts with an A the unit is half meter and has to be converted in meter
         */
        if (item.get(13).startsWith("A")) {
            articleId = Integer.parseInt(item.get(13).substring(1)); // remove letter A
            quantity = (parseDouble(item.get(18)) / 2 ); // halve quantity
            unitPrice = (parseDouble(item.get(22)) * 2 ); // double price
        } else {
            articleId = Integer.parseInt(item.get(13));
            quantity = parseDouble(item.get(18));
            unitPrice = parseDouble(item.get(22));
        }

        OrderItem orderItem = new OrderItem(
                articleId,
                description,
                quantity,
                unitPrice,
                tax);
        return orderItem;
    }

    private double parseDouble(String str) {
        String withPoint = str.replace(',', '.'); // replace comma with point
        return Double.parseDouble(withPoint); // return as double
    }

}
