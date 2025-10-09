package de.sustineo.simdesk.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.RouteParameters;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.search.SearchResult;
import de.sustineo.simdesk.services.SearchService;
import de.sustineo.simdesk.views.LeaderboardDriverView;
import de.sustineo.simdesk.views.LeaderboardSessionDetailsView;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile(ProfileManager.PROFILE_LEADERBOARD + " & " + ProfileManager.PROFILE_INSIGHTS)
@Service
@RequiredArgsConstructor
public class SearchComponentFactory {
    private final SearchService searchService;

    public Component createSearchField() {
        ComboBox<SearchResult> searchComboBox = new ComboBox<>();
        searchComboBox.setPlaceholder("Search drivers, sessions, and more...");
        searchComboBox.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchComboBox.setClearButtonVisible(true);
        searchComboBox.setWidth("350px");
        searchComboBox.setOverlayWidth("500px");

        // Set up the search functionality with proper lazy loading
        searchComboBox.setItems(query ->
                searchService.search(
                        query.getFilter().orElse(""),
                        query.getOffset(),
                        query.getLimit()
                ).stream()
        );

        // Custom renderer to show type and label
        searchComboBox.setRenderer(new ComponentRenderer<>(searchResult -> {
            HorizontalLayout container = new HorizontalLayout();
            container.setAlignItems(FlexComponent.Alignment.CENTER);
            container.getStyle()
                    .setGap("var(--lumo-space-s)");

            Span label = new Span(searchResult.getLabel());
            label.getStyle()
                    .setFontWeight("bold");

            Span type = new Span(searchResult.getType().getLabel());
            type.getElement().getThemeList().add("badge contrast");

            container.add(label, type);
            return container;
        }));

        // Handle selection
        searchComboBox.addValueChangeListener(event -> {
            SearchResult selectedResult = event.getValue();
            if (selectedResult != null) {
                navigateToSearchResult(searchComboBox, selectedResult);
                searchComboBox.clear();
                searchComboBox.blur();
            }
        });

        return searchComboBox;
    }

    private void navigateToSearchResult(Component source, SearchResult searchResult) {
        source.getUI().ifPresent(ui -> {
            switch (searchResult.getType()) {
                case DRIVER:
                    ui.navigate(LeaderboardDriverView.class, new RouteParameters("driverId", searchResult.getId()));
                    break;
                case SESSION:
                    ui.navigate(LeaderboardSessionDetailsView.class, new RouteParameters("fileChecksum", searchResult.getId()));
                    break;
            }
        });
    }
}

