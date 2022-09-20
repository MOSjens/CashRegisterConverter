package com.jensreinhart;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;

/**
 * Unit test for simple App.
 */
public class XmlCreatorTest
{
    /**
     * test with german order
     */
    @Test
    public void testXmlCreatorGerman() throws Exception {
        Order order = new Order("30-01-2021T00:00:00+00:00", true);

        // Add 6 items
        for (int i = 1; i <= 6; i++) {
            OrderItem orderItem = new OrderItem(1000 + i, "test article " + i, i, 10.00 * i, 0.19);
            order.getOrderItemList().add(orderItem);
        }


        XmlCreator.saveOrderToXml(order, "TestGerman");

        File f = new File("LaxwareImportTestGerman.xml");
        assertTrue(f.exists());
    }

    /**
     * test with france order
     */
    @Test
    public void testXmlCreatorFrance() throws Exception {
        Order order = new Order("30-01-2021T00:00:00+00:00", false);

        // Add 6 items
        for (int i = 1; i <= 6; i++) {
            OrderItem orderItem = new OrderItem(1000 + i, "test article " + i, i, 10.00 * i, 0.19);
            order.getOrderItemList().add(orderItem);
        }


        XmlCreator.saveOrderToXml(order, "TestFrance");

        File f = new File("LaxwareImportTestFrance.xml");
        assertTrue(f.exists());
    }
}
