package guru.qa.niffler.api;

import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface SpendApi {

    @POST("/internal/spends/add")
    Call<SpendJson> addSpend(@Body SpendJson spend);

    @PATCH("/internal/spends/edit")
    Call<SpendJson> editSpend(@Body SpendJson spend);

    @GET("/internal/spends/{id}")
    Call<SpendJson> getSpend(@Path("id") String id,
                             @Query("username") String username);

    @GET("/internal/spends/all")
    Call<List<SpendJson>> getAllSpend(@Query("username") String username,
                                      @Query("filterCurrency") CurrencyValues filterCurrency,
                                      @Query("from") String from,
                                      @Query("to") String to);

    @DELETE("/internal/spends/remove")
    Call<Void> removeSpends(@Query("username") String username,
                            @Query("ids") List<String> ids
    );

    @POST("/internal/categories/add")
    Call<CategoryJson> addCategory(@Body CategoryJson addCategory);

    @PATCH("/internal/categories/update")
    Call<CategoryJson> updateCategory(@Body CategoryJson addCategory);

    @GET("/internal/categories/all")
    Call<List<CategoryJson>> getAllCategories(@Query("username") String username,
                                              @Query("excludeArchived") boolean excludeArchived);

}