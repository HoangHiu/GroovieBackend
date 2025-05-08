package org.myapp.groovie.service.itf;

import org.myapp.groovie.dto.in.PersonalDetailDtoIn;
import org.myapp.groovie.entity.user.PersonalDetail;
import org.myapp.groovie.response.ApiCallException;

import java.util.UUID;

public interface IPersonalDetailService {
    PersonalDetail update(UUID userId, PersonalDetailDtoIn personalDetailDtoIn) throws ApiCallException;
    PersonalDetail update(String username, PersonalDetailDtoIn personalDetailDtoIn) throws ApiCallException;

}
