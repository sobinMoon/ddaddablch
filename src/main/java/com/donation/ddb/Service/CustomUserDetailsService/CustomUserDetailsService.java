package com.donation.ddb.Service.CustomUserDetailsService;

import com.donation.ddb.Domain.CustomUserDetails;
import com.donation.ddb.Domain.OrganizationUser;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Repository.OrganizationUserRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentUserRepository studentUserRepository;
    private final OrganizationUserRepository organizationUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("사용자 인증 시도: {}", email);

        // 학생 사용자 검색
        Optional<StudentUser> studentUser = studentUserRepository.findBysEmail(email);
        if (studentUser.isPresent()) {
            StudentUser student = studentUser.get();
            log.info("학생 사용자 발견: {}", email);

            return CustomUserDetails.builder()
                    .id(student.getSId())
                    .email(student.getSEmail())
                    .password(student.getSPassword())
                    .nickname(student.getSNickname())
                    .role(student.getRole().name()) // "ROLE_STUDENT"
                    .build();
        }

        // 단체 사용자 검색
        Optional<OrganizationUser> orgUser = organizationUserRepository.findByoEmail(email);
        if (orgUser.isPresent()) {
            OrganizationUser organization = orgUser.get();
            log.info("단체 사용자 발견: {}", email);

            return CustomUserDetails.builder()
                    .id(organization.getOId())
                    .email(organization.getOEmail())
                    .password(organization.getOPassword())
                    .nickname(organization.getOName() != null ? organization.getOName() : organization.getOEmail()) // null 방지
                    .role(organization.getRole().name()) // "ORGANIZATION"
                    .build();
        }

        log.warn("사용자를 찾을 수 없음: {}", email);
        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
    }

    // JWT 토큰에서 사용자 정보를 가져올 때 사용하는 메서드
    public UserDetails loadUserById(Long userId, String role) {
        log.info("ID로 사용자 조회: {} (역할: {})", userId, role);

        if ("STUDENT".equals(role)) {
            StudentUser student = studentUserRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("학생 사용자를 찾을 수 없습니다: " + userId));

            return CustomUserDetails.builder()
                    .id(student.getSId())
                    .email(student.getSEmail())
                    .role(student.getRole().name())
                    .nickname(student.getSNickname())
                    .build();

        } else if ("ORGANIZATION".equals(role)) {
            OrganizationUser organization = organizationUserRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("단체 사용자를 찾을 수 없습니다: " + userId));

            return CustomUserDetails.builder()
                    .id(organization.getOId())
                    .email(organization.getOEmail())
                    .role(organization.getRole().name())
                    .nickname(organization.getOName() != null ? organization.getOName() : organization.getOEmail()) // null 방지
                    .build();
        }

        throw new UsernameNotFoundException("알 수 없는 역할: " + role);
    }
}