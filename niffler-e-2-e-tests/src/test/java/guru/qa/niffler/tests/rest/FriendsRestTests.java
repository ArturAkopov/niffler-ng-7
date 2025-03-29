package guru.qa.niffler.tests.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.FriendshipStatus;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.GatewayApiClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static guru.qa.niffler.model.FriendshipStatus.INVITE_RECEIVED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTest
public class FriendsRestTests {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();
    private final UsersApiClient usersApiClient = new UsersApiClient();

    @DisplayName("Для пользователя должен возвращаться список друзей и входящих предложений дружить")
    @ApiLogin
    @User(friends = 1, incomeInvitations = 1)
    @Test
    void friendsAndIncomeInvitationsListShouldBeReturned(UserJson user, @Token String token) {
        final UserJson expectedFriend = user.testData().friends().getFirst();
        final UserJson expectedInvitation = user.testData().incomeInvitations().getFirst();
        final List<UserJson> response = gatewayApiClient.allFriends(token, null);
        assertEquals(2, response.size());
        final UserJson actualInvitation = response.getFirst();
        final UserJson actualFriend = response.getLast();
        assertEquals(expectedFriend.id(), actualFriend.id());
        assertEquals(expectedInvitation.id(), actualInvitation.id());
    }

    @DisplayName("Дружба должна удаляться")
    @ApiLogin
    @User(friends = 1)
    @Test
    void shouldBeAbleToRemoveFriendship(UserJson user, @Token String token) {
        final UserJson friendToRemove = user.testData().friends().getFirst();

        gatewayApiClient.removeFriend(
                token,
                friendToRemove.username()
        );

        final List<UserJson> response = gatewayApiClient.allFriends(
                token,
                null
        );

        assertEquals(0, response.size());
    }

    @DisplayName("Запрос на дружбу должен приниматься")
    @ApiLogin
    @User(incomeInvitations = 1)
    @Test
    void shouldBeAbleToAcceptFriendship(UserJson user, @Token String token) {
        final UserJson invitationToAccept = user.testData().incomeInvitations().getFirst();

        gatewayApiClient.acceptInvitation(
                token,
                invitationToAccept.username()
        );

        final List<UserJson> response = gatewayApiClient.allFriends(
                token,
                null
        );

        final UserJson acceptedFriend = response.getFirst();

        assertEquals(1, response.size());
        assertEquals(invitationToAccept.username(), acceptedFriend.username());
        assertEquals(FriendshipStatus.FRIEND, acceptedFriend.friendshipStatus());
    }

    @DisplayName("Запрос на дружбу должен отклоняться")
    @ApiLogin
    @User(incomeInvitations = 1)
    @Test
    void shouldBeAbleToDeclineFriendship(UserJson user, @Token String token) {
        final UserJson invitationToDecline = user.testData().incomeInvitations().getFirst();

        UserJson declinedFriend = gatewayApiClient.declineInvitation(
                token,
                invitationToDecline.username()
        );

        final List<UserJson> friends = gatewayApiClient.allFriends(
                token,
                null
        );

        final List<UserJson> incomeInvitations = friends
                .stream()
                .filter(userJson -> userJson.friendshipStatus().equals(INVITE_RECEIVED))
                .toList();

        assertEquals(invitationToDecline.username(), declinedFriend.username());
        assertEquals(0, friends.size());
        assertEquals(0, incomeInvitations.size());
    }

    @DisplayName("После отправки приглашения дружить, должны создаваться исходящие и входящие предложения дружить")
    @ApiLogin
    @User
    @Test
    void shouldCreateIncomeAndOutcomeInvitationsAfterSendingFriendRequest(UserJson sender, @Token String token) {
        final String addresseeName = RandomDataUtils.randomUsername();

        final UserJson addressee = usersApiClient.createUser(
                addresseeName,
                "12345"
        );

        gatewayApiClient.sendInvitation(
                token,
                addresseeName
        );

        final UserJson actualOutcomeInvitation = gatewayApiClient.allPeople(
                token,
                addressee.username()
        ).getFirst();

        final UserJson incomeInvitationActual = usersApiClient.friends(
                addresseeName,
                sender.username()
        ).getFirst();

        assertEquals(addresseeName, actualOutcomeInvitation.username());
        assertEquals(sender.username(), incomeInvitationActual.username());
    }
}
