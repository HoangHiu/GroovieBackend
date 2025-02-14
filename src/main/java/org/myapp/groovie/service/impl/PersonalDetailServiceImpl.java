package org.myapp.groovie.service.impl;

import org.myapp.groovie.dto.in.PersonalDetailDtoIn;
import org.myapp.groovie.entity.user.PersonalDetail;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IPersonalDetailService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PersonalDetailServiceImpl implements IPersonalDetailService {
    @Override
    public PersonalDetail update(UUID userId, PersonalDetailDtoIn personalDetailDtoIn) throws ApiCallException {
        return null;
    }
}
