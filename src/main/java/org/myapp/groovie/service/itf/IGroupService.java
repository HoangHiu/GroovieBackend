package org.myapp.groovie.service.itf;

import org.myapp.groovie.entity.user.Group;
import org.myapp.groovie.entity.user.Role;
import org.myapp.groovie.response.ApiCallException;

import java.util.Set;
import java.util.UUID;

public interface IGroupService{
    Group getGroupByRole(Role role) throws ApiCallException;

    Set<Group> getGroupsByIds(Set<UUID> groupIds) throws ApiCallException;
}
