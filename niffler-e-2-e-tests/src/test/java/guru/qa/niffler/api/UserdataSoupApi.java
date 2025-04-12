package guru.qa.niffler.api;

import guru.qa.jaxb.userdata.*;
import retrofit2.Call;
import retrofit2.http.*;


public interface UserdataSoupApi {

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("/ws")
    Call<UserResponse> currentUser(@Body CurrentUserRequest currentUserRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("/ws")
    Call<UserResponse> updateUserInfo(@Body UpdateUserRequest user);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("/ws")
    Call<UsersResponse> allUsers(@Body AllUsersRequest usersRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("/ws")
    Call<UsersResponse> allUsers(@Body AllUsersPageRequest allUsersPageRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("/ws")
    Call<UsersResponse> friends(@Body FriendsRequest friendsRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("/ws")
    Call<UsersResponse> friendsPage(@Body FriendsPageRequest friendsPageRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("/ws")
    Call<Void> removeFriend(@Body RemoveFriendRequest removeFriendRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("/ws")
    Call<UserResponse> sendInvitation(@Body SendInvitationRequest sendInvitationRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("/ws")
    Call<UserResponse> acceptInvitation(@Body AcceptInvitationRequest acceptInvitationRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("/ws")
    Call<UserResponse> declineInvitation(@Body DeclineInvitationRequest declineInvitationRequest);

}