package sec.project.config;

import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import sec.project.domain.Account;
import sec.project.domain.Signup;
import sec.project.repository.AccountRepository;
import sec.project.repository.SignupRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private SignupRepository signupRepository;

    @PostConstruct
    public void init() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode("admin");
        Account admin = new Account("admin", hashedPassword);
        accountRepository.save(admin);
        Signup sgn = new Signup("Grizzly", "Bearhouse 15, Bearforest 1548", 2014, admin);
        signupRepository.save(sgn);
        sgn = new Signup("Girly", "Ponyland 8, Where ponies live 1425", 2015, admin);
        signupRepository.save(sgn);
        sgn = new Signup("Manly", "Manlynd 1, EqualsManlyLand 0513", 2016, admin);
        signupRepository.save(sgn);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        if (accountRepository.findByUsername(username)==null) {
            throw new UsernameNotFoundException("No such user: "+ username);
        }
        return new org.springframework.security.core.userdetails.User(
                username,
                accountRepository.findByUsername(username).getPassword(),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority("USER")));
        
    }
}
