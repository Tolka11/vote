package ru.javaops.topjava2.web.vote;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaops.topjava2.repository.ChoiceRepository;
import ru.javaops.topjava2.util.JsonUtil;
import ru.javaops.topjava2.web.AbstractControllerTest;

import java.time.LocalTime;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaops.topjava2.web.user.UserTestData.USER_MAIL;
import static ru.javaops.topjava2.web.vote.VoteTestData.*;

public class VoteControllerTest extends AbstractControllerTest {

    private static final String REST_URL = VoteController.REST_URL + '/';

    @Autowired
    private ChoiceRepository choiceRepository;


    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAllToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER.contentJson(votes));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void makeChoice() throws Exception {
        if (LocalTime.now().isAfter(VoteController.REVOTE_DEADLINE)) {
            perform(MockMvcRequestBuilders.put(REST_URL + 3)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.writeValue("")))
                    .andDo(print())
                    .andExpect(status().isNotAcceptable());
        } else {
            perform(MockMvcRequestBuilders.put(REST_URL + 3)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.writeValue("")))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            CHOICE_MATCHER.assertMatch(choiceRepository.getById(4), choice4);
        }
    }

    // Test for rating doesn't work, because it needs a background process RatingMaker
}
