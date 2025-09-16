package net.juststock.trading.service.impl;

import org.springframework.stereotype.Service;

import net.juststock.trading.service.interfaces.OtpService;
import net.juststock.trading.service.verify.TwilioVerifyClient;
import net.juststock.trading.util.PhoneNormalizer;

@Service
public class OtpServiceImpl implements OtpService {

    private final TwilioVerifyClient verifyClient;

    public OtpServiceImpl(TwilioVerifyClient verifyClient) {
        this.verifyClient = verifyClient;
    }

    @Override
    public void requestOtp(String phoneE164) {
        // Normalize to E.164 and delegate OTP to Twilio Verify
        String normalized = PhoneNormalizer.normalizeE164Loose(phoneE164);
        verifyClient.sendVerification(normalized);
    }

    @Override
    public boolean verifyOtp(String phoneE164, String code) {
        // Normalize to E.164 and validate code against Twilio Verify
        String normalized = PhoneNormalizer.normalizeE164Loose(phoneE164);
        return verifyClient.checkVerification(normalized, code);
    }

    @Override
    public void resendOtp(String phoneE164) {
        // Twilio Verify: calling start again resends a new code (rate limited)
        String normalized = PhoneNormalizer.normalizeE164Loose(phoneE164);
        verifyClient.sendVerification(normalized);
    }
}
