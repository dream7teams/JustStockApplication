package net.juststock.trading.service.sms;

public interface SmsSender {
    void send(String toE164, String message);
}

