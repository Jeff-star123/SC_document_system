package com.sjc.schooldocuments.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByStudentNumber(String studentNumber);

    Optional<User> findByEmployeeNumber(String employeeNumber);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByStudentNumber(String studentNumber);

    boolean existsByEmployeeNumber(String employeeNumber);

    long countByRole(Role role);

    long countByAccountStatus(AccountStatus status);

    List<User> findByRoleAndAccountStatus(Role role, AccountStatus status);

    List<User> findAllByRole(Role role);
}
