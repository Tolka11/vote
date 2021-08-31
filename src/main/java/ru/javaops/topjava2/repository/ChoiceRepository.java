package ru.javaops.topjava2.repository;

import ru.javaops.topjava2.model.Choice;

import java.time.LocalDate;
import java.util.List;

public interface ChoiceRepository extends BaseRepository<Choice> {
    public List<Choice> findAllByDate(LocalDate date);

    public List<Choice> findAllByDateOrderByIdAsc(LocalDate date);
}
