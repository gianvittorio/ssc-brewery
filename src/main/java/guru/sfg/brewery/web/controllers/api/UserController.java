package guru.sfg.brewery.web.controllers.api;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@Controller
public class UserController {

    private final UserRepository userRepository;

    private final GoogleAuthenticator googleAuthenticator;

    @GetMapping("/register2fa")
    public String register2fa(Model model) {

        User user = getUser();

        String url = GoogleAuthenticatorQRGenerator.getOtpAuthURL(
                "SFG",
                user.getUsername(),
                googleAuthenticator.createCredentials(user.getUsername())
        );

        log.debug("Google QR URL: {}", url);

        model.addAttribute("googleurl", url);

        return "user/register2fa";
    }

    @PostMapping("/register2fa")
    public String confirm2Fa(@RequestParam Integer verifyCode) {

        User user = getUser();

        log.debug("Entered code is: {}", verifyCode);

        if (!googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {
            return "user/register2fa";
        }

        userRepository.findById(user.getId())
                .map(savedUser -> {
                    savedUser.setUseGoogle2f(true);
                    userRepository.save(savedUser);

                    return savedUser;
                })
                .orElseThrow();

        return "/index";
    }

    @GetMapping("/verify2fa")
    public String verify2fa() {
        return "user/verify2fa";
    }

    @PostMapping("/verify2fa")
    public String verifyPostOf2Fa(@RequestParam Integer verifyCode) {
        User user = getUser();

        if (!googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {
            return "user/verify2fa";
        }

        ((User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal())
                .setGoogle2faRequired(false);

        return "/index";
    }

    private static User getUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
