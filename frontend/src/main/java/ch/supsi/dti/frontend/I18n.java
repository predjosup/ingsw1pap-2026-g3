package ch.supsi.dti.frontend;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class I18n {

    private final Locale locale;
    private final ResourceBundle bundle;

    public I18n(Locale locale) {
        this.locale = locale;
        this.bundle = ResourceBundle.getBundle("i18n.messages", locale);
    }

    public Locale locale() {
        return locale;
    }

    public ResourceBundle bundle() {
        return bundle;
    }

    public boolean isItalian() {
        return Locale.ITALIAN.getLanguage().equals(locale.getLanguage());
    }

    public boolean isEnglish() {
        return Locale.ENGLISH.getLanguage().equals(locale.getLanguage());
    }

    public String text(String key, Object... args) {
        String pattern = bundle.getString(key);
        if (args == null || args.length == 0) {
            return pattern;
        }
        return MessageFormat.format(pattern, args);
    }
}
