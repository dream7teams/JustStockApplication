package net.juststock.trading.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import net.juststock.trading.domain.common.signal.MarketIndexData;

public interface MarketIndexDataRepository extends JpaRepository<MarketIndexData, Long> {
   public MarketIndexData findTopBySymbolOrderByTimestampDesc(String symbol);
}
