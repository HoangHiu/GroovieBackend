package org.myapp.groovie.service.impl;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.entity.user.Group;
import org.myapp.groovie.entity.user.Role;
import org.myapp.groovie.repository.GroupRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements IGroupService {
    private final GroupRepository groupRepository;

    @Override
    public Group getGroupByRole(Role role) throws ApiCallException {
        Group group = groupRepository.findByRole(role);
        if(group == null){
            throw new ApiCallException("Group with role: " + role.name() + " doesn's exists", HttpStatus.NOT_FOUND);
        }
        return group;
    }
}
