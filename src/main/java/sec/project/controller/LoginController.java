package sec.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class LoginController {
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loadLogin() {
        return "login";
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String submitLogin(@RequestParam String username, @RequestParam String password) {
        return "redirect:/form";
    }
    
    @RequestMapping(value = "/logout", method = RequestMethod.GET) 
    public String logout () {
        return "login?logout";
    }
    
}
