package com.jangjak.chagok.payment.repository;

import com.jangjak.chagok.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
