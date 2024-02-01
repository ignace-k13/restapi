package be.abis.exercise.service;

import be.abis.exercise.model.ConversionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class CurrencyServiceImpl implements CurrencyService {

	@Autowired
	private RestTemplate rt;

	private String baseUri = "https://open.er-api.com/v6/latest";

	@Override
	public double getExchangeRate(String fromCur, String toCur) {
	    ConversionResult r = rt.getForObject(baseUri+"/"+fromCur.toUpperCase(),ConversionResult.class);
		System.out.println("Date: " + r.getTime_last_update_utc());
		Map<String,Double> rates = r.getRates();
		double d = rates.get(toCur.toUpperCase());
		return d;
	}

}
