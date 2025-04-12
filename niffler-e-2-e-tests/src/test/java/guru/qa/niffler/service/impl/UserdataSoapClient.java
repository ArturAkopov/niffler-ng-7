package guru.qa.niffler.service.impl;

import guru.qa.jaxb.userdata.*;
import guru.qa.niffler.api.UserdataSoapApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.converter.SoapConverterFactory;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.rest.UserJson;
import io.qameta.allure.Step;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UserdataSoapClient extends RestClient {

    private static final Config CONFIG = Config.getInstance();
    private final UserdataSoapApi userdataSoapApi;

    public UserdataSoapClient() {
        super(CONFIG.userdataUrl(), false, SoapConverterFactory.create("niffler-userdata"), HttpLoggingInterceptor.Level.BODY);
        this.userdataSoapApi = create(UserdataSoapApi.class);
    }


    @NotNull
    @Step("Get current user info using SOUP")
    public UserResponse currentUser(UserJson userJson) throws IOException {
        CurrentUserRequest request = new CurrentUserRequest();
        request.setUsername(userJson.username());
        return requireNonNull(userdataSoapApi.currentUser(request).execute().body());
    }

    @NotNull
    @Step("Update user info using SOUP")
    public UserResponse updateUser(UpdateUserRequest request) throws IOException {
        return requireNonNull(userdataSoapApi.updateUserInfo(request).execute().body());
    }

    @NotNull
    @Step("Get all user info order by username using SOUP")
    public UsersResponse allUsersSort(AllUsersRequest request) throws IOException {
        return requireNonNull(userdataSoapApi.allUsers(request).execute().body());
    }

    @NotNull
    @Step("Get all user info page using SOUP")
    public UsersResponse allUsersPage(AllUsersPageRequest request) throws IOException {
        return requireNonNull(userdataSoapApi.allUsers(request).execute().body());
    }

    @NotNull
    @Step("Get all friends info for user order by username using SOUP")
    public UsersResponse allFriendsSort(String username, String sortUsername) throws IOException {
        FriendsRequest request = new FriendsRequest();
        request.setUsername(username);
        request.setSearchQuery(sortUsername);
        return requireNonNull(userdataSoapApi.friends(request).execute().body());
    }

    @NotNull
    @Step("Get all friends info page for user using SOUP")
    public UsersResponse allFriendsPage(UserJson userJson, int page, int size) throws IOException {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(page);
        pageInfo.setSize(size);
        FriendsPageRequest request = new FriendsPageRequest();
        request.setUsername(userJson.username());
        request.setPageInfo(pageInfo);
        return requireNonNull(userdataSoapApi.friendsPage(request).execute().body());
    }

    @Step("Remove friend for user using SOUP")
    public void removeFriend(String username, String removedFriendUsername) throws IOException {
        RemoveFriendRequest request = new RemoveFriendRequest();
        request.setUsername(username);
        request.setFriendToBeRemoved(removedFriendUsername);
        userdataSoapApi.removeFriend(request).execute();
    }

    @NotNull
    @Step("Send invitation friend using SOUP")
    public UserResponse sendInvitationFriend(String username, String friendName) throws IOException {
        SendInvitationRequest request = new SendInvitationRequest();
        request.setUsername(username);
        request.setFriendToBeRequested(friendName);
        return requireNonNull(userdataSoapApi.sendInvitation(request).execute().body());
    }

    @NotNull
    @Step("Accept invitation friend using SOUP")
    public UserResponse acceptInvitationFriend(String username, String incomeInvitationUsername) throws IOException {
        AcceptInvitationRequest request = new AcceptInvitationRequest();
        request.setUsername(username);
        request.setFriendToBeAdded(incomeInvitationUsername);
        return requireNonNull(userdataSoapApi.acceptInvitation(request).execute().body());
    }

    @NotNull
    @Step("Decline invitation friend using SOUP")
    public UserResponse declineInvitationFriend(String username, String incomeInvitationUsername) throws IOException {
        DeclineInvitationRequest request = new DeclineInvitationRequest();
        request.setUsername(username);
        request.setInvitationToBeDeclined(incomeInvitationUsername);
        return requireNonNull(userdataSoapApi.declineInvitation(request).execute().body());
    }


}