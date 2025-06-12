package com.donation.ddb.Domain;

import com.donation.ddb.Domain.Enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    // 알림 받을 학생
    private Long studentId;

    // 알림 내용
    private String title;           // "새 댓글이 달렸습니다"
    private String content;         // "김철수님이 댓글을 달았습니다"

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType; // POST_COMMENT, DONATION_COMPLETE

    // 연결된 엔티티 정보
    private Long relatedPostId;     // 관련 게시글 ID (댓글 알림용)
    private Long relatedDonationId; // 관련 기부 ID (기부 완료 알림용)

    // 읽음 처리
    @Column(name = "is_read")
    private Boolean isRead = false;

    // 클릭시 이동할 URL
    private Long redirectUrl;     // "/post/123", "/donation/456"
}

