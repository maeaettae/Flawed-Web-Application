# Flawed-Web-Application

## The fictional purpose of the web application explained

The web application includes functionalities for registering an account, logging in, signing up to an event and viewing the sign-ups. The fictional purpose of this application is to allow registered users to sign up for a certain event every year. Currently, only registrations to an event of 2018 are accepted. A user can sign-up with any name and address. When a sign-up is complete, a registration key is provided to the user, which is viewed on a signups-page.

## The real purpose of the web application explained

This application, however, contains multiple security flaws from OWASP 2013 Top 10 List, which include XSS and CSRF vulnerabilities. This document provides ways to reproduce those issues with explanations of how to fix them.

## Startup

First, clone/download the project, open it up and run it with the IDE of your choice.
Then, connect to `http://localhost:8080`. You should be redirected to `http://localhost:8080/login`.
Go to account registration page by pressing the topmost button `Click here!`. Enter a new username and password and click `Register!`. Go back to the login page by pressing `Go!`. Now you should be able to log in using your newly created account.
After succesful login, you should redirected to the startup page `http://localhost:8080/form`, and be seeing a page for signing up to the greatest event of 2018.

## Issue 1: A3-Cross-Site Scripting (XSS)

### Steps to reproduce:

1. Go to the startup page after logging in, which requests you to sign up for the new event.
2. Copy the next snippet of code to the input field `Name`:

  ```javascript
  <IFRAME SRC="javascript:alert('XSS');"></IFRAME>
  ```
  And click Submit
3. A popup with message "XSS" should now appear on the page that lists you registrations.

### Where the flaw comes from:

The vulnerability comes from the following lines of code, which can be found in signups.html:
  ```html
    <div th:if="${success}">
      <p>Thank you! Succesfully registered as <span id="name"></span>, from <span id="address"></span></p>
      <script th:inline="javascript">
      /*<![CDATA[*/
        var name = [[${name}]];
        var address = [[${address}]];
            
        document.getElementById("name").innerHTML =  name;
        document.getElementById("address").innerHTML = address;
       /*]]>*/
      </script>
    </div>
  ```
### How to fix it:
  
In the code above, name and address can contain any unvalidated, user-provided data. The easiest way to fix it may be
using thymeleaf instead of javascript, by replacing the code above with:

  ```html
    <div th:if="${success}">
      <p>Thank you! Succesfully registered as <span th:text="${name}"></span>, from <span th:text="${address}"></span></p>
    </div>
  ```
th:text escapes the text, which can and in this case will contain malicious javascript code.
  
## Issue 2: A4-Insecure Direct Object References

### Steps to reproduce:

1. Login with you new account.
2. Go to `http://localhost:8080/signups/admin`.
3. You can now see sign-ups of another user that do not belong to you.

### Where the flaw comes from:

This is the code that controls access to the signups:

  ```java
    @RequestMapping(value = "/signups/{user}", method = RequestMethod.GET)
    public String checkSignup(@PathVariable String user, Model model) {
        List<Signup> signups = accountRepository.findByUsername(user).getSignups();
        model.addAttribute("signups", signups);
        return "signups";
    }
  ```
### How to fix it:

We have to check if the request is valid for the currently logged in user.
  ```java
    @RequestMapping(value = "/signups/{user}", method = RequestMethod.GET)
    public String checkSignup(Authentication auth, @PathVariable String user, Model model) {
        //FIXED: if user parameter does not belong to the currently logged in user, redirect to /form
        if (accountRepository.findByUsername(auth.getName()) != accountRepository.findByUsername(user))
            return "redirect:/form";
        List<Signup> signups = accountRepository.findByUsername(user).getSignups();
        model.addAttribute("signups", signups);
        return "signups";
    }
  ```
## Issue 3: A6-Sensitive Data Exposure

Currently, the application does not protect the password, which is sensitive data, in any way.
Using any tool to detect http traffic you can easily find out the password you are using for logging in.
In this 

### Steps to reproduce:

1. Open Fiddler
2. If you are logged in in the web application, log out by pressing `Logout!`.
3. Log in again.
4. Fiddler should now have logged your request.
_NOTE: Fiddler may be unable to log your requests to localhost:8080. In this case, you can user for example either
`http://localhost:.8080` or `http://{yourmachinename}:8080`._
5. In Fiddler, go to `Inspectors`-tab.
6. Then, go to `WebForms`-tab.
7. You should be able to see your username and password in plain text.

