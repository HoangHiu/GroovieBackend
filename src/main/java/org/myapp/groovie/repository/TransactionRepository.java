package org.myapp.groovie.repository;

import org.myapp.groovie.entity.payment.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Transaction findByUserMail(String email);
}
