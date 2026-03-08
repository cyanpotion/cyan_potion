package com.xenoamess.cyan_potion.civilization.cache;

import com.xenoamess.cyan_potion.civilization.character.Person;

import java.util.concurrent.ConcurrentHashMap;

public class PersonCache {

    public static final ConcurrentHashMap<String, Person> PERSON_CACHE = new ConcurrentHashMap<>();


}
