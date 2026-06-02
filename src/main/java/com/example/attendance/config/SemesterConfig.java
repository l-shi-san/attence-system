package com.example.attendance.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

@Component
public class SemesterConfig {

    private static final Logger log = LoggerFactory.getLogger(SemesterConfig.class);
    private static final String PROPERTIES_FILE = "./config/semester.properties";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Value("${semester.start-date:2026-02-23}")
    private String defaultStartDate;

    private LocalDate semesterStart;

    @PostConstruct
    public void init() {
        Path configPath = Paths.get(PROPERTIES_FILE);
        if (Files.exists(configPath)) {
            Properties props = new Properties();
            try (InputStream is = Files.newInputStream(configPath)) {
                props.load(is);
                String val = props.getProperty("semester.start-date");
                if (val != null && !val.isBlank()) {
                    semesterStart = LocalDate.parse(val.trim(), FMT);
                    return;
                }
            } catch (Exception e) { log.warn("读取semester.properties失败"); }
        }
        semesterStart = LocalDate.parse(defaultStartDate, FMT);
    }

    public LocalDate getSemesterStart() { return semesterStart; }

    public void setSemesterStart(LocalDate date) {
        this.semesterStart = date;
        try {
            Path configPath = Paths.get(PROPERTIES_FILE);
            Files.createDirectories(configPath.getParent());
            Properties props = new Properties();
            if (Files.exists(configPath)) {
                try (InputStream is = Files.newInputStream(configPath)) { props.load(is); }
            }
            props.setProperty("semester.start-date", date.format(FMT));
            try (OutputStream os = Files.newOutputStream(configPath)) { props.store(os, "Semester config"); }
        } catch (IOException e) { log.error("持久化失败", e); }
    }

    public int getTeachingWeek(LocalDate date) {
        if (date.isBefore(semesterStart)) return 0;
        long days = ChronoUnit.DAYS.between(semesterStart, date);
        return (int) (days / 7) + 1;
    }

    public LocalDate getTeachingWeekStart(int week) { return semesterStart.plusWeeks(week - 1); }
    public LocalDate getTeachingWeekEnd(int week) { return getTeachingWeekStart(week).plusDays(6); }
    public int getCurrentTeachingWeek() { return getTeachingWeek(LocalDate.now()); }
    public String getTeachingWeekLabel(int week) {
        return "第" + week + "周（" + getTeachingWeekStart(week) + " ~ " + getTeachingWeekEnd(week) + "）";
    }
    public String getTeachingWeekKey(int week) { return "W" + String.format("%02d", week); }
}
