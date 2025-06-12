package com.donation.ddb.Repository;

import com.donation.ddb.Domain.OrgSubscription;
import com.donation.ddb.Domain.OrganizationUser;
import com.donation.ddb.Domain.StudentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrgSubscriptionRepository extends JpaRepository<OrgSubscription, Long> {
    // 특정 학생의 특정 단체 구독 조회
    Optional<OrgSubscription> findByOrganizationUserAndStudentUser(
            OrganizationUser organizationUser, StudentUser studentUser);

    // 학생의 활성화된 구독 단체 목록
    List<OrgSubscription> findByStudentUserAndIsActiveTrue(StudentUser studentUser);

    // 단체의 활성화된 구독자 목록
    List<OrgSubscription> findByOrganizationUserAndIsActiveTrue(OrganizationUser organizationUser);

    // 단체의 구독자 수
    @Query("SELECT COUNT(os) FROM OrgSubscription os WHERE os.organizationUser = :organization AND os.isActive = true")
    Long countByOrganizationUserAndIsActiveTrue(@Param("organization") OrganizationUser organizationUser);

    // 학생이 구독한 단체 수
    @Query("SELECT COUNT(os) FROM OrgSubscription os WHERE os.studentUser = :student AND os.isActive = true")
    Long countByStudentUserAndIsActiveTrue(@Param("student") StudentUser studentUser);

    // 알림 활성화된 구독자들
    @Query("SELECT os FROM OrgSubscription os WHERE os.organizationUser = :organization AND os.isActive = true AND os.notificationEnabled = true")
    List<OrgSubscription> findNotificationEnabledSubscribers(@Param("organization") OrganizationUser organizationUser);

    // 구독자가 많은 단체 순으로 조회
    @Query("SELECT os.organizationUser, COUNT(os) as subscriberCount FROM OrgSubscription os " +
            "WHERE os.isActive = true GROUP BY os.organizationUser ORDER BY subscriberCount DESC")
    List<Object[]> findPopularOrganizations();
}
