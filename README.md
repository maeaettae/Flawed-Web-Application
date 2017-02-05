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

## Issue 1: A2-Broken Authentication and Session Management

### Steps to reproduce:


2. 


## Issue 2: A3-Cross-Site Scripting (XSS)

### Steps to reproduce:

1. Go to the startup page after logging in, which requests you to sign up for the new event.
2. Copy the next snippet of code to the input field `Name`:

  ```javascript
  <IFRAME SRC="javascript:alert('XSS');"></IFRAME>
  ```
  And click Submit
3. A popup with message "XSS" should now appear on the page that lists you registrations.

### Steps to reproduce using ZAP:

1. Open the application
2. Enter `http://localhost:8080` as `URL to attack`, and click `Attack`.
3. The application lists every page that is currently accessible to `Sites`-drop-down list.
4. From there, select `http://localhost:8080`, and choose `Include in Context` -> `New Context`.
5. Select `Authentication` and choose `Form-based Authentication` from the drop-down list.
6. Select `http://localhost:8080/login` for the `Form Target URL`.
7. Login `Request POST Data` should be automatically filled. If not, copy `username=ZAP&password=ZAP` to the field.
8. Change `Password Parameter` to `password`.
9. Now select `Users` from the left. Click `Add`.
10. Type anything as a `User Name`. Type your new login credentials to the fields below and click `Add`.
11. Click `OK`.
12. Choose `Tools` -> `Spider...`.
13. Select `http://localhost:8080/` for `Starting point`.
14. Choose the newly created context and user from the drop-down lists below.
15. Click `Start Scan`.



## Issue 3: A4-Insecure Direct Object References

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
## Issue 4: A6-Sensitive Data Exposure

### Steps to reproduce:

## Issue 5: A8-Cross-Site Request Forgery (CSRF)

### Steps to reproduce:
