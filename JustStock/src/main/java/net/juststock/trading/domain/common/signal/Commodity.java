package net.juststock.trading.domain.common.signal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Commodity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name; // e.g., Gold, Crude Oil
	private String symbol; // e.g., XAUUSD, CL
	private Double price; // Current price
	private Double changePercent; // Daily % change
	private Long volume; // Trading volume
}