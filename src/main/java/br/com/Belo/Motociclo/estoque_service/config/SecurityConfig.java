package br.com.Belo.Motociclo.estoque_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())      // desabilita o form de login
                .httpBasic(basic -> basic.disable())    // desabilita autenticação básica
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}