package com.donation.ddb.Repository.DonationRepository;


import com.donation.ddb.Domain.Donation;
import com.donation.ddb.Domain.StudentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation,Long>,DonationRepositoryCustom {

    //중복 트랜잭션 체크
    boolean existsByTransactionHash(String transactionHash);

    //트랜잭션해시로 기부 조회하기
    Optional<Donation> findByTransactionHash(String transactionHash);

    //특정 학생의 기부 내역 조회(SUCCESS 상태만)
    @Query("SELECT COALESCE(SUM(d.amount),0) FROM Donation d WHERE d.studentUser.sId=:studentId " +
            "AND d.status='SUCCESS'")
    BigDecimal getTotalDonationAmountByStudentId(@Param("studentId") Long studentId);

    //특정 학생의 총 기부 횟수 조회 (SUCCESS만)
    @Query("SELECT COUNT(d) FROM Donation d WHERE d.studentUser.sId=:studentId AND " +
            "d.status='SUCCESS'")
    Long getTotalDonationCountByStudentId(@Param("studentId") Long studentId);

    //최근 기부 내역 조회 (캠페인 정보 포함 - fetch join 사용)
    @Query("SELECT d FROM Donation d JOIN FETCH d.campaign c WHERE d.studentUser.sId=:studentId AND "+"d.status='SUCCESS'")
    List<Donation> findRecentDonationByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    //지금까지 총 기부된 금액
    @Query("SELECT SUM(d.amount) FROM Donation d")
    BigDecimal getTotalDonation();

    //가장 많이 기부한 카테고리 조회 -> 필요시 구현하기
}
