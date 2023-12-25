package de.sustineo.acc.servertools.exceptions;

import com.vaadin.flow.router.*;
import de.sustineo.acc.servertools.views.ErrorView;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class NotFoundExceptionHandler extends RouteNotFoundError {
    @Override
    public int setErrorParameter(BeforeEnterEvent beforeEnterEvent, ErrorParameter<NotFoundException> errorParameter) {
        HashMap<String, List<String>> parameters = new HashMap<>();
        parameters.put(ErrorView.QUERY_PARAMETER_HTTP_STATUS, List.of(HttpStatus.NOT_FOUND.toString()));

        if (errorParameter.getException().getMessage() != null) {
            parameters.put(ErrorView.QUERY_PARAMETER_ERROR_MESSAGE, List.of(errorParameter.getException().getMessage()));
        }

        beforeEnterEvent.rerouteTo(ErrorView.class, new QueryParameters(parameters));
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
