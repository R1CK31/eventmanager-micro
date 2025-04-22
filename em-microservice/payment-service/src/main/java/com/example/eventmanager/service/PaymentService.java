package com.example.eventmanager.service;


import com.example.eventmanager.entity.Payment;
import com.example.eventmanager.entity.PaymentLog;
import com.example.eventmanager.repository.PaymentLogRepository;
import com.example.eventmanager.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional; 

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentLogRepository paymentLogRepository;

    public PaymentService(PaymentRepository paymentRepository, PaymentLogRepository paymentLogRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentLogRepository = paymentLogRepository;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    @Transactional
    public Payment createPayment(Payment payment) {
        Payment savedPayment = paymentRepository.save(payment);

        PaymentLog log = new PaymentLog();
        log.setPaymentId(savedPayment.getId());
        log.setLogMessage("Payment created with amount: " + savedPayment.getAmount());
        log.setCreatedAt(LocalDateTime.now());
        paymentLogRepository.save(log);

        return savedPayment;
    }

    @Transactional
    public Payment updatePayment(Long id, Payment paymentDetails) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setAmount(paymentDetails.getAmount());
        payment.setPaymentDate(paymentDetails.getPaymentDate());
        payment.setEventId(paymentDetails.getEventId());
        payment.setUserId(paymentDetails.getUserId());

        Payment updatedPayment = paymentRepository.save(payment);

        PaymentLog log = new PaymentLog();
        log.setPaymentId(updatedPayment.getId());
        log.setLogMessage("Payment updated. New amount: " + updatedPayment.getAmount());
        log.setCreatedAt(LocalDateTime.now());
        paymentLogRepository.save(log);

        return updatedPayment;
    }

    @Transactional
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
        PaymentLog log = new PaymentLog();
        log.setPaymentId(id);
        log.setLogMessage("Payment with ID " + id + " deleted.");
        log.setCreatedAt(LocalDateTime.now());
        paymentLogRepository.save(log);
    }
}
