package guru.qa.niffler.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static guru.qa.niffler.model.FriendshipStatus.FRIEND;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class FriendsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Sql(scripts = "/allFriendsShouldBeReturned.sql")
    @Test
    void allFriendsShouldBeReturned() throws Exception {
        mockMvc.perform(get("/internal/friends/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "Artur")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value("Artur2"))
                .andExpect(jsonPath("$[0].friendshipStatus").value(FRIEND.name()))
                .andExpect(jsonPath("$[1].username").value("Artur1"))
                .andExpect(jsonPath("$[1].friendshipStatus").value(FRIEND.name()));
    }

    @Sql(scripts = "/friendShouldBeRemoved.sql")
    @Test
    void friendShouldBeRemoved() throws Exception {
        mockMvc.perform(delete("/internal/friends/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "Artur")
                        .param("targetUsername", "Artur1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

}
