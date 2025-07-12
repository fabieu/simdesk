package de.sustineo.simdesk.logging;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import jakarta.annotation.Nullable;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * A Logback Appender that writes formatted log messages into
 * a JavaFX TextArea.
 */
public class TextAreaAppender extends AppenderBase<ILoggingEvent> {
    @Setter
    private PatternLayout layout;

    private static final Set<TextArea> textAreas = new HashSet<>();

    /**
     * Adds a TextArea to the list of targets for log messages.
     *
     * @param textArea The TextArea to append log messages to.
     */
    public static void addTextArea(@Nullable TextArea textArea) {
        if (textArea != null) {
            textAreas.add(textArea);
        }
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (textAreas.isEmpty()) {
            return;
        }

        // Format the message (uses the Layout you configure in logback.xml)
        String message = layout.doLayout(event);

        // Must append from the JavaFX Application Thread
        for (TextArea textArea : textAreas) {
            Platform.runLater(() -> textArea.appendText(message));
        }
    }
}
