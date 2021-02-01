package guru.sfg.brewery.config;

import guru.sfg.brewery.security.HeaderUsernamePasswordExtractor;
import guru.sfg.brewery.security.QueryParamsUsernamePasswordExtractor;
import guru.sfg.brewery.security.RestAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public RestAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager) {
        RestAuthFilter filter = new RestAuthFilter(
                new AntPathRequestMatcher("/api/**"),
                new HeaderUsernamePasswordExtractor()
        );
        filter.setAuthenticationManager(authenticationManager);

        return filter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();

        http.authorizeRequests(getExpressionInterceptUrlRegistryCustomizer())
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin().and().httpBasic();
    }

    private Customizer<ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry> getExpressionInterceptUrlRegistryCustomizer() {
        return authorize ->
                authorize
                        .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                        .antMatchers("/beers/find", "/beers*").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll();
    }

//    @Override
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("spring")
//                .password("guru")
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("spring")
                .password("{bcrypt}$2a$10$AY3Q4ujrfUiByTafuRENQeBped7g76.Yss4IKW7A0a0LkaKTIVfKC")
                .roles("ADMIN")
                .and()
                .withUser("user")
                .password("{sha256}86d907ff2acb09dad3e65dacf88973e3b630287f7d592b90f6f93d3a9ecee12965cb14a1690933cf")
                .roles("USER")
                .and()
                .withUser("scott")
                .password("{ldap}{SSHA}lYBLpjg38mz2huYq74L9cAlx/3jbfe6yk50SAw==")
                .roles("CUSTOMER");
    }
}
