package com.example.demo.config;

import com.example.demo.security.JwtTokenFilterConfigurer;
import com.example.demo.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
// 指定為 web security 的設定
@EnableWebSecurity
// 啟用安全方法設定
@EnableGlobalMethodSecurity(prePostEnabled = true)

public class WebSecurityJWTConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
//        http.cors().and().csrf()
//                .ignoringAntMatchers("/api/**")
//                .ignoringAntMatchers("/users/**");

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .antMatchers("/users/login").permitAll()
                .antMatchers("/users/signup").permitAll()
                .antMatchers("/api/news/**").permitAll()
                // swagger related
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .anyRequest().authenticated();

        // if user without permission
        // @TODO

        http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));

        // optional if need browser to test API
        // http.httpBasic();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // allow swagger
//        web.ignoring().antMatchers("/v2/api-docs/**");
//        web.ignoring().antMatchers("/swagger.json");
//        web.ignoring().antMatchers("/swagger-ui.html");
//        web.ignoring().antMatchers("/swagger-resources/**");
//        web.ignoring().antMatchers("/webjars/**");
//
//        // others...
//        web.ignoring().antMatchers("/configuration/**");
//        web.ignoring().antMatchers("/public");

        web.ignoring()
                // swagger related
                .antMatchers("/swagger-resources/**")
                .antMatchers("/v2/api-docs")
                .antMatchers("/swagger-ui.html")
                .antMatchers("/configuration/**") // not sure for what
                .antMatchers("/webjars/**")
                // public static...
                .antMatchers("/public");

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
