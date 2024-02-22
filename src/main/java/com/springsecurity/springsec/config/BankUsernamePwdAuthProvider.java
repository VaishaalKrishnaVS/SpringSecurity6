package com.springsecurity.springsec.config;

import com.springsecurity.springsec.model.Authority;
import com.springsecurity.springsec.model.Customer;
import com.springsecurity.springsec.repository.CustomerRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class BankUsernamePwdAuthProvider implements AuthenticationProvider {
    private CustomerRepository customerRepository;
    private PasswordEncoder passwordEncoder;

    public BankUsernamePwdAuthProvider(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();
        List<Customer> customerList = customerRepository.findByEmail(username);
        if (!customerList.isEmpty()){
            if(passwordEncoder.matches(pwd, customerList.get(0).getPwd()))
                return new UsernamePasswordAuthenticationToken(username, pwd, getGrantedAuthorities(customerList.get(0).getAuthorities()));
            else throw new BadCredentialsException("Invalid Password");
        }
        else throw new BadCredentialsException("No user found");
    }

    private List<GrantedAuthority> getGrantedAuthorities(Set<Authority> authorities) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Authority authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
        }
        return grantedAuthorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom((authentication)));
    }
}
