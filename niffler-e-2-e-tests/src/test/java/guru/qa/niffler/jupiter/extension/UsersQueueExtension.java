package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUsers(String name, String password, boolean empty) {
    }

    private static final Queue<StaticUsers> EMPTY_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUsers> NOT_EMPTY_USERS = new ConcurrentLinkedQueue<>();

    static {
        EMPTY_USERS.add(new StaticUsers("Artur2", "12345", true));
        NOT_EMPTY_USERS.add(new StaticUsers("Artur", "12345", false));
        NOT_EMPTY_USERS.add(new StaticUsers("Artur1", "12345", false));
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UserType {
        boolean empty() default true;
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> AnnotationSupport.isAnnotated(parameter, UserType.class))
                .findFirst()
                .map(parameter -> parameter.getAnnotation(UserType.class))
                .ifPresent(
                        userType -> {
                            Optional<StaticUsers> user = Optional.empty();
                            StopWatch stopWatch = StopWatch.createStarted();
                            while (user.isEmpty() && stopWatch.getTime(TimeUnit.SECONDS) < 30) {
                                user = userType.empty()
                                        ? Optional.ofNullable(EMPTY_USERS.poll())
                                        : Optional.ofNullable(NOT_EMPTY_USERS.poll());
                            }
                            Allure.getLifecycle().updateTestCase(
                                    testCase ->
                                            testCase.setStart(new Date().getTime())
                            );
                            user.ifPresentOrElse(
                                    u ->
                                            context.getStore(NAMESPACE)
                                                    .put(context.getUniqueId(), u),
                                    () ->
                                            new IllegalStateException("Can't find user after 30 sec")
                            );
                        }
                );
    }

    @Override
    public void afterEach(ExtensionContext context) {
        StaticUsers user = context.getStore(NAMESPACE).get(context.getUniqueId(), StaticUsers.class);
        if (user.empty()) {
            EMPTY_USERS.add(user);
        } else {
            NOT_EMPTY_USERS.add(user);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUsers.class) &&
                AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public StaticUsers resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), StaticUsers.class);
    }

}