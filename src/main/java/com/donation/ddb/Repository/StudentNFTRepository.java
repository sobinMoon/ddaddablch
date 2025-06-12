package com.donation.ddb.Repository;


import com.donation.ddb.Domain.StudentNFT;
import com.donation.ddb.Domain.StudentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentNFTRepository extends JpaRepository<StudentNFT, Long> {

    // 안전한 방식: 객체 기준
    List<StudentNFT> findByStudentUserOrderByCreatedAtDesc(StudentUser studentUser);

    long countByStudentUser(StudentUser studentUser);

    @Query("SELECT sn FROM StudentNFT sn WHERE sn.studentUser.sId = :studentId AND sn.id = :nftId")
    StudentNFT findByStudentIdAndId(@Param("studentId") Long studentId, @Param("nftId") Long nftId);

    void deleteByStudentUser(StudentUser studentUser);

    boolean existsByImageUrl(String imageUrl);
}
