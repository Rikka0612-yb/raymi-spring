package com.rikka.raymispring.tool;

import org.springframework.ai.tool.annotation.Tool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 晏波
 * 2026/3/25 20:48
 */
public class DateTimeTools {
    @Tool(description = "Use this tool to get the current time, if the conversation involves time",name = "Raymi的金丝怀表")
    public String getCurrentDatetime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
