package guru.qa.niffler.tests.soap;

import guru.qa.jaxb.userdata.*;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.SoupTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.UserdataSoapClient;
import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static guru.qa.jaxb.userdata.FriendshipStatus.INVITE_SENT;
import static org.junit.jupiter.api.Assertions.*;

@SoupTest
@ParametersAreNonnullByDefault
public class SoapUsersTests {

    private final UserdataSoapClient userdataSoapClient = new UserdataSoapClient();

    @Test
    @User
    void getCurrentUserTest(UserJson userJson) throws IOException {
        UserResponse response = userdataSoapClient.currentUser(userJson);
        assertEquals(
                userJson.username(),
                response.getUser().getUsername()
        );
    }

    @Test
    @User(
            friends = 3
    )
    void getAllFriendsPageForUserTest(UserJson userJson) throws IOException {
        UsersResponse response = userdataSoapClient.allFriendsPage(userJson, 1, 10);
        assertEquals(1, response.getTotalPages().intValue());
        assertEquals(3, response.getTotalElements().intValue());
    }

    @Test
    @User(
            friends = 3
    )
    void getAllFriendsForUserWithSortByUsernameTest(UserJson userJson) throws IOException {
        final String sortUsername = userJson.testData().friends().getFirst().username();
        UsersResponse response = userdataSoapClient.allFriendsSort(userJson.username(), sortUsername);
        assertEquals(1, response.getUser().size());
        assertTrue(userJson.testData().friends().stream()
                .map(UserJson::username)
                .toList().contains(response.getUser().getFirst().getUsername()));
    }

    @Test
    @User(
            friends = 1
    )
    void removeFriendTest(UserJson userJson) throws IOException {
        final String removedFriend = userJson.testData().friends().getFirst().username();
        userdataSoapClient.removeFriend(userJson.username(), removedFriend);
        UsersResponse response = userdataSoapClient.allFriendsSort(userJson.username(), removedFriend);
        assertEquals(0, response.getUser().size());
    }

    @Test
    @User(
            incomeInvitations = 1
    )
    void acceptIncomeInvitationFriendTest(UserJson userJson) throws IOException {
        final String friendUsername = userJson.testData().incomeInvitations().getFirst().username();
        userdataSoapClient.acceptInvitationFriend(userJson.username(), friendUsername);
        UsersResponse response = userdataSoapClient.allFriendsSort(userJson.username(), friendUsername);
        assertEquals(1, response.getUser().size());
        assertEquals(friendUsername, response.getUser().getFirst().getUsername());

    }

    @Test
    @User(
            incomeInvitations = 1
    )
    void declineIncomeInvitationFriendTest(UserJson userJson) throws IOException {
        final String friendUsername = userJson.testData().incomeInvitations().getFirst().username();
        userdataSoapClient.declineInvitationFriend(userJson.username(), friendUsername);
        UsersResponse response = userdataSoapClient.allFriendsSort(userJson.username(), friendUsername);
        assertEquals(0, response.getUser().size());
    }

    @Test
    @User
    void sendInvitationFriendTest(UserJson userJson) throws IOException {
        final String username = "Artur";
        final String friendName = userJson.username();
        UserResponse response = userdataSoapClient.sendInvitationFriend(username, friendName);
        assertEquals(INVITE_SENT, response.getUser().getFriendshipStatus());
    }

}
