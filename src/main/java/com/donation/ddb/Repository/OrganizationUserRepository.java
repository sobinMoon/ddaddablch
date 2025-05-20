package com.donation.ddb.Repository;


import com.donation.ddb.Domain.OrganizationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationUserRepository extends JpaRepository<OrganizationUser,Long> {
 Optional<OrganizationUser> findByoEmail(String email);
 Optional<OrganizationUser> findByoWalletAddress(String address);
 Optional<OrganizationUser> findByoId(Long aLong);

}
