package fr.eni.tp.filmotheque.configuration;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;



@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers("/").permitAll()
				.requestMatchers("/css/*").permitAll()
				.requestMatchers("/images/*").permitAll()
				.requestMatchers("/films").permitAll()
				.requestMatchers("/films/detail").permitAll()
				.requestMatchers(HttpMethod.GET, "/films/creer").hasRole("MEMBRE")
				.requestMatchers(HttpMethod.POST, "/films/creer").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/contexte").hasRole("MEMBRE")
				.requestMatchers(HttpMethod.GET, "/avis/creer").hasRole("MEMBRE")
				.requestMatchers(HttpMethod.POST, "/avis/creer").hasRole("MEMBRE")
				.requestMatchers("/login").permitAll()
				.requestMatchers("/logout").permitAll()
				
				.anyRequest().denyAll() // interdit l'accès aux urls non paramétrées
			)
			.httpBasic(Customizer.withDefaults())
			.formLogin((formLogin) ->
				formLogin
					.loginPage("/login")
					.defaultSuccessUrl("/session").permitAll()
			)
			.logout((logout) ->
				logout
					.invalidateHttpSession(true)
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
					.logoutSuccessUrl("/")
			);

					

		return http.build();
	}
	
	
	
	@Bean
    UserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        // Requête pour récupérer les utilisateurs
        jdbcUserDetailsManager.setUsersByUsernameQuery(
            "SELECT email AS username, password, 1 AS enabled FROM membre WHERE email = ?"
        );

        // Requête pour récupérer les autorités (rôles)
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
        		"SELECT m.email AS username, r.ROLE AS authority " +
        	            "FROM membre m " +
        	            "JOIN ROLES r ON m.admin = r.IS_ADMIN " +
        	            "WHERE m.email = ?"
        );
        return jdbcUserDetailsManager;
	}
	
	
}
