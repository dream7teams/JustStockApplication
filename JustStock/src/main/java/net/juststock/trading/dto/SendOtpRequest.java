package net.juststock.trading.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SendOtpRequest {

  @NotBlank
  // E.164 format required by Twilio: e.g., +919022282949
  @Pattern(regexp="^\\+?[1-9]\\d{7,14}$", message="Invalid phone number format")
  private String phone;

  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
}
