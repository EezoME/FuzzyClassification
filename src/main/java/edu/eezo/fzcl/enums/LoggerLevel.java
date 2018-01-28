package edu.eezo.fzcl.enums;

/**
 * @author eezo33
 */
public enum LoggerLevel {
    FATAL("Фатальная ошибка"), ERROR("Ошибка"), WARN("Предупреждение"), INFO("Информация"), DEBUG("Отладочная информация");
    private String about;

    LoggerLevel(String about) {
        this.about = about;
    }

    public String getAbout() {
        return about;
    }
}
