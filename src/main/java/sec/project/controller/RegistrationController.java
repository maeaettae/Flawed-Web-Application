package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Account;
import sec.project.repository.AccountRepository;


@Controller
public class RegistrationController {
    @Autowired
    private AccountRepository accountRepository;
    
    
    @RequestMapping(value = "/register", method = RequestMethod.GET) 
    public String register() {
        return "register";
    }
    
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String submitRegistration(@RequestParam String newUsername, @RequestParam String newPassword, Model model) {
        //checks if the username already exists
        if (accountRepository.findByUsername(newUsername)!=null) {
            model.addAttribute("registrationError", true);
            return "register";
        }
        //checks if the strings are empty
        if (newUsername.isEmpty() || newPassword.isEmpty()) {
            model.addAttribute("lengthError", true);
            return "register";
        }
        //checks if there are any spaces in the strings
        if (newUsername.matches(".*  *.*") || newPassword.matches(".*  *.*")) {
            model.addAttribute("spaceError", true);
            return "register";
        }
        Account acc = new Account(newUsername, newPassword);
        accountRepository.save(acc);
        model.addAttribute("done", true);
        return "register";
    }
}
