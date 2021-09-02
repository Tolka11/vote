package ru.javaops.topjava2.web.vote;

import ru.javaops.topjava2.model.Choice;
import ru.javaops.topjava2.model.Vote;
import ru.javaops.topjava2.to.VoteTo;
import ru.javaops.topjava2.web.MatcherFactory;

import java.time.LocalDate;
import java.util.List;

public class VoteTestData {
    public static final MatcherFactory.Matcher<Vote> MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Vote.class);
    public static final MatcherFactory.Matcher<VoteTo> TO_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(VoteTo.class);
    public static final MatcherFactory.Matcher<Choice> CHOICE_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Choice.class);

    public static final Vote vote1 = new Vote(1, "Bigrest", "Paste, tiramisu, wine - 20.00; ", 1, LocalDate.now());
    public static final Vote vote2 = new Vote(2, "Middlecafe", "Soup, cutlet, compote - 10.00; ", 2, LocalDate.now());
    public static final Vote vote3 = new Vote(3, "Littleponchik", "Donut - 1.00; Tea - 0.50; ", 3, LocalDate.now());
    public static final Vote vote4 = new Vote(4, "Voting cafe", "Mamalyga - 2.00; Shaslyk - 1.50; Beer - 2.33; ", 4, LocalDate.now());

    public static final VoteTo voteTo1 = new VoteTo(1, vote1.getName(), vote1.getRestaurantId(), vote1.getMenu(), 0);
    public static final VoteTo voteTo2 = new VoteTo(2, vote2.getName(), vote2.getRestaurantId(), vote2.getMenu(), 1);
    public static final VoteTo voteTo3 = new VoteTo(3, vote3.getName(), vote3.getRestaurantId(), vote3.getMenu(), 1);

    public static final List<Vote> votes = List.of(vote1, vote2, vote3);
    public static final List<VoteTo> rating = List.of(voteTo2, voteTo3, voteTo1);

    public static final Choice choice4 = new Choice(4, 1, 3, LocalDate.now());

    public static Vote getUpdated() {
        return new Vote(1, "Rebranded", "pelemeshki - 2.00; ", 1, LocalDate.now());
    }
}