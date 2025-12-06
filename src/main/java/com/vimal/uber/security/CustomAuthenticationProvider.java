package com.vimal.uber.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String identifier = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(identifier);

            if (!userDetails.isAccountNonLocked()) {
                throw new LockedException("Account is locked. Please contact support.");
            }

            if (!userDetails.isEnabled()) {
                throw new DisabledException("Your account is disabled. Please verify your email or contact support.");
            }

            if (!userDetails.isAccountNonExpired()) {
                throw new AccountExpiredException("Your account has expired.");
            }

            if (!userDetails.isCredentialsNonExpired()) {
                throw new CredentialsExpiredException("Your credentials have expired. Please reset your password.");
            }

            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("Invalid password.");
            }
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("No user found with this email or username.");
        } catch (LockedException | DisabledException | AccountExpiredException | CredentialsExpiredException |
                 BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException("Unexpected authentication error occurred.", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}