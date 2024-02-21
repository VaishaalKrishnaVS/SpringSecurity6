package com.springsecurity.springsec.config;

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
            if(passwordEncoder.matches(pwd, customerList.get(0).getPwd())){
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(customerList.get(0).getRole()));
                return new UsernamePasswordAuthenticationToken(username,pwd,authorities);
            }
            else throw new BadCredentialsException("Invalid Password");
        }
        else throw new BadCredentialsException("No user found");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom((authentication)));
    }
}
