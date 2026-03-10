package com.xenoamess.cyan_potion.civilization.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.xenoamess.cyan_potion.civilization.GameDateManager;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.generator.RandomPersonGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 我们这个框架假设死人不会复活
 * <p>
 * 既 死亡是一个纯粹的单向的过程
 */
@Slf4j
public class PersonCache {

    /**
     * 全部活人加死人的池
     */
    public static final ConcurrentHashMap<String, Person> ALL_PERSON_CACHE = new ConcurrentHashMap<>();

    /**
     * 所有活人都在这个池。这个池里允许有少量死人，但是要有机制尽可能快的清理掉，而又不频繁到影响性能
     * 考虑gc？
     * 不 先考虑手工
     */
    public static final ConcurrentHashMap<String, Person> LIKELY_ALIVE_PERSON_CACHE = new ConcurrentHashMap<>();

    public static final ConcurrentSkipListSet<String> NEW_DEAD_PERSON_ID_MARK = new ConcurrentSkipListSet<>();

    public static void generatePersons(@NotNull GameDateManager gameDateManager) {
        RandomPersonGenerator generator = new RandomPersonGenerator();
        List<Person> persons = generator.generateMultiple(100, gameDateManager.getCurrentDate());
        for (Person person : persons) {
            PersonCache.LIKELY_ALIVE_PERSON_CACHE.put(person.getId(), person);
            PersonCache.ALL_PERSON_CACHE.put(person.getId(), person);
        }
        log.info("Generated {} persons at game date {}", persons.size(), gameDateManager.getFormattedDate());
    }

    public static void update() {
        for (String deadPersonId : NEW_DEAD_PERSON_ID_MARK) {
            LIKELY_ALIVE_PERSON_CACHE.remove(deadPersonId);
        }
    }

    @NotNull
    public static Collection<Person> getAllAliveAndDeadPersonCollection() {
        return PersonCache.ALL_PERSON_CACHE.values();
    }

    @NotNull
    public static Stream<Person> getAllAliveAndDeadPersonStream() {
        return PersonCache.ALL_PERSON_CACHE.values().parallelStream();
    }

    public static Collection<Person> getAllAlivePersonWithSomeDeadPersonCollection() {
        return PersonCache.LIKELY_ALIVE_PERSON_CACHE.values();
    }

    public static Stream<Person> getAllAlivePersonWithSomeDeadPersonStream() {
        return PersonCache.LIKELY_ALIVE_PERSON_CACHE.values().parallelStream();
    }

    public static Stream<Person> getAllAlivePersonStream() {
        return PersonCache.LIKELY_ALIVE_PERSON_CACHE.values().parallelStream().filter(
                new Predicate<Person>() {
                    @Override
                    public boolean test(Person person) {
                        if (person.isAlive()) {
                            return true;
                        }
                        NEW_DEAD_PERSON_ID_MARK.add(person.getId());
                        return false;
                    }
                }
        );
    }

}
