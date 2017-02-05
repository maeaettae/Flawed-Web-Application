package sec.project.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Signup extends AbstractPersistable<Long> {

    private String name;
    private String address;
    private String key;
    private int year;
    
    @ManyToOne
    private Account account;

    public Signup() {
        super();
    }

    public Signup(String name, String address, int year, Account account) {
        this();
        this.name = name;
        this.address = address;
        this.year = year;
        this.account = account;
        
        switch(year) {
            case 2014:
                key = "15342ea4314";
                break;
            case 2015:
                key = "531245ger31";
                break;
            case 2016:
                key = "2512323afe5";
                break;
            case 2017:
                key = "gae3415o027";
                break;
            case 2018:
                key = "412351dfA34";
                break;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public Account getAccount() {
        return account;
    }
    
    public void setAccount(Account acc) {
        account = acc;
    }
    
    public String toString() {
        return "Event " + year + "   (Registration key: " + key + ")"; 
    }

}
