package net.badowl.persistentsession;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@SpringBootApplication
public class PersistentSessionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersistentSessionApplication.class, args);
    }

    @Configuration
    @EnableWebSecurity
    @EnableJdbcHttpSession
    public static class Config extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                .withUser("user").password(passwordEncoder().encode("user")).roles("USER");
        }

        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .authorizeRequests()
                    /* Enable public access to H2 console */
                    .antMatchers("/h2-console/**").permitAll()

                    .antMatchers("/all").permitAll()
                    .antMatchers("/logout").permitAll()
                    .antMatchers("/**").authenticated()

                    .and().httpBasic();
            /* So H2 works properly when navigating to /h2-console */
            http.headers().frameOptions().disable();

            http.requestCache().requestCache(new NullRequestCache());
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public EmbeddedDatabase dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .addScript("org/springframework/session/jdbc/schema-h2.sql").build();
        }

        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

    }

    @RestController
    public static class Ednpoint {

        @GetMapping("/all")
        public String anonymouslyAccesible() {
            return "Hi to all!";
        }

        @GetMapping("/auth")
        public String authenticatedOnly() {
            return "Hello authenticated!";
        }
    }
}
