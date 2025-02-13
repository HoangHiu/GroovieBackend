package org.myapp.groovie.service.itf;

import org.myapp.groovie.entity.user.Group;
import org.myapp.groovie.entity.user.Role;
import org.myapp.groovie.response.ApiCallException;

public interface IGroupService{
    Group getGroupByRole(Role role) throws ApiCallException;
}
