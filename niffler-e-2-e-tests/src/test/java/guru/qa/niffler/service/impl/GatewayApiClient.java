package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.rest.FriendJson;
import guru.qa.niffler.model.rest.UserJson;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GatewayApiClient extends RestClient {

    private final GatewayApi gatewayApi;

    public GatewayApiClient() {
        super(CONFIG.gatewayUrl());
        this.gatewayApi = create(GatewayApi.class);
    }

    @Step("Send GET request /api/friends/all to niffler-gateway")
    @Nonnull
    public List<UserJson> allFriends(String bearerToken,
                                     @Nullable String searchQuery) {
        final Response<List<UserJson>> response;
        try {
            response = gatewayApi.allFriends(bearerToken, searchQuery)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Step("Send DELETE request api/friends/remove to niffler-gateway")
    public void removeFriend(String bearerToken, String username) {
        final Response<Void> response;

        try {
            response = gatewayApi.removeFriend(bearerToken, username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        assertEquals(200, response.code());
    }

    @Step("Send POST request api/invitations/accept to niffler-gateway")
    @Nonnull
    public UserJson acceptInvitation(String bearerToken, String friendUsername) {
        final Response<UserJson> response;

        try {
            response = gatewayApi.acceptInvitation(
                    bearerToken,
                    new FriendJson(
                            friendUsername
                    )
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        assertEquals(200, response.code());

        return Objects.requireNonNull(response.body());
    }

    @Step("Send POST request api/invitations/decline to niffler-gateway")
    @Nonnull
    public UserJson declineInvitation(String bearerToken, String friendUsername) {
        final Response<UserJson> response;

        try {
            response = gatewayApi.declineInvitation(
                    bearerToken,
                    new FriendJson(
                            friendUsername
                    )
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        assertEquals(200, response.code());

        return Objects.requireNonNull(response.body());
    }

    @Step("Send POST request api/invitations/decline to niffler-gateway")
    @Nonnull
    public UserJson sendInvitation(String bearerToken, String friendUsername) {
        final Response<UserJson> response;

        try {
            response = gatewayApi.sendInvitation(
                    bearerToken,
                    new FriendJson(
                            friendUsername
                    )
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        assertEquals(200, response.code());

        return Objects.requireNonNull(response.body());
    }

    @Step("Send GET request api/users/all to niffler-gateway")
    @Nonnull
    public List<UserJson> allPeople(String bearerToken, @Nullable String searchQuery) {
        final Response<List<UserJson>> response;

        try {
            response = gatewayApi.allUsers(
                    bearerToken,
                    searchQuery
            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(200, response.code());

        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            return Collections.emptyList();
        }
    }
}
