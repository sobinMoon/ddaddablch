package com.donation.ddb.Repository;


import com.donation.ddb.Domain.StudentUser;
import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentUserRepository extends JpaRepository<StudentUser,Long> {
    Optional<StudentUser> findBysEmail(String email);
    //Optional<StudentUser> findBysWalletAddress(String address);
    Optional<StudentUser> findBysNickname(String nickname);
    Boolean existsBysEmail(String email);

}
