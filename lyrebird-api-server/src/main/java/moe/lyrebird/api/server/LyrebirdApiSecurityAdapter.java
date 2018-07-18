package moe.lyrebird.api.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import moe.lyrebird.api.conf.Endpoints;

@Configuration
@EnableWebSecurity
public class LyrebirdApiSecurityAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/" + Endpoints.VERSIONS_CONTROLLER + "/**").anonymous()
            .antMatchers("/actuator/**").hasRole("admin")
            .and()
            .httpBasic();
    }

}
