package com.example.ecom.project.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import com.example.ecom.project.security.jwt.AuthTokenFilter;
import com.example.ecom.project.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableMethodSecurity
public class WebSecurityConfig  {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // SOLUȚIA: Îi dăm service-ul direct în constructor, așa cum cere eroarea!
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

        // Setăm doar encoder-ul, service-ul e deja setat sus
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Criptare puternică
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.disable()) // Dezactivăm CSRF pt API
//                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // <--- LINIA MAGICĂ
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Fără sesiuni
//                .authorizeHttpRequests(auth -> auth
//                        // 1. PUBLIC (Poate intra oricine)
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll() // Oricine vede lista
//                        .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()  // Oricine vede un produs
//                        .requestMatchers(HttpMethod.GET, "/api/reviews/product/**").permitAll() // Oricine citește recenzii
//                        .requestMatchers("/image/**", "/index.html", "/shop.html", "/product.html", "/cart.html", "/login.html", "/style.css", "/*.js").permitAll()
//
//                        // 2. DOAR ADMIN (Aici e securitatea critică!)
//                        // Doar Adminul poate adăuga, modifica sau șterge produse
//                        .requestMatchers(HttpMethod.POST, "/api/product").hasRole("ADMIN")       // Add Product
//                        .requestMatchers(HttpMethod.PUT, "/api/product/**").hasRole("ADMIN")     // Update Product
//                        .requestMatchers(HttpMethod.DELETE, "/api/product/**").hasRole("ADMIN")  // Delete Product
//
//                        // Admin Panel general
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/api/orders/admin/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")                        // 3. LOGAȚI (Oricine are cont)
//                        .requestMatchers(HttpMethod.POST, "/api/reviews/add").authenticated() // Adăugare recenzie
//                        .requestMatchers("/api/reviews/delete/**").authenticated() // Ștergere recenzie (controlerul verifică suplimentar dacă e a ta)
//
//                        // 4. RESTUL (Default)
//                        .anyRequest().authenticated()
//
//                );
//
//        http.authenticationProvider(authenticationProvider());
//        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. RUTE PUBLICE
                        .requestMatchers("/api/auth/**", "/image/**", "/*.html", "/*.css", "/*.js").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/product/**", "/api/reviews/product/**").permitAll()
                        .requestMatchers("/api/newsletter/subscribe").permitAll()
                        .requestMatchers("/api/newsletter/unsubscribe").permitAll()
                        .requestMatchers("/api/contact/**").permitAll()

                        // În filterChain, adaugă această linie la rutele publice (.permitAll()):


                        // 2. REGULA DE AUR PENTRU DELETE USER (O punem prima la admin!)
                        // Specificăm metoda DELETE explicit
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/users/**").hasAuthority("ROLE_ADMIN")

                        // 3. RESTUL RUTELOR DE ADMIN
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/orders/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/newsletter/admin/**").hasAuthority("ROLE_ADMIN")

                        // Produse (Admin)
                        .requestMatchers(HttpMethod.POST, "/api/product").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/product/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/product/**").hasAuthority("ROLE_ADMIN")

                        // 4. LOGAȚI & RESTUL
                        // Permite utilizatorilor logați să își vadă propriile comenzi
                        .requestMatchers("/api/my-orders").authenticated()
                        .requestMatchers("/api/reviews/**").authenticated()
                        .requestMatchers( "/api/user/avatar/**").authenticated() // ADAUGĂ "/api/user/avatar/**"
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. Permite toate originile (localhost:5500, localhost:3000, etc.)
        configuration.setAllowedOrigins(List.of("*"));

        // 2. Permite toate metodele HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 3. Permite toate headerele (inclusiv Authorization pentru token)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

        // 4. Înregistrează configurația pentru toate rutele
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
