package org.myapp.groovie;

import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.UserDtoIn;
import org.myapp.groovie.entity.user.Group;
import org.myapp.groovie.entity.user.Role;
import org.myapp.groovie.entity.user.User;
import org.myapp.groovie.repository.GroupRepository;
import org.myapp.groovie.repository.UserRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IGroupService;
import org.myapp.groovie.service.itf.IUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RootUser implements CommandLineRunner {
    private final IUserService userService;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        initGroups();
        initAdmin();
    }

    public void initGroups() {
        Set<Role> existingRoles = groupRepository.findAll().stream()
                .map(Group::getRole)
                .collect(Collectors.toSet());

        List<Group> groupsToInitialize = List.of(
                new Group(null, "Admin", "Admin", Role.ADMIN),
                new Group(null, "Moderator", "Moderator", Role.MODERATOR),
                new Group(null, "Artist", "Artist", Role.ARTIST),
                new Group(null, "Regular", "Regular", Role.REGULAR)
        );

        List<Group> newGroups = groupsToInitialize.stream()
                .filter(group -> !existingRoles.contains(group.getRole()))
                .toList();

        if (!newGroups.isEmpty()) {
            groupRepository.saveAll(newGroups);
        }
    }


    private void initAdmin() throws ApiCallException {
        UUID adminGroupId = groupRepository.findByRole(Role.ADMIN).getUuid();

        User userCheckName = userRepository.getUserByUsername("hieuAdmin");
        User userCheckMail = userRepository.getUserByEmail("hoangdinhhieu2105.work@gmail.com");

        if (userCheckName == null && userCheckMail == null){
            User user = userService.create(new UserDtoIn("hieuAdmin", "21052003", "hoangdinhhieu2105.work@gmail.com" , Set.of(adminGroupId.toString())));
            System.out.println("User: " + user);
        }
    }
}
