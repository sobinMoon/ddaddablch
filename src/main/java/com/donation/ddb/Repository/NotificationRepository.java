package com.donation.ddb.Repository;

import com.donation.ddb.Domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    // 읽지 않은 알림 개수 조회
    Integer countByStudentIdAndIsReadFalse(Long studentId);

    // 읽지 않은 알림 목록 조회 (최신순)
    List<Notification> findByStudentIdAndIsReadFalseOrderByCreatedAtDesc(Long studentId);

    // 특정 학생의 모든 알림 조회 (최신순)
    List<Notification> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    // 특정 알림 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.notificationId = :notificationId")
    int markAsRead(@Param("notificationId") Long notificationId);

    // 특정 학생의 모든 알림 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.studentId = :studentId")
    void markAllAsReadByStudentId(@Param("studentId") Long studentId);
}