### Where the flaw comes from:

The connection is not secure, because it uses HTTP instead of HTTPS, which means that the information that
is moving between the sender and the receiver is not encrypted.

### How to fix it:

To fix this, we need to create a `Self-Signed Certificate` and make some adjustments to the code.
You can find the instructions [here](https://drissamri.be/blog/java/enable-https-in-spring-boot/).

To make things a little bit easier, the code for the java class from the link is already included in the project.
Just uncomment the contents of sec.project.config.`HTTPSRedirectionConfiguration.java`.

Then, as is suggested in the link, create a file, `application.properties`, which contains the following:
  ```
  server.port: 8443
  server.ssl.key-store: keystore.p12
  server.ssl.key-store-password: mypassword
  server.ssl.keyStoreType: PKCS12
  server.ssl.keyAlias: tomcat
  ```
and paste it to either the root folder of the project or to `src/main/resources/`.

Copy the self-signed certificate to the project root folder and modify the `mypassword` of `server.ssl.key-store-password: mypassword` from `application.properties` to contain the password of the certificate.

Now you only need to add a new security exception in your browser for the certificate. If you don't know how to do this
for your current browser, you can find the instructions easily using the search engine of your choice.

## Issue 4: A8-Cross-Site Request Forgery (CSRF)

### Steps to reproduce:

1. Login with your username and password.
2. Go to the project root folder.
3. Open `maliciousAd.html`.
4. Go back to the website and reload.
5. Now you should be logged out.

### Where the flaw comes from:

In this case the ad wasn't that malicous after all: it merely logged you out. However in some cases this could be really harmful,
as this is not the only way to use CSRF.

The flaw comes from the security configuration, which disables CSRF protection in the following way using `csrf.disable()`:
```java
  @Override
  protected void configure(HttpSecurity http) throws Exception {
      http
          .authorizeRequests()                
              .antMatchers("/register").permitAll()
              .anyRequest().authenticated()
              .and()
          .formLogin()
              .loginPage("/login").permitAll()
              .and()
          .logout()
              .permitAll()
              .and()
          .csrf()
              .disable();
  }
```
### How to fix it:

To fix it, remove
 ```java
      .csrf()
          .disable();
 ```
 
 Now, to enable logging out, we need to replace 
 ```html
        <form action="#" th:action="@{/logout}" method="GET">
            <p><input type="submit" value="Logout!" /></p>
        </form>
 ```
 with
 ```html
        <form action="#" th:action="@{/logout}" method="POST">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <p><input type="submit" value="Logout!" /></p>
        </form>
 ```
 in `form.html` and `signups.html`.
 
 Now, a CSRF token is sent every time you log out, preventing the malicious ad from working.
 
## Issue 5: A2-Broken Authentication and Session Management

### Explanation:

Another major flaw can be found when looking at the source code. User credentials are stored to a database, but they are
never encrypted. This means that sensitive data (i.e. passwords) can be fetched from the database in plain text, which in turn means that that information could be compromised in unfortunate circumstances.

### Where the flaw comes from:

The following function can be found in `RegistrationController.java`. It creates new accounts from registration credentials. 
Look at the code after the checks.
```java
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
```
### How to fix it:

Encrypt every password before storing them to a database, for example by using `BCryptPasswordEncoder` which is included in
the Spring framework.

Replace the end of the code above with the following:
```java
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        newPassword = passwordEncoder.encode(newPassword);
        Account acc = new Account(newUsername, newPassword);
        accountRepository.save(acc);
        model.addAttribute("done", true);
        return "register";
```

In addition, paste this to `SecurityConfiguration.java`:
```java
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
```
That should configure the AuthenticationManagerBuilder to tell Spring to use BCryptPasswordEncoder 
to compare the passwords.

## Important:

This list contains only the 5 most important flaws I intentionally produced from OWASP 2013 Top 10 List.
This list of flaws is by no means exhaustive. There still exists several flaws, even some of which I am not aware of.
For example, you can access the registration keys of the current and past years, if you fiddle with the sign-up-form source code.
