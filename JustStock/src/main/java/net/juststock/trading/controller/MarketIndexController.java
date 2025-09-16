package net.juststock.trading.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import net.juststock.trading.domain.common.signal.MarketIndexData;
import net.juststock.trading.service.interfaces.MarketIndexService;

@RestController
@RequestMapping("/api/market")
public class MarketIndexController {

	private final MarketIndexService marketIndexService;

	public MarketIndexController(MarketIndexService marketIndexService) {
		this.marketIndexService = marketIndexService;
	}

	// ✅ Latest values
	@GetMapping("/{symbol}/latest")
	public ResponseEntity<MarketIndexData> getLatest(@PathVariable String symbol) {
		return ResponseEntity.ok(marketIndexService.fetchLatest(symbol));
	}

	// ✅ Last saved values
	@GetMapping("/{symbol}/last")
	public ResponseEntity<MarketIndexData> getLast(@PathVariable String symbol) {
		return ResponseEntity.ok(marketIndexService.getLastSaved(symbol));
	}
}
