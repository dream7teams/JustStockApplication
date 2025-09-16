package net.juststock.trading.service.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class TwilioSmsSender implements SmsSender {
    private static final Logger log = LoggerFactory.getLogger(TwilioSmsSender.class);

    private final String accountSid;
    private final String authToken;
    private final String fromNumber; // optional if using Messaging Service
    private final String messagingServiceSid; // optional; preferred

    
    private final boolean enabled;

    public TwilioSmsSender(
            @Value("${twilio.accountSid:}") String accountSid,
            @Value("${twilio.authToken:}") String authToken,
            @Value("${twilio.from:}") String fromNumber,
            @Value("${twilio.messagingServiceSid:}") String messagingServiceSid) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromNumber = fromNumber;
        this.messagingServiceSid = messagingServiceSid;
        this.enabled = !(isBlank(accountSid) || isBlank(authToken) || (isBlank(fromNumber) && isBlank(messagingServiceSid)));
        if (!enabled) {
            log.warn("Twilio is not fully configured; SMS will be logged instead.");
        }
    }

    @Override
    public void send(String toE164, String message) {
        if (!enabled) {
            log.info("[SMS-DEV] To: {} | {}", toE164, message);
            return;
        }
        try {
            if (!toE164.startsWith("+")) {
                log.warn("Destination number not in E.164 format: {}", toE164);
            }
            Twilio.init(accountSid, authToken);
            if (!isBlank(fromNumber)) {
                // Use specific From number (must be SMS-capable and enabled for destination country)
                Message.creator(new PhoneNumber(toE164), new PhoneNumber(fromNumber), message).create();
            } else if (!isBlank(messagingServiceSid)) {
                // Fallback to Messaging Service if From is not set
                Message.creator(new PhoneNumber(toE164), messagingServiceSid, message).create();
            } else {
                throw new IllegalStateException("Neither twilio.from nor twilio.messagingServiceSid configured");
            }
            log.info("SMS sent to {}", toE164);
        } catch (Exception e) {
            log.error("Failed to send SMS via Twilio: {}", e.getMessage());
        }
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
