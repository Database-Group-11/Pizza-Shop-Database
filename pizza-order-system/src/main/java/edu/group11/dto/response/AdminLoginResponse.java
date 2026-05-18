package main.java.edu.group11.dto.response;

public class AdminLoginResponse {
    private int adminId;
    private String username;
    private String email;
    private String role;
    private String token;  // 可选，用于 JWT 认证

    public AdminLoginResponse() {}

    public AdminLoginResponse(int adminId, String username, String email, String role) {
        this.adminId = adminId;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public AdminLoginResponse(int adminId, String username, String email, String role, String token) {
        this.adminId = adminId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.token = token;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
