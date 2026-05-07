package com.attendance.entity;

import java.time.LocalTime;

public enum Session {
    MORNING(LocalTime.of(9, 0), LocalTime.of(10, 0)),
    AFTERNOON(LocalTime.of(13, 30), LocalTime.of(14, 30));

    private final LocalTime startTime;
    private final LocalTime endTime;

    Session(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime()   { return endTime; }

    /**
     * Returns true if the current system time falls within this session's window.
     */
    public boolean isOpen() {
        LocalTime now = LocalTime.now();
        return !now.isBefore(startTime) && !now.isAfter(endTime);
    }

    /**
     * Returns the currently open session, or empty if none.
     */
    public static java.util.Optional<Session> currentOpenSession() {
        for (Session s : values()) {
            if (s.isOpen()) return java.util.Optional.of(s);
        }
        return java.util.Optional.empty();
    }

    public String getDisplayName() {
        return name() + " (" + startTime + " – " + endTime + ")";
    }
}
