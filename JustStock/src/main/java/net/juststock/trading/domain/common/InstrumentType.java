package net.juststock.trading.domain.common;

import java.util.HashMap;
import java.util.Map;

public enum InstrumentType {
    NIFTY,
    BANKNIFTY,
    SENSEX,
    STOCK,
    COMMODITY;

    private static final Map<String, InstrumentType> LOOKUP = new HashMap<>();

    static {
        // NIFTY
        alias("NIFTY", NIFTY);
        alias("NIFTY50", NIFTY);
        alias("CNX_NIFTY", NIFTY);
        alias("CNXNIFTY", NIFTY);
        alias("NSE_NIFTY", NIFTY);

        // BANKNIFTY
        alias("BANKNIFTY", BANKNIFTY);
        alias("NIFTYBANK", BANKNIFTY);
        alias("BANK_NIFTY", BANKNIFTY);

        // SENSEX
        alias("SENSEX", SENSEX);
        alias("BSESENSEX", SENSEX);
        alias("BSE_SENSEX", SENSEX);

        // STOCK (a.k.a. Cash / Equity)
        alias("STOCK", STOCK);
        alias("EQUITY", STOCK);
        alias("CASH", STOCK);

        // COMMODITY
        alias("COMMODITY", COMMODITY);
        alias("MCX", COMMODITY);
    }

    private static void alias(String key, InstrumentType type) {
        LOOKUP.put(normalize(key), type);
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
    }

    /** Accept common aliases like "NIFTY50" and return the canonical enum. */
    public static InstrumentType parse(String input) {
        InstrumentType t = LOOKUP.get(normalize(input));
        if (t == null) throw new IllegalArgumentException("Unsupported instrument: " + input);
        return t;
    }
}
