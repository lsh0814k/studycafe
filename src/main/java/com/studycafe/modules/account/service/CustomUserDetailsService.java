package com.studycafe.modules.account.service;

import com.studycafe.modules.account.web.dto.UserAccount;
import com.studycafe.modules.account.domain.Account;
import com.studycafe.modules.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserAccount loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> byEmail = accountRepository.findByEmail(username);
        if (byEmail.isPresent()) {
            return new UserAccount(byEmail.get());
        }

        Optional<Account> byNickname = accountRepository.findByNickname(username);
        if (byNickname.isPresent()) {
            return new UserAccount(byNickname.get());
        }

        throw new UsernameNotFoundException(username);
    }
}
