package org.myapp.groovie.service.impl;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.PersonalDetailDtoIn;
import org.myapp.groovie.entity.user.PersonalDetail;
import org.myapp.groovie.entity.user.User;
import org.myapp.groovie.repository.PersonalDetailRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IPersonalDetailService;
import org.myapp.groovie.service.itf.IUserService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonalDetailServiceImpl implements IPersonalDetailService {
    private final PersonalDetailRepository personalDetailRepository;
    private final IUserService userService;

    @Override
    public PersonalDetail update(UUID userId, PersonalDetailDtoIn personalDetailDtoIn) throws ApiCallException {
        User user = userService.getOneById(userId);
        PersonalDetail personalDetail = user.getPersonalDetail();

        personalDetail.updateFromDtoIn(personalDetailDtoIn);
        personalDetail.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return personalDetailRepository.save(personalDetail);
    }

    @Override
    public PersonalDetail update(String username, PersonalDetailDtoIn personalDetailDtoIn) throws ApiCallException {
        User user = userService.getOneByUsername(username);
        PersonalDetail personalDetail = user.getPersonalDetail();

        personalDetail.updateFromDtoIn(personalDetailDtoIn);
        personalDetail.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return personalDetailRepository.save(personalDetail);
    }
}
