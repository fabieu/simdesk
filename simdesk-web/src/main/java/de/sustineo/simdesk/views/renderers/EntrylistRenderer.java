package de.sustineo.simdesk.views.renderers;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.Style;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccDriverCategory;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccNationality;

public class EntrylistRenderer {
    public static Renderer<AccDriverCategory> createAccDriverCategoryRenderer() {
        return new ComponentRenderer<>(driverCategory -> {
            Span driverCategoryIcon = new Span();
            driverCategoryIcon.getStyle()
                    .setPosition(Style.Position.RELATIVE)
                    .setWidth("1.333333em")
                    .setLineHeight("1em")
                    .setBorderRadius("50%");

            switch (driverCategory) {
                case BRONZE:
                    driverCategoryIcon.getStyle().setBackgroundColor("var(--driver-category-bronze-color)");
                    break;
                case SILVER:
                    driverCategoryIcon.getStyle().setBackgroundColor("var(--driver-category-silver-color)");
                    break;
                case GOLD:
                    driverCategoryIcon.getStyle().setBackgroundColor("var(--driver-category-gold-color)");
                    break;
                case PLATINUM:
                    driverCategoryIcon.getStyle().setBackgroundColor("var(--driver-category-platinum-color)");
                    break;
            }

            return new HorizontalLayout(driverCategoryIcon, new Text(driverCategory.getName()));
        });
    }

    public static Renderer<AccNationality> createAccNationalityRenderer() {
        return new ComponentRenderer<>(nationality -> {
            // Add flag icons from npm package flag-icons
            Span flagIcon = new Span();
            flagIcon.addClassNames("fi", "fi-" + nationality.getAlpha2Code().toLowerCase());

            return new HorizontalLayout(flagIcon, new Text(nationality.getShortName()));
        });
    }
}
