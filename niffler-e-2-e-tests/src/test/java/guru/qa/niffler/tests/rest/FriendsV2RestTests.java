package guru.qa.niffler.tests.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.FriendshipStatus;
import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.GatewayV2ApiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTest
public class FriendsV2RestTests {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

    private final GatewayV2ApiClient gatewayV2ApiClient = new GatewayV2ApiClient();

    @DisplayName("Для пользователя должен возвращаться список друзей и входящих предложений дружить")
    @ApiLogin
    @User(friends = 1, incomeInvitations = 1)
    @Test
    void friendsAndIncomeInvitationsListShouldBeReturned(UserJson user, @Token String token) {
        final UserJson expectedFriend = user.testData().friends().getFirst();
        final UserJson expectedInvitation = user.testData().incomeInvitations().getFirst();

        final RestResponsePage<UserJson> response = gatewayV2ApiClient.allFriends(token, 0, 2, null, null);

        assertEquals(2, response.getContent().size());

        final UserJson actualInvitation = response.getContent().getFirst();
        final UserJson actualFriend = response.getContent().getLast();

        assertEquals(expectedFriend.id(), actualFriend.id());
        assertEquals(expectedInvitation.id(), actualInvitation.id());
    }

    @DisplayName("Для пользователя должен возвращаться список друзей и входящих предложений дружить c фильтрацией по username")
    @ApiLogin
    @User(friends = 2, incomeInvitations = 2)
    @Test
    void friendsAndIncomeInvitationsShouldBeReturnedFilteredByUsername(UserJson user, @Token String token) {
        final int pageNumber = 0;
        final int size = 4;
        final UserJson searchedFriend = user.testData().friends().getFirst();

        final RestResponsePage<UserJson> response = gatewayV2ApiClient.allFriends(
                token,
                pageNumber,
                size,
                null,
                searchedFriend.username()
        );

        final UserJson actualFriend = response.getContent().getFirst();

        assertEquals(searchedFriend.id(), actualFriend.id());
        assertEquals(searchedFriend.username(), actualFriend.username());
        assertEquals(FriendshipStatus.FRIEND, actualFriend.friendshipStatus());
    }
}
