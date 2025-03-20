package guru.qa.niffler.api;

import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.rest.UserJson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import javax.annotation.Nullable;

public interface GatewayV2Api {

    @GET("api/v2/friends/all")
    Call<RestResponsePage<UserJson>> allFriends(@Header("Authorization") String bearerToken,
                                                @Query("page") int page,
                                                @Query("size") int size,
                                                @Query("sort") @Nullable String sort,
                                                @Query("searchQuery") @Nullable String searchQuery);

}
