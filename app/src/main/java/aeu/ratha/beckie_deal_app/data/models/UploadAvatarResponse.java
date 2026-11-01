package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

public class UploadAvatarResponse {
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private UserData data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public UserData getData() { return data; }
    public boolean isSuccess() { return "success".equalsIgnoreCase(status); }

    public static class UserData {
        @SerializedName("id")
        private int id;
        
        @SerializedName("email")
        private String email;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("avatar")
        private String avatar;

        public int getId() { return id; }
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getAvatar() { return avatar; }
    }
}
