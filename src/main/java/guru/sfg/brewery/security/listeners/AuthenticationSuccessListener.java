package guru.sfg.brewery.security.listeners;

import guru.sfg.brewery.domain.LoginSuccess;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.LoginSuccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener {

    private final LoginSuccessRepository loginSuccessRepository;

    @EventListener
    public void listen(AuthenticationSuccessEvent event) {
        log.debug("User logged in Okay");

        if (event.getAuthentication() instanceof UsernamePasswordAuthenticationToken) {
            LoginSuccess.LoginSuccessBuilder builder = LoginSuccess.builder();

            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getAuthentication();

            if (token.getPrincipal() instanceof User) {
                User user = (User) token.getPrincipal();
                builder.user(user);

                log.debug("User name logged in: {}", user.getUsername());
            }

            if (token.getDetails() instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();

                log.debug("Source IP: {}", details.getRemoteAddress());

                builder.sourceIp(details.getRemoteAddress());
            }

            LoginSuccess loginSuccess = loginSuccessRepository.save(builder.build());

            log.debug("Login Successfully saved. Id: {}", loginSuccess.getId());
        }
    }
}
