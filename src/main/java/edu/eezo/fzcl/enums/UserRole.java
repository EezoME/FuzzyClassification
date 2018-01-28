package edu.eezo.fzcl.enums;

/**
 * @author eezo33
 */
public enum UserRole {
    DEVELOPER("Разработчик"), ADMIN("Администратор"), USER("Пользователь"), GUEST("Гость");
    private String about;

    UserRole(String about) {
        this.about = about;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
