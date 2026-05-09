package com.wastely.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.wastely.dao.ScheduleDao;
import com.wastely.model.CollectionInfo;
import com.wastely.model.Schedule;
import com.wastely.store.DataChangeBus;
import com.wastely.store.DataTopics;

/**
 * Service layer for Schedule management
 * Handles filtering, sorting, validation, and CRUD operations
 */
public class ScheduleService {

    private final ScheduleDao dao = new ScheduleDao();

    /* =========================
       BASE DATA ACCESS
       ========================= */

    public List<Schedule> getAllSchedules() {
        List<Schedule> schedules = dao.findAll();
        return schedules == null ? Collections.emptyList() : schedules;
    }

    public Schedule getById(int id) {
        return getAllSchedules().stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /* =========================
       FILTERING
       ========================= */

    public List<Schedule> getByStatus(String status) {
        return getAllSchedules().stream()
                .filter(s -> equalsIgnoreCase(s.getStatus(), status))
                .collect(Collectors.toList());
    }

    public List<Schedule> getByBarangay(String barangayName) {
        String query = isBlank(barangayName)
                ? ""
                : barangayName.trim().toLowerCase(Locale.ENGLISH);

        return getAllSchedules().stream()
                .filter(s -> s.getBarangayName() != null)
                .filter(s -> s.getBarangayName()
                        .toLowerCase(Locale.ENGLISH)
                        .contains(query))
                .collect(Collectors.toList());
    }

    /* =========================
       VALIDATION
       ========================= */

    public boolean isScheduleValid(Schedule schedule) {
        return schedule != null
                && !isBlank(schedule.getBarangayName())
                && schedule.getDate() != null
                && schedule.getTime() != null
                && !isBlank(schedule.getStatus());
    }

    /* =========================
       SORTING
       ========================= */

    public List<Schedule> sortByDateAsc() {
        return getAllSchedules().stream()
                .sorted(Comparator.comparing(
                        Schedule::getDate,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).thenComparing(
                        Schedule::getTime,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .collect(Collectors.toList());
    }

    public List<Schedule> sortByDateDesc() {
        return getAllSchedules().stream()
                .sorted(Comparator.comparing(
                        Schedule::getDate,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ).thenComparing(
                        Schedule::getTime,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .collect(Collectors.toList());
    }

    /* =========================
       COUNTING
       ========================= */

    public int countByStatus(String status) {
        int count = (int) getAllSchedules().stream()
                .filter(s -> equalsIgnoreCase(s.getStatus(), status))
                .count();
                if (count == 0) return 0;
        return count;
    }

    public int countByBarangay(String barangayName) {
        return getByBarangay(barangayName).size();
    }

    public boolean isEmpty() {
        return getAllSchedules().isEmpty();
    }

    public int totalSchedules() {
        return getAllSchedules().size();
    }

    /* =========================
       SCHEDULE FILTER (FIXED)
       ========================= */

    // NEW CLEAN VERSION (recommended)
    public List<String> getBarangaysScheduledForDay(LocalDate weekStart,
                                                     LocalDate weekEnd,
                                                     DayOfWeek day) {

        if (weekStart == null || weekEnd == null || day == null) {
            return Collections.emptyList();
        }

        return getAllSchedules().stream()
                .filter(s -> s.getDate() != null)
                .filter(s -> !s.getDate().isBefore(weekStart))
                .filter(s -> !s.getDate().isAfter(weekEnd))
                .filter(s -> s.getDate().getDayOfWeek() == day)
                .map(Schedule::getBarangayName)
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.toList());
    }

    // BACKWARD COMPATIBLE (your UI still works)
    public List<String> getBarangaysScheduledForDay(LocalDate weekStart,
                                                     LocalDate weekEnd,
                                                     String dayLabel) {

        return getBarangaysScheduledForDay(
                weekStart,
                weekEnd,
                parseDay(dayLabel)
        );
    }

    private DayOfWeek parseDay(String dayLabel) {
        if (isBlank(dayLabel)) return null;

        String normalized = dayLabel.trim().toUpperCase(Locale.ENGLISH);

        return switch (normalized) {
            case "MON", "MONDAY" -> DayOfWeek.MONDAY;
            case "TUE", "TUESDAY" -> DayOfWeek.TUESDAY;
            case "WED", "WEDNESDAY" -> DayOfWeek.WEDNESDAY;
            case "THU", "THURSDAY" -> DayOfWeek.THURSDAY;
            case "FRI", "FRIDAY" -> DayOfWeek.FRIDAY;
            case "SAT", "SATURDAY" -> DayOfWeek.SATURDAY;
            case "SUN", "SUNDAY" -> DayOfWeek.SUNDAY;
            default -> null;
        };
    }

    /* =========================
       CRUD
       ========================= */

    public boolean saveSchedule(Schedule schedule) {
        boolean success = isScheduleValid(schedule) && dao.saveOrUpdate(schedule);
        publish(success);
        return success;
    }

    public boolean updateSchedule(Schedule schedule) {
        boolean success = isScheduleValid(schedule) && dao.saveOrUpdate(schedule);
        publish(success);
        return success;
    }

    public boolean deleteSchedule(int scheduleId) {
        boolean success = dao.deleteById(scheduleId);
        publish(success);
        return success;
    }

    /* =========================
       EXTRA
       ========================= */

    public CollectionInfo getCollectionInfo(String barangayName) {
        return dao.findCurrentCollectionInfo(barangayName);
    }

    /* =========================
       HELPERS
       ========================= */

    private boolean equalsIgnoreCase(String a, String b) {
        return a != null && b != null && a.equalsIgnoreCase(b);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Get all barangays scheduled for a specific day of the week (any date).
     * Useful for daily collection displays.
     */
    public List<String> getBarangaysByCollectionDay(String dayLabel) {
        DayOfWeek targetDay = parseDay(dayLabel);
        if (targetDay == null) {
            return Collections.emptyList();
        }

        return getAllSchedules().stream()
                .filter(s -> s.getDate() != null)
                .filter(s -> s.getDate().getDayOfWeek() == targetDay)
                .map(Schedule::getBarangayName)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Calculate the next scheduled date for a specific day of the week.
     * Used to display upcoming collections in the schedule table.
     */
    public LocalDate getNextScheduledDate(String dayLabel) {
        LocalDate today = LocalDate.now();
        DayOfWeek targetDay = parseDay(dayLabel);

        if (targetDay == null) {
            return null;
        }

        // Find next occurrence of the target day
        LocalDate candidate = today;
        while (candidate.getDayOfWeek() != targetDay) {
            candidate = candidate.plusDays(1);
        }

        // If it's today, move to next week
        if (candidate.equals(today)) {
            candidate = candidate.plusDays(7);
        }

        return candidate;
    }

    private void publish(boolean success) {
        if (success) {
            DataChangeBus.publish(
                    DataTopics.SCHEDULES,
                    DataTopics.BARANGAYS,
                    DataTopics.COLLECTION_INFO,
                    DataTopics.DASHBOARD
            );
        }
    }
}