package dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class logPass {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String email;
    private String password;

    logPass() {}

    logPass(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public void setPassword() {
        this.password = password;
    }
    public void setEmail() {
        this.email = email;
    }
}
