package com.donation.ddb.Repository;

import com.donation.ddb.Domain.AuthEvent;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Domain.WalletAuthStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthEventRepository extends JpaRepository<AuthEvent, Long> {

    // ID로 AuthEvent 찾기 (JpaRepository에서 기본 제공되지만 명시적으로 선언)
    Optional<AuthEvent> findById(Long id);

    // 지갑 주소로 최신 AuthEvent 찾기 (가장 최근 생성된 것)
    @Query("SELECT ae FROM AuthEvent ae WHERE ae.walletAddress = :walletAddress ORDER BY ae.createdAt DESC")
    Optional<AuthEvent> findByWalletAddress(@Param("walletAddress") String walletAddress);

    // 사용자로 최신 AuthEvent 찾기
    @Query("SELECT ae FROM AuthEvent ae WHERE ae.user = :user ORDER BY ae.createdAt DESC")
    Optional<AuthEvent> findByUser(@Param("user") StudentUser user);

    // 사용자 ID로 최신 AuthEvent 찾기
    @Query("SELECT ae FROM AuthEvent ae WHERE ae.user.sId = :userId ORDER BY ae.createdAt DESC")
    Optional<AuthEvent> findByUserId(@Param("userId") Long userId);

    // 지갑 주소와 특정 상태로 최신 AuthEvent 찾기
    @Query("SELECT ae FROM AuthEvent ae WHERE ae.walletAddress = :walletAddress AND ae.eventType = :status ORDER BY ae.createdAt DESC")
    Optional<AuthEvent> findByWalletAddressAndStatus(@Param("walletAddress") String walletAddress, @Param("status") WalletAuthStatus status);

    // 지갑 주소의 PENDING 상태인 최신 AuthEvent 찾기
    @Query("SELECT ae FROM AuthEvent ae WHERE ae.walletAddress = :walletAddress AND ae.eventType = 'PENDING' ORDER BY ae.createdAt DESC")
    Optional<AuthEvent> findLatestPendingByWalletAddress(@Param("walletAddress") String walletAddress);

    // ID와 지갑 주소로 AuthEvent 찾기 (보안을 위해 추가 검증)
    @Query("SELECT ae FROM AuthEvent ae WHERE ae.id = :id AND ae.walletAddress = :walletAddress")
    Optional<AuthEvent> findByIdAndWalletAddress(@Param("id") Long id, @Param("walletAddress") String walletAddress);
}