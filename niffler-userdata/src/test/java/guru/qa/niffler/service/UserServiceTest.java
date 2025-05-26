package guru.qa.niffler.service;

import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.FriendshipStatus;
import guru.qa.niffler.data.UserEntity;
import guru.qa.niffler.data.projection.UserWithStatus;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.ex.NotFoundException;
import guru.qa.niffler.ex.SameUsernameException;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserJsonBulk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static guru.qa.niffler.data.FriendshipStatus.PENDING;
import static guru.qa.niffler.model.FriendshipStatus.INVITE_SENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;

    private final UUID mainTestUserUuid = UUID.randomUUID();
    private final String mainTestUserName = "dima";
    private UserEntity mainTestUser;

    private final UUID secondTestUserUuid = UUID.randomUUID();
    private final String secondTestUserName = "barsik";
    private UserEntity secondTestUser;

    private final UUID thirdTestUserUuid = UUID.randomUUID();
    private final String thirdTestUserName = "emma";
    private UserEntity thirdTestUser;

    private final String notExistingUser = "not_existing_user";
    private final String searchQuery = "Gandalf";

    @BeforeEach
    void init() {
        mainTestUser = new UserEntity();
        mainTestUser.setId(mainTestUserUuid);
        mainTestUser.setUsername(mainTestUserName);
        mainTestUser.setCurrency(CurrencyValues.RUB);

        secondTestUser = new UserEntity();
        secondTestUser.setId(secondTestUserUuid);
        secondTestUser.setUsername(secondTestUserName);
        secondTestUser.setCurrency(CurrencyValues.RUB);

        thirdTestUser = new UserEntity();
        thirdTestUser.setId(thirdTestUserUuid);
        thirdTestUser.setUsername(thirdTestUserName);
        thirdTestUser.setCurrency(CurrencyValues.RUB);
    }


    @ValueSource(strings = {
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAMAAABg3Am1AAACwVBMVEUanW4am2wBY0QYmWsMfFYBZEQAAQBHcEz+" +
            "/v4AAAAZm20anW4bnW4IdFAanW4anW4BZUQbnm8bnm8anW4anG4VflganW4anW4Jd1Ibnm8EbEoDaUcbnW4anW4anW4bnm8anW4anW4" +
            "anG4BZUQWlGcHKR0anG0anW4FbksanG4anW4FbEobnm4anW4anW4anW4anW4anW4Zm2wanG0ZmmwZm20BY0MZmmwanW4anG4anG0cnm9B" +
            "roj0+vj9/v4hoXIAYkMfoHEMfFYLe1QWlGgTjWIQh14XlWgNf1gNf1gAYUIAYkIAYUIAYUIAYUIKeFMAYUIEakgAYUISimAAYUIAYEEAYUE" +
            "AYUIAYUIAYUIBCwcanW4MSjQHLB8BBwUJOCcNTzcSbUwVe1YanW4AAwIPWj8Sakvj8+0LQC0ZmmwAY0MAY0MAYkNdupglonVovp9RtJFbuZg1" +
            "qX8Zm2yAyK7f8er3+/p9x6yk2MUDaUi038+Fy7IHck6w3c0KeFMGb0wEakllvZ4IdVAGcE0Le1UKeVMbnm8AAAD///8AYUIanW4Zm20amGsCEAs" +
            "GJhoMfFYZmmwOVj0EHBQYj2QBBgQSbEwYjGIam2wUd1MYkmYCDQkFHhUOVDsJdVEJNSUZlWgSbU0Zk2cVkmYABANqv6Hs9vKr28pTtpLn9fBeupn+/" +
            "v46q4Mdn3D6/PsDaUcPhFwBZEQIc08AYkIYmWsVfliU0bsAAQALQS0ZlWkOUjkYjmMUeFSb1MB5xqoBCQYDFg8KPSsHKx4Zl2oYkGV3xakanG4NUDgcnnC" +
            "OzrcIMiMzqH5IsYv1+vmHzLNauJbF5tq84tRUtpPX7eWg1sJDr4gOgVn6/fyCyrArpXlwwqWm2cfa7+goo3fB5Ng9rIUjoXQhoHOe1sLT7ONLso0OgVrt9/P9/v2" +
            "+49XI59zW7eUKeVOYug0QAAAAhnRSTlP+/q/+/gX+AP7+/vYU/QO9kAb7sv7+EcH+helU03EZM/0BIQj+/vv3E+2o/rBEFZlayYYr4Aj9RPrrlv7+/v7+VP71Bf7Zs" +
            "PXhfOkTkvUd/ob+OP58/uHz2bD+0P7+/v7+/v7P/v7+/v66r5CQ/v7+/v7+uv7+/v7+kv7+Hf44hpL+HYY4/hahmy4AAAMTSURBVEjHY2BHBqbmRsUmagwMbVCkZpJp" +
            "ZG6KooQBiW1sZQlXCkdtDJZWxlg1KGlZMDBg08DQZqGlhKGBT5a7DQ/gluVD1SAmDDEMuw0gJCyGrEEumoGQBoZkOYQGHpk2IoAMD0yDID/cMDw2tDHwC0I1SDIQp4FBEq" +
            "JBXgirC7oXTpvcgyokJA/SoCjehmkDV+K8XrZ2zri+iYeR7RNXBGrQVcDUMGMSZzsnBO2bw4jQoKAL1KCD4ZiuOdvakUDfTISUDjuDnjoDmg1dCRCzo/aDXAVkzp4PV6Kux2DA" +
            "gK5hARtQFdsBUeY2kWkTZ4H0xiO8YcCgj+6gQ71AZ8zaDeXNPNjePmkLQlafwRDdhlSgmVNE4YI9fTE9SAFlyKCJbsNsoAU7kfjTUWQ1GRjQbJicAgz+Hdhjuo2BAT0VMLTNAPo4F" +
            "ipivxybBjQwFeii7RDm40drV2NEEoYNiUAbMsDc6mccHA8JOykJqEEVzG3s4OBY4UjQSfMXt7cvXghirXna0bHqHkEnMagC4+EYWOQ5B0dHLkEnMaS1c87bBRZZBtSwhKCT2o6eXNAFYV" +
            "3o6OhYSdhJcGS9lINjlQ2GDd04NawEhtJS9FDiYnBHtm/1OQT7/kugi26hu8idwRWhfU25re1yGPdBLUcHh90V9IhzZXBD8IChwnGmxB7MtnkBDCKOCoy05MbgibDu4lqgIzpWXD5y8+zdayD" +
            "mpTsYYejJUOeC8HR+GdBUBLpdhJG8XVoYnD2QQqmgFElD1Q3M/ODhzMBez4wUrHnX0yGq7c4XWmNmIOZmYLlU44TiyOOn927dc2LJKazFp1MTqGytXERsYbyoAVwYa0hNaCMKTJDSgBT30rwixN" +
            "ggwisNq4EEJLoIa+iSEIBXWaxMc7sJuad7LhMrolJUztJWwW+DijaTMnK1y5pj9oQLtwauq2bZrGgtAQevdetxuKt7/TovB8ymg7eP7wb/4M3oNmwO9t/g6+ONtXHiFxAWuilw4ysWln4oat0YuCk0" +
            "LMAPR2sG6JWgkMiI8E44CI+IDAliRVECAE4WhZg/rX3CAAAAAElFTkSuQmCC",
            ""
    })
    @ParameterizedTest
    void userShouldBeUpdated(String photo, @Mock UserRepository userRepository) {
        when(userRepository.findByUsername(eq(mainTestUserName)))
                .thenReturn(Optional.of(mainTestUser));

        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(answer -> answer.getArguments()[0]);

        userService = new UserService(userRepository);

        final String photoForTest = photo.isEmpty() ? null : photo;

        final UserJson toBeUpdated = new UserJson(
                null,
                mainTestUserName,
                "Test",
                "TestSurname",
                "Test TestSurname",
                CurrencyValues.USD,
                photoForTest,
                null,
                null
        );
        final UserJson result = userService.update(toBeUpdated);
        assertEquals(mainTestUserUuid, result.id());
        assertEquals("Test TestSurname", result.fullname());
        assertEquals(CurrencyValues.USD, result.currency());
        assertEquals(photoForTest, result.photo());

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void getRequiredUserShouldThrowNotFoundExceptionIfUserNotFound(@Mock UserRepository userRepository) {
        when(userRepository.findByUsername(eq(notExistingUser)))
                .thenReturn(Optional.empty());

        userService = new UserService(userRepository);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getRequiredUser(notExistingUser));
        assertEquals(
                "Can`t find user by username: '" + notExistingUser + "'",
                exception.getMessage()
        );
    }

    @Test
    void allUsersShouldReturnCorrectUsersList(@Mock UserRepository userRepository) {
        when(userRepository.findByUsernameNot(eq(mainTestUserName)))
                .thenReturn(getMockUsersMappingFromDb());

        userService = new UserService(userRepository);

        final List<UserJsonBulk> users = userService.allUsers(mainTestUserName, null);
        assertEquals(2, users.size());
        final UserJsonBulk invitation = users.stream()
                .filter(u -> u.friendshipStatus() == INVITE_SENT)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Friend with state INVITE_SENT not found"));

        final UserJsonBulk friend = users.stream()
                .filter(u -> u.friendshipStatus() == null)
                .findFirst()
                .orElseThrow(() -> new AssertionError("user without status not found"));


        assertEquals(secondTestUserName, invitation.username());
        assertEquals(thirdTestUserName, friend.username());
    }

    @Test
    void getCurrentUserShouldReturnCorrectUserJson(@NonNull @Mock UserRepository userRepository) {
        final UserEntity userEntity = getUserEntity();

        when(userRepository.findByUsername(eq(userEntity.getUsername())))
                .thenReturn(Optional.of(userEntity));

        final UserService userService = new UserService(userRepository);
        final UserJson result = userService.getCurrentUser(userEntity.getUsername());

        assertEquals(userEntity.getId(), result.id());
        assertEquals(userEntity.getUsername(), result.username());
        assertEquals(userEntity.getCurrency(), result.currency());
        assertEquals(userEntity.getFirstname(), result.firstname());
        assertEquals(userEntity.getSurname(), result.surname());
        assertEquals(userEntity.getFullname(), result.fullname());
        assertEquals(new String(userEntity.getPhoto(), StandardCharsets.UTF_8),
                result.photo());
        assertEquals(new String(userEntity.getPhotoSmall(), StandardCharsets.UTF_8),
                result.photoSmall());
    }

    @Test
    void getCurrentUserShouldReturnDefaultUserJsonWhenUserNotFoundInDb(@NonNull @Mock UserRepository userRepository) {

        when(userRepository.findByUsername(eq(mainTestUserName)))
                .thenReturn(Optional.empty());

        final UserService userService = new UserService(userRepository);
        final UserJson result = userService.getCurrentUser(mainTestUserName);

        assertNull(result.id());
        assertEquals(mainTestUserName, result.username());
        assertEquals(CurrencyValues.RUB, result.currency());
        assertNull(result.firstname());
        assertNull(result.surname());
        assertNull(result.fullname());
        assertNull(result.photo());
        assertNull(result.photoSmall());
        assertNull(result.friendshipStatus());
    }

    @Test
    void allUsersListShouldCallCorrectRepositoryMethodInCaseSearchQueryIsPassed(@Mock UserRepository userRepository) {

        userService = new UserService(userRepository);
        userService.allUsers(mainTestUserName, searchQuery);
        verify(userRepository, times(1))
                .findByUsernameNot(
                        eq(mainTestUserName),
                        eq(searchQuery));
    }

    @Test
    void allUsersPageableShouldCallCorrectRepositoryMethodInCaseSearchQueryIsPassed(@NonNull @Mock UserRepository userRepository) {
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(
                userRepository.findByUsernameNot(
                        eq(mainTestUserName),
                        eq(searchQuery),
                        eq(pageRequest)
                )
        )
                .thenReturn(new PageImpl<>(getMockUsersMappingFromDb(), pageRequest, 0));

        UserService userService = new UserService(userRepository);
        userService.allUsers(mainTestUserName, pageRequest, searchQuery);
        verify(userRepository, times(1))
                .findByUsernameNot(
                        eq(mainTestUserName),
                        eq(searchQuery),
                        eq(pageRequest)
                );
    }

    @Test
    void friendsListShouldCallCorrectRepositoryMethodInCaseSearchQueryIsPassed(@NonNull @Mock UserRepository userRepository) {
        when(userRepository
                .findByUsername(eq(mainTestUserName)))
                .thenReturn(Optional.of(mainTestUser));
        when(userRepository
                .findFriends(
                        eq(mainTestUser),
                        eq(searchQuery)
                ))
                .thenReturn(getMockUsersMappingFromDb());

        userService = new UserService(userRepository);
        userService.friends(mainTestUserName, searchQuery);

        verify(userRepository, times(1))
                .findFriends(
                        eq(mainTestUser),
                        eq(searchQuery));
    }

    @Test
    void friendsPageableShouldCallCorrectRepositoryMethodInCaseSearchQueryIsPassed(@NonNull @Mock UserRepository userRepository) {
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(userRepository
                .findByUsername(eq(mainTestUserName)))
                .thenReturn(Optional.of(mainTestUser));

        when(userRepository
                .findFriends(
                        eq(mainTestUser),
                        eq(searchQuery),
                        eq(pageRequest)
                ))
                .thenReturn(new PageImpl<>(getMockUsersMappingFromDb(), pageRequest, 0));

        UserService userService = new UserService(userRepository);
        userService.friends(mainTestUserName, pageRequest, searchQuery);
        verify(userRepository, times(1))
                .findFriends(
                        eq(mainTestUser),
                        eq(searchQuery),
                        eq(pageRequest)
                );
    }

    @Test
    void shouldThrowExceptionInCaseFriendshipIsCreateForSelfUser(@Mock UserRepository userRepository) {
        userService = new UserService(userRepository);

        final SameUsernameException exception = assertThrows(SameUsernameException.class,
                () -> userService.createFriendshipRequest(mainTestUserName, mainTestUserName));
        assertEquals("Can`t create friendship request for self user", exception.getMessage()
        );
    }

    @Test
    void friendshipRequestShouldBeCreated(@NonNull @Mock UserRepository userRepository) {
        thirdTestUser.addInvitations(mainTestUser);
        when(userRepository
                .findByUsername(eq(mainTestUserName)))
                .thenReturn(Optional.of(mainTestUser));
        when(userRepository
                .findByUsername(eq(thirdTestUserName)))
                .thenReturn(Optional.of(thirdTestUser));
        when(userRepository
                .save(mainTestUser))
                .thenReturn(thirdTestUser);

        UserService userService = new UserService(userRepository);
        UserJson result = userService.createFriendshipRequest(mainTestUserName, thirdTestUserName);

        assertEquals(thirdTestUser.getId(), result.id());
        assertEquals(thirdTestUser.getUsername(), result.username());
        assertEquals(thirdTestUser.getFirstname(), result.firstname());
        assertEquals(thirdTestUser.getSurname(), result.surname());
        assertEquals(thirdTestUser.getFullname(), result.fullname());
        assertEquals(thirdTestUser.getCurrency(), result.currency());
        assertEquals(INVITE_SENT, result.friendshipStatus());

        verify(userRepository, times(1))
                .save(eq(mainTestUser));
    }

    @Test
    void shouldThrowExceptionInCaseFriendshipIsDeclineForSelfUser(@Mock UserRepository userRepository) {
        userService = new UserService(userRepository);

        final SameUsernameException exception = assertThrows(SameUsernameException.class,
                () -> userService.declineFriendshipRequest(mainTestUserName, mainTestUserName));
        assertEquals("Can`t decline friendship request for self user", exception.getMessage());
    }

    @Test
    void friendshipRequestShouldBeDeclined(@NonNull @Mock UserRepository userRepository) {
        mainTestUser.addInvitations(secondTestUser);
        secondTestUser.addFriends(PENDING, mainTestUser);

        when(userRepository
                .findByUsername(eq(mainTestUserName)))
                .thenReturn(Optional.of(mainTestUser));
        when(userRepository
                .findByUsername(eq(secondTestUserName)))
                .thenReturn(Optional.of(secondTestUser));
        when(userRepository
                .save(mainTestUser))
                .thenReturn(secondTestUser);

        UserService userService = new UserService(userRepository);
        UserJson result = userService.declineFriendshipRequest(mainTestUserName, secondTestUserName);

        assertEquals(secondTestUser.getId(), result.id());
        assertEquals(secondTestUser.getUsername(), result.username());
        assertEquals(secondTestUser.getFirstname(), result.firstname());
        assertEquals(secondTestUser.getSurname(), result.surname());
        assertEquals(secondTestUser.getFullname(), result.fullname());
        assertEquals(secondTestUser.getCurrency(), result.currency());
        assertNull(result.friendshipStatus());

        verify(userRepository, times(1)).save(eq(mainTestUser));
    }

    @Test
    void shouldThrowExceptionInCaseFriendshipIsRemovedForSelfUser(@Mock UserRepository userRepository) {
        userService = new UserService(userRepository);

        final SameUsernameException exception = assertThrows(SameUsernameException.class,
                () -> userService.removeFriend(mainTestUserName, mainTestUserName));
        assertEquals("Can`t remove friendship relation for self user", exception.getMessage());
    }

    @Test
    void friendshipRequestShouldBeRemoved(@NonNull @Mock UserRepository userRepository) {
        mainTestUser.addFriends(FriendshipStatus.ACCEPTED, thirdTestUser);
        when(userRepository
                .findByUsername(eq(mainTestUserName)))
                .thenReturn(Optional.of(mainTestUser));
        when(userRepository
                .findByUsername(eq(thirdTestUserName)))
                .thenReturn(Optional.of(thirdTestUser));

        UserService userService = new UserService(userRepository);
        userService.removeFriend(mainTestUserName, thirdTestUserName);


        assertEquals(0, userService.getRequiredUser(mainTestUserName).getFriendshipRequests().size());
        assertEquals(0, userService.getRequiredUser(thirdTestUserName).getFriendshipRequests().size());
        assertEquals(0, userService.getRequiredUser(mainTestUserName).getFriendshipAddressees().size());
        assertEquals(0, userService.getRequiredUser(thirdTestUserName).getFriendshipAddressees().size());
        verify(userRepository, times(1)).save(eq(mainTestUser));
        verify(userRepository, times(1)).save(eq(thirdTestUser));
    }

    @NonNull
    private List<UserWithStatus> getMockUsersMappingFromDb() {
        return List.of(
                new UserWithStatus(
                        secondTestUser.getId(),
                        secondTestUser.getUsername(),
                        secondTestUser.getCurrency(),
                        secondTestUser.getFullname(),
                        secondTestUser.getPhotoSmall(),
                        PENDING
                ),
                new UserWithStatus(
                        thirdTestUser.getId(),
                        thirdTestUser.getUsername(),
                        thirdTestUser.getCurrency(),
                        thirdTestUser.getFullname(),
                        thirdTestUser.getPhotoSmall(),
                        FriendshipStatus.ACCEPTED
                )
        );
    }

    @NonNull
    private UserEntity getUserEntity() {
        final UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());
        userEntity.setUsername(mainTestUserName);
        userEntity.setCurrency(CurrencyValues.RUB);
        userEntity.setFirstname("firstname");
        userEntity.setSurname("surname");
        userEntity.setFullname("fullname");
        userEntity.setPhoto(new byte[1111]);
        userEntity.setPhotoSmall(new byte[11]);
        userEntity.setFriendshipRequests(new ArrayList<>());
        userEntity.setFriendshipAddressees(new ArrayList<>());

        return userEntity;
    }

}