package org.myapp.groovie.controller;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.param.checkout.SessionCreateParams.LineItem;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.SubscriptionCancelParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.myapp.groovie.entity.payment.Transaction;
import org.myapp.groovie.entity.payment.TransactionStatus;
import org.myapp.groovie.entity.user.Group;
import org.myapp.groovie.entity.user.Role;
import org.myapp.groovie.entity.user.User;
import org.myapp.groovie.repository.TransactionRepository;
import org.myapp.groovie.repository.UserRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.PaymentSyncService;
import org.myapp.groovie.service.itf.IGroupService;
import org.myapp.groovie.service.itf.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class StripeController {
    ApiExecutorService<Object> apiExecutorService = new ApiExecutorService<>();
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final IUserService userService;
    private final IGroupService groupService;
    private final PaymentSyncService paymentSyncService;

    @Value("${spring.stripe.api-secret-key}")
    private String STRIPE_SECRET_KEY;

    @Value("${spring.stripe.webhook-endpoint-secret}")
    private String STRIPE_WEBHOOK;

    @Value("${spring.stripe.artist-subscription-price-id}")
    private String ARTIST_SUB_PRICE_ID;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<ApiCallResponse<Object>> createCheckoutSession(
            Authentication authentication
    ) {
        return apiExecutorService.execute(() -> {
            User user = userService.getOneByUsername(authentication.getName());
            String userMail = user.getEmail();

            Stripe.apiKey = STRIPE_SECRET_KEY;

            LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setPrice(ARTIST_SUB_PRICE_ID)
                    .setQuantity(1L)
                    .build();

            SessionCreateParams params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .addLineItem(lineItem)
                    .setCustomerEmail(userMail)
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl("http://localhost:5173/login")
                    .setCancelUrl("http://localhost:5173/login")
                    .build();
            Session session = null;
            try {
                session = Session.create(params);

                transactionRepository.save(Transaction.builder()
                                .uuid(UUID.randomUUID())
                                .userMail(userMail)
                                .amount(BigDecimal.valueOf(lineItem.getQuantity()))
                                .currency("USD")
                                .status(TransactionStatus.PENDING)
                        .build());

            } catch (StripeException e) {
                throw new ApiCallException("Error in creating session id", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ApiCallResponse<>(session.getId());
        });
    }

    @PostMapping("/cancel-subscription")
    public ResponseEntity<String> cancelSubscription(Authentication authentication) {
        try {
            Stripe.apiKey = STRIPE_SECRET_KEY;
            User user = userService.getOneByUsername(authentication.getName());

            Transaction transaction = transactionRepository.findByUserMail(user.getEmail());

            Subscription subscription = Subscription.retrieve(transaction.getSubscriptionId());
            subscription.cancel(SubscriptionCancelParams.builder()
                    .setInvoiceNow(false)
                    .setProrate(false)
                    .build());


            Group regularGroup = groupService.getGroupByRole(Role.REGULAR);
            user.setGroups(Set.of(regularGroup));
            userRepository.save(user);
            return ResponseEntity.ok("Subscription canceled succesQsfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to cancel subscription: " + e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        String endpointSecret = STRIPE_WEBHOOK;

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            switch (event.getType()) {
                case "checkout.session.completed":
                    Session session = (Session) event.getDataObjectDeserializer()
                            .getObject().orElse(null);
                    if (session == null) {
                        return ResponseEntity.badRequest().body("Session not found in event");
                    }

                    String email = session.getCustomerDetails().getEmail();

                    paymentSyncService.updateTransaction(email, session.getSubscription());
                    paymentSyncService.promoteToArtist(email);
                    break;

                case "checkout.session.expired":
                    Session expired = (Session) event.getDataObjectDeserializer()
                            .getObject().orElse(null);
                    if (expired != null) {
                        String mail = expired.getCustomerDetails().getEmail();
                        var tx = transactionRepository.findByUserMail(mail);
                        if (tx != null) {
                            tx.setStatus(TransactionStatus.FAILED);
                            transactionRepository.save(tx);
                        }
                    }
                    break;

                case "invoice.payment_succeeded":
                    Invoice invoice = (Invoice) event.getDataObjectDeserializer()
                            .getObject().orElse(null);
                    if (invoice != null) {
                        String mail = invoice.getCustomerEmail();
                        var tx = transactionRepository.findByUserMail(mail);
                        if (tx != null) {
                            tx.setInvoiceId(invoice.getId());
                            tx.setStatus(TransactionStatus.COMPLETED);
                            transactionRepository.save(tx);
                            paymentSyncService.promoteToArtist(mail);
                        }
                    }
                    break;

                case "invoice.payment_failed":
                    Invoice failedInv = (Invoice) event.getDataObjectDeserializer()
                            .getObject().orElse(null);
                    if (failedInv != null) {
                        String mail = failedInv.getCustomerEmail();
                        var tx = transactionRepository.findByUserMail(mail);
                        if (tx != null) {
                            tx.setInvoiceId(failedInv.getId());
                            tx.setStatus(TransactionStatus.FAILED);
                            transactionRepository.save(tx);

                            // demote back to regular
                            User u = userRepository.getUserByEmail(mail);
                            Group regular = groupService.getGroupByRole(Role.REGULAR);
                            if (u != null && regular != null) {
                                u.setGroups(Set.of(regular));
                                userRepository.save(u);
                            }
                        }
                    }
                    break;

                default:
                    System.out.println("Event: " + event.getType());
                    break;
            }

            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

}
