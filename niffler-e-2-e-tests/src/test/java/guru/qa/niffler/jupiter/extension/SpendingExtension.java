package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nonnull;
import java.util.*;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

    private final SpendClient spendClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (ArrayUtils.isNotEmpty(userAnno.spendings())) {
                        final UserJson user = UserExtension.createdUser();

                        final String username = user != null
                                ? user.username()
                                : userAnno.username();

                        final List<SpendJson> createdSpends = new ArrayList<>();

                        for (Spending spendAnno : userAnno.spendings()) {
                            SpendJson spend = new SpendJson(
                                    null,
                                    new Date(),
                                    new CategoryJson(
                                            null,
                                            spendAnno.category(),
                                            username,
                                            false
                                    ),
                                    spendAnno.currency(),
                                    spendAnno.amount(),
                                    spendAnno.description(),
                                    username
                            );

                            createdSpends.add(
                                    spendClient.createSpend(spend)
                            );
                        }
                        if (user != null) {
                            user.testData().spends().addAll(
                                    createdSpends
                            );
                        } else {
                            context.getStore(NAMESPACE).put(
                                    context.getUniqueId(),
                                    createdSpends
                            );
                        }
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdSpends(extensionContext).toArray(SpendJson[]::new);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static List<SpendJson> createdSpends(ExtensionContext extensionContext) {
        return Optional.ofNullable(extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), List.class))
                .orElse(Collections.emptyList());
    }
}