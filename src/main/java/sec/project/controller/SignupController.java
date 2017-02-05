package sec.project.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.AccountRepository;
import sec.project.repository.SignupRepository;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;
    
    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        return "form";
    }


    @RequestMapping(value = "/signups/{user}", method = RequestMethod.POST)
    public String addSignup(Authentication auth, @PathVariable String user, @RequestParam String name, 
            @RequestParam String address,  @RequestParam int year, Model model) {
        
        signupRepository.save(new Signup(name, address, year, accountRepository.findByUsername(auth.getName())));
        List<Signup> signups = accountRepository.findByUsername(user).getSignups();
        model.addAttribute("signups", signups);
        model.addAttribute("name", name);
        model.addAttribute("address", address);
        model.addAttribute("success", true);
        
        return "signups";
    }
    
    @RequestMapping(value = "/signups/{user}", method = RequestMethod.GET)
    public String checkSignup(@PathVariable String user, Model model) {
        List<Signup> signups = accountRepository.findByUsername(user).getSignups();
        model.addAttribute("signups", signups);
        return "signups";
    }
}
