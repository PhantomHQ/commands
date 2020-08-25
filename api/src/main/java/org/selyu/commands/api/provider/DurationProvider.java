package org.selyu.commands.api.provider;

import org.selyu.commands.api.argument.CommandArg;
import org.selyu.commands.api.lang.Lang;
import org.selyu.commands.api.parametric.ICommandProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class DurationProvider implements ICommandProvider<Date> {
    private final Lang lang;

    public DurationProvider(@Nonnull Lang lang) {
        this.lang = lang;
    }

    public static long smartParseDuration(String s) {
        if (s.contains(":")) {
            String[] parts = s.split(":");
            String hours = parts[0];
            String minutes = parts[1];
            if (charCount(':', s) >= 2) {
                // hh:mm:ss
                String seconds = parts[2];
                try {
                    int h = Integer.parseInt(hours);
                    int m = Integer.parseInt(minutes);
                    int sec = Integer.parseInt(seconds);
                    return (h * 60 * 60 * 1000) + (m * 60 * 1000) + (sec * 1000);
                } catch (NumberFormatException ex) {
                    return 0;
                }
            } else {
                // hh:mm
                try {
                    int h = Integer.parseInt(hours);
                    int m = Integer.parseInt(minutes);
                    return (h * 60 * 60 * 1000) + (m * 60 * 1000);
                } catch (NumberFormatException ex) {
                    return 0;
                }
            }
        } else {
            return parseDurationSimple(s);
        }
    }

    public static long parseDurationSimple(String str) {
        int h = parseTime(str, 'h');
        int m = parseTime(str, 'm');
        int s = parseTime(str, 's');

        if (h == -1 || m == -1 || s == -1) {
            return -1;
        }

        long hoursToMS = (h * 60 * 60 * 1000);
        long minutesToMS = (m * 60 * 1000);
        long secondsToMS = (s * 1000);

        return hoursToMS + minutesToMS + secondsToMS;
    }

    public static int charCount(char c, String s) {
        int y = 0;
        for (char x : s.toCharArray()) {
            if (x == c) {
                y++;
            }
        }
        return y;
    }

    public static int parseTime(String s, char c) {
        s = s.toLowerCase();
        if (s.indexOf(c) > -1) {
            if (charCount(c, s) == 1) {
                int index = s.indexOf(c);
                return getCountAt(s, index);
            } else {
                return -1; // error: can't have more than one occurrence of a time modifier
            }
        }
        return 0;
    }

    public static int getCountAt(String s, int index) {
        if (index > 0) {
            int start = index - 1;
            char[] chars = s.toCharArray();
            while (true) {
                if (chars.length > start && start > 0) {
                    char c = chars[start];
                    if (!isTimeModifier(c)) {
                        start--;
                        continue;
                    } else {
                        start++;
                    }
                }
                break;
            }
            String countStr = s.substring(start, index);
            try {
                return Integer.parseInt(countStr);
            } catch (NumberFormatException ex) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public static boolean isTimeModifier(char c) {
        return c == 'h' || c == 'm' || c == 's';
    }

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public Date provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws IllegalArgumentException {
        String s = arg.get();
        try {
            long l = smartParseDuration(s);
            if (l != -1) {
                return new Date(l);
            } else {
                throw new IllegalArgumentException(lang.get("invalid_duration"));
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(lang.get("invalid_duration"));
        }
    }

    @Nonnull
    @Override
    public String argumentDescription() {
        return "duration";
    }

    @Nonnull
    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }

}
