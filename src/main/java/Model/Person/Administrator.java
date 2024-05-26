package Model.Person;

public class Administrator implements IPerson{
    private String username;
    private String password;
    private String email;
    private String fullname;
    private String otp;
    private byte[] image_data;
    private String hotline;

    public Administrator(String username, String password, String email, String fullname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
    }

    public Administrator(String username, String password, String email, String fullname, String otp, byte[] image_data, String hotline) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
        this.otp = otp;
        this.image_data = image_data;
        this.hotline = hotline;
    }

    public Administrator(String username, String password) {
        this.username = username;
        this.password = password;
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

    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public void setUsername(String username) {
        this.username = username;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public String getEmail() {
        return email;
    }
    @Override
    public void setEmail(String email) {
        this.email = email;
    }
    @Override
    public String getFullname() {
        return fullname;
    }
    @Override
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
