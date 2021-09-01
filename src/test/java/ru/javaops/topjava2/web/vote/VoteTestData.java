package ru.javaops.topjava2.web.vote;

import ru.javaops.topjava2.model.Vote;
import ru.javaops.topjava2.web.MatcherFactory;

import java.time.LocalDate;

public class VoteTestData {
    public static final MatcherFactory.Matcher<Vote> MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Vote.class);

    public static final Vote vote4 = new Vote(4, "Voting cafe", "Mamalyga - 2.00; Shaslyk - 1.50; Beer - 2.33; ", 4, LocalDate.now());

}
