package be.abis.exercise;

import be.abis.exercise.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class CurrencyAPITest {

    @Autowired
    CurrencyService cs;

    @Test
    public void testGetRate() {
        String fromCur="EUR";
        String toCur="JPY";
        double d = cs.getExchangeRate(fromCur,toCur);
        System.out.println("Rate from " + fromCur + " to "+ toCur+  " is " + d);

        assertEquals(1,cs.getExchangeRate(fromCur,fromCur));
    }
}
