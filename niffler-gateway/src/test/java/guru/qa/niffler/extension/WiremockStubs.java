package guru.qa.niffler.extension;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(WiremockExtension.class)
@Repeatable(WiremockStubs.List.class)
public @interface WiremockStubs {
    String jsonFile();
    String urlPath();
    QueryParam[] queryParams() default {};

    @interface QueryParam {
        String name();
        String value();
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        WiremockStubs[] value();
    }
}
