package de.sustineo.simdesk.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.springframework.stereotype.Service;

@Service
public class ButtonComponentFactory extends ComponentFactory {
    public Button createPrimaryButton(String label) {
        Button button = new Button(label);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return button;
    }

    public Button createPrimarySuccessButton(String label) {
        Button button = new Button(label);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        return button;
    }

    public Button createWarningButton(String label) {
        Button button = new Button(label);
        button.addThemeVariants(ButtonVariant.LUMO_WARNING);
        return button;
    }

    public Button createErrorButton(String label) {
        Button button = new Button(label);
        button.addThemeVariants(ButtonVariant.LUMO_ERROR);
        return button;
    }

    public Button createCancelIconButton() {
        Button button = new Button(VaadinIcon.CLOSE.create());
        button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        return button;
    }

    public Button createDialogCancelButton(Dialog dialog) {
        Button cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(event -> dialog.close());
        cancelButton.getStyle()
                .set("margin-right", "auto");
        return cancelButton;
    }
}
