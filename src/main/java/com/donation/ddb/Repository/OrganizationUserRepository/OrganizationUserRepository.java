package com.donation.ddb.Repository.OrganizationUserRepository;

import com.donation.ddb.Domain.OrganizationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, Long> {
    Optional<OrganizationUser> findByoId(Long aLong);
    Optional<OrganizationUser> findByoEmail(String email);
    Optional<OrganizationUser> findByoWalletAddress(String address);
}
