package be.abis.exercise.model;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;


public class ConversionResult {

	@JsonFormat(pattern="EEE, dd MMM yyyy HH:mm:ss Z")
	private LocalDateTime time_last_update_utc;

	/*private String time_last_update_utc;*/
	private Map<String,Double> rates;

	public LocalDateTime getTime_last_update_utc() {
		return time_last_update_utc;
	}

	public void setTime_last_update_utc(LocalDateTime time_last_update_utc) {
		this.time_last_update_utc = time_last_update_utc;
	}

	/*public String getTime_last_update_utc() {
		return time_last_update_utc;
	}

	public void setTime_last_update_utc(String time_last_update_utc) {
		this.time_last_update_utc = time_last_update_utc;
	}*/

	public Map<String, Double> getRates() {
		return rates;
	}

	public void setRates(Map<String, Double> rates) {
		this.rates = rates;
	}
}
