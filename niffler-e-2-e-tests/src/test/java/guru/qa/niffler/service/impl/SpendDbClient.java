package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = new SpendRepositoryJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Nonnull
    @Override
    @Step("Создание траты через БД")
    public SpendJson createSpend(SpendJson spend) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> SpendJson.fromEntity(
                                spendRepository.createSpend(
                                        SpendEntity.fromJson(spend)
                                )
                        )
                )
        );
    }

    @Nonnull
    @Override
    @Step("Создание категории через БД")
    public CategoryJson createCategory(CategoryJson category) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> CategoryJson.fromEntity(
                                spendRepository.createCategory(
                                        CategoryEntity.fromJson(category)
                                )
                        )
                )
        );
    }

    @NotNull
    @Override
    @Step("Обновление категории через БД")
    public CategoryJson updateCategory(CategoryJson category) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> CategoryJson.fromEntity(
                                spendRepository.updateCategory(
                                        CategoryEntity.fromJson(category)
                                )
                        )
                )
        );
    }

    @Override
    @Step("Удаление категории через БД")
    public void removeCategory(CategoryJson category) {
        xaTransactionTemplate.execute(
                () -> {
                    spendRepository.removeCategory(
                            CategoryEntity.fromJson(category)
                    );
                    return null;
                }
        );
    }
}
