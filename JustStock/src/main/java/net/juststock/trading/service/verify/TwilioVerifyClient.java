package net.juststock.trading.service.verify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;

@Service
public class TwilioVerifyClient {
    private static final Logger log = LoggerFactory.getLogger(TwilioVerifyClient.class);

    private final String accountSid;
    private final String authToken;
    private final String verifyServiceSid;

    private volatile boolean initialized = false;

    public TwilioVerifyClient(
            @Value("${twilio.accountSid:}") String accountSid,
            @Value("${twilio.authToken:}") String authToken,
            @Value("${twilio.verifyServiceSid:}") String verifyServiceSid) {
        this.accountSid = accountSid == null ? null : accountSid.trim();
        this.authToken = authToken == null ? null : authToken.trim();
        this.verifyServiceSid = verifyServiceSid == null ? null : verifyServiceSid.trim();
    }

    private void init() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    if (isBlank(accountSid) || isBlank(authToken) || isBlank(verifyServiceSid)) {
                        throw new IllegalStateException("Twilio Verify is not configured: set twilio.accountSid, twilio.authToken, twilio.verifyServiceSid");
                    }
                    Twilio.init(accountSid, authToken);
                    initialized = true;
                }
            }
        }
    }

    public void sendVerification(String toE164) {
        init();
        try {
            Verification.creator(verifyServiceSid, toE164 == null ? null : toE164.trim(), "sms").create();
            log.info("Twilio Verify OTP initiated to {}", toE164);
        } catch (ApiException e) {
            log.error("Twilio Verify send failed: {} (status={}, code={}, moreInfo={})",
                    e.getMessage(), e.getStatusCode(), e.getCode(), e.getMoreInfo());
            throw e;
        }
    }

    public boolean checkVerification(String toE164, String code) {
        init();
        try {
            VerificationCheck check = VerificationCheck.creator(verifyServiceSid)
                    .setTo(toE164 == null ? null : toE164.trim())
                    .setCode(code == null ? null : code.trim())
                    .create();
            String status = check.getStatus();
            boolean ok = "approved".equalsIgnoreCase(status);
            if (!ok) {
                log.warn("Twilio Verify not approved (status={}) for {}", status, toE164);
            }
            return ok;
        } catch (ApiException e) {
            log.error("Twilio Verify check failed: {} (status={}, code={}, moreInfo={})",
                    e.getMessage(), e.getStatusCode(), e.getCode(), e.getMoreInfo());
            return false;
        }
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
