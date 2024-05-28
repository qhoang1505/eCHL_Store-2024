package Model.Person;

public class Client_Chat {
    private String username;
    private String password;
    private String email;
    private String fullname;
    private String otp;
    private byte[] image_data;
    private String hotline;

    public Client_Chat(String username, String email, String fullname, byte[] image_data, String hotline) {
        this.username = username;
        this.email = email;
        this.fullname = fullname;
        this.image_data = image_data;
        this.hotline = hotline;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public byte[] getImage_data() {
        return image_data;
    }

    public void setImage_data(byte[] image_data) {
        this.image_data = image_data;
    }

    public String getHotline() {
        return hotline;
    }

    public void setHotline(String hotline) {
        this.hotline = hotline;
    }
}
