package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AllureApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.allure.AllureResults;
import guru.qa.niffler.model.allure.Project;
import io.qameta.allure.Step;
import retrofit2.Response;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AllureApiClient extends RestClient {

    private final AllureApi allureApi;

    public AllureApiClient() {
        super(CONFIG.allureDockerServiceUrl());
        this.allureApi = create(AllureApi.class);
    }

    @Step("Создание allure проекта")
    public void createProject(String projectId) {
        final Response<Void> response;
        try {
            response = allureApi.createProject(
                    new Project(projectId)
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code());
    }

    @Step("Отправка allure-results")
    public void sendResults(String projectId, AllureResults allureResults) {
        final Response<Void> response;
        try {
            response = allureApi.sendResults(
                    projectId,
                    allureResults
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    @Step("Генерация allure отчета")
    public void generateReport(String projectId,
                               String executionName,
                               String executionFrom,
                               String executionType) {
        final Response<Void> response;
        try {
            response = allureApi.generateReport(
                    projectId,
                    executionName,
                    executionFrom,
                    executionType
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

}
