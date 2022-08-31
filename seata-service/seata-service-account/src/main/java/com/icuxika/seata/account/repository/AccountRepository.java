package com.icuxika.seata.account.repository;

import com.icuxika.seata.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
