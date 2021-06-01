package com.example.demo.config;

import com.example.demo.security.CustomPermissionEvaluator;
import com.example.demo.security.JwtTokenFilterConfigurer;
import com.example.demo.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

@Configuration

// 指定為 web security 的設定
@EnableWebSecurity
/**
 * 啟用安全方法設定:
 * jsr250Enabled | prePostEnabled | securedEnabled 三種可選
 *
 * jsr250Enabled 可以使用: @DenyAll, @RolesAllowed, @PermitAll
 * prePostEnabled 可以使用: @PreAuthorize, @PostAuthorize, @PreFilter, @PostFilter
 * securedEnabled 可以使用: @Secured
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)

public class WebSecurityJWTConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomPermissionEvaluator customPermissionEvaluator;

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
                .antMatchers(HttpMethod.GET, "/api/news/**").permitAll()
                // swagger related
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .anyRequest().authenticated();

        // if user without permission, return http status 403
        // @TODO
        // 如果要將預設的 response 從 403 改成 401
        // http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

        http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));

        // optional if need browser to test API
        // http.httpBasic();

        // override hasPermission implement on @PreAuthorize
        http.authorizeRequests().expressionHandler(defaultWebSecurityExpressionHandler());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // allow swagger
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

    @Bean
    public DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
        defaultWebSecurityExpressionHandler.setPermissionEvaluator(customPermissionEvaluator);
        return defaultWebSecurityExpressionHandler;
    }
}
