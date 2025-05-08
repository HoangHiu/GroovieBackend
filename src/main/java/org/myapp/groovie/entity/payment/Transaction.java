package org.myapp.groovie.entity.payment;

import jakarta.persistence.*;
import lombok.*;
import org.myapp.groovie.entity.user.User;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    @Id
    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "user_mail")
    private String userMail;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "subscription_id")
    private String subscriptionId;

    @Column(name = "invoice_id")
    private String invoiceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionStatus status;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @PrePersist
    public void prePersist() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
