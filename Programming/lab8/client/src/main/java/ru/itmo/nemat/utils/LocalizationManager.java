package ru.itmo.nemat.utils;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import java.util.LinkedHashMap;
import java.util.Map;

public class LocalizationManager {

    private static final String BUNDLE_NAME = "messages";

    private static final ObjectProperty<ResourceBundle> bundleProperty = new SimpleObjectProperty<>();

    static {
        setLocale(new Locale("ru", "RU"));
    }

    private static final Map<String, Locale> SUPPORTED_LOCALES = new LinkedHashMap<>();

    static {
        SUPPORTED_LOCALES.put("Русский", new Locale("ru", "RU"));
        SUPPORTED_LOCALES.put("English (CA)", new Locale("en", "CA"));
        SUPPORTED_LOCALES.put("Srpski", new Locale("sr", "RS"));
        SUPPORTED_LOCALES.put("Latviešu", new Locale("lv", "LV"));
    }

    public static void setLocale(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        bundleProperty.set(bundle);
        Locale.setDefault(locale);
    }


    public static String getString(String key) {
        try {
            return bundleProperty.get().getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }


    public static String format(String rawString) {
        if (rawString == null || rawString.isEmpty()) return "";

        if (!rawString.contains(":")) {
                return getString(rawString);
        }

        String[] parts = rawString.split(":", 2);
        String key = parts[0];
        String argsString = parts[1];

        String template = getString(key);

        if (template.equals(key)) {
            return rawString;
        }

        String[] args = argsString.split(",");

        for (int i = 0; i < args.length; i++) {
            args[i] = format(args[i].trim());
        }

        return MessageFormat.format(template, (Object[]) args);
    }

    public static StringBinding getBinding(String key) {
        return Bindings.createStringBinding(
                () -> getString(key),
                bundleProperty
        );
    }

    public static ObjectProperty<ResourceBundle> bundleProperty() {
        return bundleProperty;
    }

    public static String formatNumber(double number) {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(number);
    }

    public static String formatDate(Date date) {
        if (date == null) return "";

        ZoneId zone;
        String lang = Locale.getDefault().getLanguage();

        if (lang.equals("en")) {
            zone = ZoneId.of("America/Toronto");
        } else if (lang.equals("sr")) {
            zone = ZoneId.of("Europe/Belgrade");
        } else if (lang.equals("lv")) {
            zone = ZoneId.of("Europe/Riga");
        } else {
            zone = ZoneId.of("Europe/Moscow");
        }

        LocalDateTime ldt = date.toInstant().atZone(zone).toLocalDateTime();

        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(Locale.getDefault());

        return ldt.format(dtf);
    }

    public static Map<String, Locale> getSupportedLocales() {
        return SUPPORTED_LOCALES;
    }

}