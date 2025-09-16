package net.juststock.trading.service.interfaces;

public interface OtpService {
    void requestOtp(String phoneE164);
    boolean verifyOtp(String phoneE164, String code);
    void resendOtp(String phoneE164);
}
