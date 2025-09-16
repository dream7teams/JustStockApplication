package net.juststock.trading.domain.common.signal;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Entity
@Data
public class MarketIndexData {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String symbol; // SENSEX, NIFTY50, BANKNIFTY

	    @Column(name = "index_value")  // ✅ avoid reserved "value"
	    private double value;

	    @Column(name = "price_change")  // ✅ avoid reserved "change"
	    private double change;

	    @Column(name = "percent_change")
	    private double percentChange;

	    @Column(name = "recorded_at")   // ✅ avoid reserved "timestamp"
	    private ZonedDateTime timestamp;
}
