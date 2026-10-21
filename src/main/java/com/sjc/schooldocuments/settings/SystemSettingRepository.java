package com.sjc.schooldocuments.settings;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {

    Optional<SystemSetting> findBySettingKey(String settingKey);

    List<SystemSetting> findAllByOrderBySettingKeyAsc();

    /**
     * Row-level lock used by the reference-number generator so concurrent
     * submissions cannot produce the same sequence value.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SystemSetting s where s.settingKey = ?1")
    Optional<SystemSetting> findBySettingKeyForUpdate(String settingKey);
}
