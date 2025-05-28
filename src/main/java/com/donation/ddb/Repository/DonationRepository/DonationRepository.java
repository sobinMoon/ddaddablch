package com.donation.ddb.Repository.DonationRepository;


import com.donation.ddb.Domain.Donation;
import com.donation.ddb.Domain.StudentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation,Long> {

    //중복 트랜잭션 체크
    boolean existsByTransactionHash(String transactionHash);

    //트랜잭션 확인
    Optional<Donation> findByTransactionHash(String transactionHash);

    //특정 학생의 기부 내역 조회 (최신순)
    // Page<Donation> findByStudentUserOrderByCreatedAtDesc(StudentUser studentUser);

//    //특정 학생의 총 기부 금액 조회
//    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.studentUser.sId=:studentId AND d.status = 'CONFIRMED'")
//    BigDeciaml getTotalDonationAmountBy StudentId(@Param("studentId") Long studentId);

}
