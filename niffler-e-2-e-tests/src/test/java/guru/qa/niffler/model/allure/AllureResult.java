package guru.qa.niffler.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AllureResult(
        @JsonProperty("file_name")
        String filename,

        @JsonProperty("content_base64")
        String contentBase64
) {
}
