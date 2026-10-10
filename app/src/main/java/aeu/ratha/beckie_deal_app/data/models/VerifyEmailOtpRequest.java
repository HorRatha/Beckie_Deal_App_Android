package aeu.ratha.beckie_deal_app.data.models;

/**
 * Request model for email OTP verification.
 * Sent to /api/v1/auth/verify-email-otp endpoint.
 */
public class VerifyEmailOtpRequest {
    private String email;
    private String otp;

    public VerifyEmailOtpRequest() {}

    public VerifyEmailOtpRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
