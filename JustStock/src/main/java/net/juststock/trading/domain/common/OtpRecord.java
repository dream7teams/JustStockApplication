package net.juststock.trading.domain.common;

import java.time.Instant;

public class OtpRecord {
  private final String otp;
  private final Instant expiresAt;
  private int attempts;
  private Instant lastSentAt;

  public OtpRecord(String otp, Instant expiresAt, Instant lastSentAt) {
    this.otp = otp;
    this.expiresAt = expiresAt;
    this.lastSentAt = lastSentAt;
    this.attempts = 0;
  }

  public String getOtp(){ return otp; }
  public Instant getExpiresAt(){ return expiresAt; }
  public int getAttempts(){ return attempts; }
  public void incrementAttempts(){ attempts++; }
  public Instant getLastSentAt(){ return lastSentAt; }
  public void setLastSentAt(Instant t){ lastSentAt = t; }
}
