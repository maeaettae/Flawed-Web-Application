package sec.project.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="Account")
public class Account extends AbstractPersistable<Long> {
    
    @Column (unique = true)
    private String username;
    private String password;
    
    @OneToMany (mappedBy = "account")
    private List<Signup> signups;
    
    public Account() {}
    
    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public List<Signup> getSignups() {
        return signups;
    }
    
    public void setUsername(String usr) {
        username = usr;
    }
    
    public void setPassword(String pwd) {
        password = pwd;
    }
    
    public void setSignups(List<Signup> sgn) {
        signups = sgn;
    }
}
