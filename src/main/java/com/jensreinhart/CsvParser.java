package com.jensreinhart;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvParser {

    private static final String PAYMENT_EC = "EC";
    private static final String PAYMENT_CARD = "Karte"; // treat same as EC
    private static final String PAYMENT_CASH = "BAR";
    private static final String CANCELLATION = "ja";
    private static final String TAX_MODE_FRANCE = "TVA francaise";

    // After the france update all columns after the date column are +1
    // In the version 1.2 update there were also column number changes
    private static final int DATE_COLUMN = 3;
    private static final int TAX_MODE_COLUMN = 4;
    private static final int PAYMENT_COLUMN = 11;
    private static final int ARTICLE_ID_COLUMN = 14;
    private static final int DESCRIPTION_COLUMN = 15;
    private static final int QUANTITY_COLUMN = 21;
    private static final int CANCELLATION_COLUMN = 23;
    private static final int PRICE_COLUMN = 25;
    private static final int TAX_COLUMN = 33;

    private File csvFile;

    public CsvParser(File csvFile) {
        this.csvFile = csvFile;
    }

    public Order parsEcOrder() throws IOException, CsvValidationException {
        // Since update 1.2 there are multiple payment methods possible
        Order ecOrder = parsCvs(PAYMENT_EC);
        Order cardOrder = parsCvs(PAYMENT_CARD);
        ecOrder.getOrderItemList().addAll(cardOrder.getOrderItemList());
        return ecOrder;
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

        // get date and the country from first row
        String date = csvContent.get(1).get(DATE_COLUMN).substring(0 ,10).concat("T00:00:00+00:00"); // convert to desired format
        String taxMode = csvContent.get(1).get(TAX_MODE_COLUMN);
        Order order = new Order(date, !taxMode.equals(TAX_MODE_FRANCE));

        csvContent.stream()
                .skip(1)
                .filter(item -> item.get(PAYMENT_COLUMN).equals(payment))
                .filter(item -> !item.get(CANCELLATION_COLUMN).equalsIgnoreCase(CANCELLATION)) // ignore canceled orders
                .filter(item -> !item.get(ARTICLE_ID_COLUMN).isEmpty()) // ignore if article id is empty
                .forEach(item -> order.getOrderItemList().add(processOrderItem(item)));

        return order;
    }

    private OrderItem processOrderItem(List<String> item){

        int articleId;
        String description = item.get(DESCRIPTION_COLUMN);
        double quantity;
        double unitPrice;
        double tax = (Double.parseDouble(item.get(TAX_COLUMN)) / 100); // tax in percent

        /**
         * If the article number starts with an A the unit is half meter and has to be converted in meter
         */
        if (item.get(ARTICLE_ID_COLUMN).startsWith("A")) {
            articleId = Integer.parseInt(item.get(ARTICLE_ID_COLUMN).substring(1)); // remove letter A
            quantity = (parseDouble(item.get(QUANTITY_COLUMN)) / 2 ); // halve quantity
            unitPrice = (parseDouble(item.get(PRICE_COLUMN)) * 2 ); // double price
        } else {
            articleId = Integer.parseInt(item.get(ARTICLE_ID_COLUMN));
            quantity = parseDouble(item.get(QUANTITY_COLUMN));
            unitPrice = parseDouble(item.get(PRICE_COLUMN));
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
