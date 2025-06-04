package com.donation.ddb.Domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "organization_subscription",
        uniqueConstraints = @UniqueConstraint(columnNames = {"o_id", "s_id"}))
public class OrgSubscription extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "os_id")
    private Long osId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "o_id", nullable = false)
    private OrganizationUser organizationUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private StudentUser studentUser;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // 구독 활성화 상태

    @Builder.Default
    @Column(name = "notification_enabled", nullable = false)
    private Boolean notificationEnabled = true; // 알림 수신 여부
}
