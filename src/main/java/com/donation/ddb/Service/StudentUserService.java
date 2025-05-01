package com.donation.ddb.Service;


import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Repository.StudentUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class StudentUserService {

    @Autowired
    private final StudentUserRepository studentUserRepository;
    private final PasswordEncoder passwordEncoder;

    public Boolean duplicateNickname(String wantednickname){

        return studentUserRepository.findBysNickname(wantednickname).isPresent();
    }

    public Long create(String sName,String sNickname,
                              String sEmail, String sPassword,
                              String sConfirmPassword){

        if( sPassword==null  ||!sPassword.equals(sConfirmPassword)){
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }

        StudentUser s_user=new StudentUser();
        s_user.setSName(sName);
        s_user.setSNickname(sNickname);
        s_user.setSEmail(sEmail);
        s_user.setSPassword(passwordEncoder.encode(sPassword));
        StudentUser s=studentUserRepository.save(s_user);
        Long sId= s.getSId();

        return sId;
    }


}
