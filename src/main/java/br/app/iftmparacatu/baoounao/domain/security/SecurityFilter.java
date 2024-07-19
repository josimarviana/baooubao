package br.app.iftmparacatu.baoounao.domain.security;

import br.app.iftmparacatu.baoounao.domain.repository.UserRepository;
import br.app.iftmparacatu.baoounao.domain.services.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    JwtTokenService tokenService;

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if(token != null){
            JwtTokenService.UserTokenInfo tokenInfo = tokenService.validateToken(token);
            String username = tokenInfo.getUsername();
            UserDetails user = userDetailsServiceImpl.loadUserByUsername(username);

            if (validateToken(token, user)) {
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request,response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }

    private Boolean validateToken(String token, UserDetails userDetails) {
        return userDetails.getUsername().equals(tokenService.validateToken(token).getUsername());
    }
}
