package org.myapp.groovie.service;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.entity.payment.Transaction;
import org.myapp.groovie.entity.payment.TransactionStatus;
import org.myapp.groovie.entity.user.Group;
import org.myapp.groovie.entity.user.Role;
import org.myapp.groovie.entity.user.User;
import org.myapp.groovie.repository.TransactionRepository;
import org.myapp.groovie.repository.UserRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentSyncService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final IGroupService groupService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateTransaction(String email, String subscriptionId) {
        Transaction t = transactionRepository.findByUserMail(email);
        if (t != null) {
            t.setSubscriptionId(subscriptionId);
            t.setStatus(TransactionStatus.COMPLETED);
            transactionRepository.save(t);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void promoteToArtist(String email) throws ApiCallException {
        User u = userRepository.getUserByEmail(email);
        if (u == null) return;

        Group artist = groupService.getGroupByRole(Role.ARTIST);
        if (artist == null) return;

        u.getGroups().clear();
        u.getGroups().add(artist);
        userRepository.save(u);
    }
}