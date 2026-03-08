package com.xenoamess.cyan_potion.civilization.util;

import java.time.LocalDate;

public class TimeUtil {

    public static int calculateAge(LocalDate startDate, LocalDate endDate) {
        // Fast calculation using year difference (no ChronoUnit overhead)
        int age = endDate.getYear() - startDate.getYear();
        // Adjust if birthday hasn't occurred this year
        if (endDate.getMonthValue() < startDate.getMonthValue() ||
                (endDate.getMonthValue() == startDate.getMonthValue() &&
                        endDate.getDayOfMonth() < startDate.getDayOfMonth())) {
            age--;
        }
        return Math.max(0, age);
    }

}
