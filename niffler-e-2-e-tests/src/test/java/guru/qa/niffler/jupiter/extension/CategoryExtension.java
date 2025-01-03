package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;


public class CategoryExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {

    private static final ExtensionContext.Namespace CATEGORY_NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

    private final SpendApiClient spendApiClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(anno -> {
                    if (anno.categories().length > 0) {
                        CategoryJson categoryJson = new CategoryJson(
                                null,
                                RandomDataUtils.randomCategoryName(),
                                anno.username(),
                                false
                        );

                        CategoryJson createCategory = spendApiClient.createCategory(categoryJson);

                        if (anno.categories()[0].archived()) {
                            CategoryJson archivedCategory = new CategoryJson(
                                    createCategory.id(),
                                    createCategory.name(),
                                    createCategory.username(),
                                    true
                            );
                            createCategory = spendApiClient.updateCategory(archivedCategory);
                        }

                        context.getStore(CategoryExtension.CATEGORY_NAMESPACE).put(context.getUniqueId(), createCategory);
                    }
                });
    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(CategoryExtension.CATEGORY_NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        CategoryJson categoryJson = context.getStore(CategoryExtension.CATEGORY_NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
        if (categoryJson !=null && !categoryJson.archived()) {
            CategoryJson archivedJson = new CategoryJson(
                    categoryJson.id(),
                    categoryJson.name(),
                    categoryJson.username(),
                    true
            );
            spendApiClient.updateCategory(archivedJson);
        }

    }
}
