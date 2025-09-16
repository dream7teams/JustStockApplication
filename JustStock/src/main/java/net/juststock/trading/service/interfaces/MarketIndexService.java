package net.juststock.trading.service.interfaces;

import net.juststock.trading.domain.common.signal.MarketIndexData;
import net.juststock.trading.repository.MarketIndexDataRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Random;

@Service
public class MarketIndexService {

    private final MarketIndexDataRepository repo;
    private final Random random = new Random();

    public MarketIndexService(MarketIndexDataRepository repo) {
        this.repo = repo;
    }

    // ✅ Generate mock values (replace with real API later)
    public MarketIndexData fetchLatest(String symbol) {
        double base;
        switch (symbol.toUpperCase()) {
            case "NIFTY50": base = 19800; break;   // Mock range
            case "BANKNIFTY": base = 45000; break; // Mock range
            default: base = 66500; break;          // Sensex default
        }

        double currentValue = base + random.nextDouble() * 300;
        double change = -100 + random.nextDouble() * 200;
        double percentChange = (change / currentValue) * 100;

        MarketIndexData data = new MarketIndexData();
        data.setSymbol(symbol.toUpperCase());
        data.setValue(currentValue);
        data.setChange(change);
        data.setPercentChange(percentChange);
        data.setTimestamp(ZonedDateTime.now());

        return repo.save(data);
    }

    public MarketIndexData getLastSaved(String symbol) {
        return repo.findTopBySymbolOrderByTimestampDesc(symbol.toUpperCase());
    }
}
