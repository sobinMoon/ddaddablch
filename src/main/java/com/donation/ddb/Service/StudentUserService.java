package com.donation.ddb.Service;


import com.donation.ddb.Domain.DataNotFoundException;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Domain.VerificationToken;
import com.donation.ddb.Repository.StudentUserRepository;
import com.donation.ddb.Repository.VerificationTokenRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.donation.ddb.Domain.Role.ROLE_STUDENT;


@Service
@RequiredArgsConstructor
public class StudentUserService {

    @Autowired
    private final StudentUserRepository studentUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository tokenRepository;

    public Boolean duplicateNickname(String wantednickname){

        return studentUserRepository.findBysNickname(wantednickname).isPresent();
    }
    @Transactional
    public Long createUser(String name,String nickname,
                              String email, String password,
                              String confirmPassword){

        if(StringUtils.isEmpty(password) || !password.equals(confirmPassword)){
            throw new IllegalStateException("비밀번호가 일치하지 않습니다. ");
        }

        StudentUser user=new StudentUser();
        user.setSName(name);
        user.setSNickname(nickname);

        VerificationToken foundToken=tokenRepository.findByEmail(email)
                .orElseThrow(()-> new DataNotFoundException("이메일 인증을 먼저 해주세요."));

        //이메일 verificationtoken 찾아서 verified가 true인지 false이지 확인하기
        if(foundToken.isVerified()) user.setSEmail(email);
        else throw new IllegalStateException("인증된 이메일이 아닙니다. ");

        user.setSPassword(passwordEncoder.encode(password));
        //ROLE
        user.setRole(ROLE_STUDENT);

        StudentUser s=studentUserRepository.save(user);
        Long sid=s.getSId();

        //nickname 없으면 guest+id로 설정하기
        if(s.getSNickname()==null||s.getSNickname().isEmpty()){
            s.setSNickname("guest"+sid);
        }
        return sid;
    }

}
