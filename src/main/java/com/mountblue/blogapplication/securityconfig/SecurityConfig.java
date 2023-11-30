package com.mountblue.blogapplication.securityconfig;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource){
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        userDetailsManager.setUsersByUsernameQuery("select email, password, true as enabled from users where email=?");
        userDetailsManager.setAuthoritiesByUsernameQuery("select email, role from users where email=?");
        return userDetailsManager;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf->csrf.disable()) // Disable CSRF protection
                .authorizeHttpRequests(configure ->
                        configure

                                .requestMatchers("/api/**","/","/posts/{postId}","/goToHomepage").permitAll()
                                .requestMatchers("/access-denied","/register").permitAll()
                                .requestMatchers("/addUser","/comment/{postId}").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(form ->
                        form
                                .loginPage("/showMyLoginPage")
                                .loginProcessingUrl("/authenticateTheUser")
                                .defaultSuccessUrl("/")
                                .permitAll()
                )
                .logout(logout -> logout.permitAll())
                .exceptionHandling(configure ->
                        configure.accessDeniedPage("/access-denied")
                );
        return http.build();
    }

}
