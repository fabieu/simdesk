package de.sustineo.acc.servertools.exceptions;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.internal.DefaultErrorHandler;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.servertools.views.ErrorView;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@AnonymousAllowed
@DefaultErrorHandler
@Component
public class IllegalArgumentExceptionHandler implements HasErrorParameter<IllegalArgumentException> {
    @Override
    public int setErrorParameter(BeforeEnterEvent beforeEnterEvent, ErrorParameter<IllegalArgumentException> errorParameter) {
        HashMap<String, List<String>> parameters = new HashMap<>();
        parameters.put(ErrorView.QUERY_PARAMETER_HTTP_STATUS, List.of(HttpStatus.BAD_REQUEST.toString()));

        if (errorParameter.getException().getMessage() != null) {
            parameters.put(ErrorView.QUERY_PARAMETER_ERROR_MESSAGE, List.of(errorParameter.getException().getMessage()));
        }

        beforeEnterEvent.rerouteTo(ErrorView.class, new QueryParameters(parameters));
        return HttpServletResponse.SC_BAD_REQUEST;
    }
}
