package net.juststock.trading.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.juststock.trading.domain.admin.AdminProfile;
import net.juststock.trading.repository.AdminProfileRepository;
import net.juststock.trading.service.interfaces.UserProfileService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserProfileService userService;
    private final AdminProfileRepository adminRepo;

    public JwtAuthFilter(JwtUtil jwtUtil, UserProfileService userService, AdminProfileRepository adminRepo) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.adminRepo = adminRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // If already authenticated by an earlier filter, do nothing.
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                try {
                    var claims = jwtUtil.parseClaims(token);
                    boolean authenticated = false;

                    /* ========== ADMIN by adminId ========== */
                    Long adminId = null;
                    Object aid = claims.get("adminId");
                    if (aid instanceof Number nA) adminId = nA.longValue();
                    else if (aid instanceof String sA) { try { adminId = Long.parseLong(sA); } catch (NumberFormatException ignored) {} }

                    if (adminId != null && !authenticated) {
                        AdminProfile admin = adminRepo.findById(adminId).orElse(null);
                        if (admin != null) {
                            var auth = new UsernamePasswordAuthenticationToken(
                                admin,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                            );
                            auth.setDetails(admin);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            authenticated = true;
                        }
                    }

                    /* ========== ADMIN by email ========== */
                    if (!authenticated) {
                        Object emailClaim = claims.get("email");
                        if (emailClaim instanceof String em && !em.isBlank()) {
                            AdminProfile admin;
                            try {
                                var m = AdminProfileRepository.class.getMethod("findByEmailIgnoreCase", String.class);
                                admin = (AdminProfile) ((java.util.Optional<?>) m.invoke(adminRepo, em)).orElse(null);
                            } catch (NoSuchMethodException nsme) {
                                admin = adminRepo.findByEmail(em).orElse(null); // fallback if ignore-case not present
                            } catch (Exception reflectionIgnored) {
                                admin = adminRepo.findByEmail(em).orElse(null);
                            }
                            if (admin != null) {
                                var auth = new UsernamePasswordAuthenticationToken(
                                    admin,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                );
                                auth.setDetails(admin);
                                SecurityContextHolder.getContext().setAuthentication(auth);
                                authenticated = true;
                            }
                        }
                    }

                    /* ========== USER token by uid/userId ========== */
                    if (!authenticated) {
                        Long userId = null;

                        Object uid = claims.get("uid"); // your /api/auth/verify provides this
                        if (uid instanceof Number n) userId = n.longValue();
                        else if (uid instanceof String s) { try { userId = Long.parseLong(s); } catch (NumberFormatException ignored) {} }

                        if (userId == null) {
                            Object legacy = claims.get("userId");
                            if (legacy instanceof Number n2) userId = n2.longValue();
                            else if (legacy instanceof String s2) { try { userId = Long.parseLong(s2); } catch (NumberFormatException ignored) {} }
                        }

                        Integer ver = null;
                        Object v = claims.get("ver"); // optional token version
                        if (v instanceof Number nv) ver = nv.intValue();
                        else if (v instanceof String sv) { try { ver = Integer.parseInt(sv); } catch (NumberFormatException ignored) {} }

                        if (userId != null) {
                            var userOpt = userService.getUserById(userId);
                            if (userOpt.isPresent()) {
                                var user = userOpt.get();
                                if (ver == null || ver.equals(user.getTokenVersion())) {
                                    var principal = new UserPrincipal(
                                        user.getId(),
                                        user.getFullName(),
                                        user.getContactNumber(),
                                        Set.of("ROLE_USER")
                                    );
                                    var auth = new UsernamePasswordAuthenticationToken(
                                        principal,
                                        null,
                                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                    );
                                    SecurityContextHolder.getContext().setAuthentication(auth);
                                    authenticated = true;
                                }
                            }
                        }
                    }

                } catch (Exception ignored) {
                    // leave unauthenticated if anything goes wrong
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
