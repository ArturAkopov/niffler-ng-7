package guru.qa.niffler.api;

import guru.qa.niffler.model.allure.AllureResults;
import guru.qa.niffler.model.allure.Project;
import retrofit2.Call;
import retrofit2.http.*;

public interface AllureApi {

    @POST("allure-docker-service/projects")
    Call<Void> createProject(@Body Project project);

    @POST("allure-docker-service/send-results")
    Call<Void> sendResults(@Query("project_id") String projectId,
                           @Body AllureResults allureResults);

    @GET("allure-docker-service/generate-report")
    Call<Void> generateReport(@Query("project_id") String projectId,
                              @Query("execution_name") String executionName,
                              @Query("execution_from") String executionFrom,
                              @Query("execution_type") String executionType);

}
