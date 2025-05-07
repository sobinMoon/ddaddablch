package com.donation.ddb.Service;

import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Repository.StudentUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentUserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(StudentUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username은 여기서 이메일로 사용됨
        StudentUser user = userRepository.findBysEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getSEmail())
                .password(user.getSPassword())
                .authorities("ROLE_USER") // 또는 사용자의 실제 권한
                .build();
    }
}
