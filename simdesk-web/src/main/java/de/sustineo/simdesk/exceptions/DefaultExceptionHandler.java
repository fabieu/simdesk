package de.sustineo.simdesk.exceptions;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.InternalServerError;
import com.vaadin.flow.router.QueryParameters;
import de.sustineo.simdesk.views.ErrorView;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Log
@Component
@Profile("!debug")
public class DefaultExceptionHandler extends InternalServerError {
    @Override
    public int setErrorParameter(BeforeEnterEvent beforeEnterEvent, ErrorParameter<Exception> errorParameter) {
        HashMap<String, List<String>> parameters = new HashMap<>();
        parameters.put(ErrorView.QUERY_PARAMETER_HTTP_STATUS, List.of(HttpStatus.INTERNAL_SERVER_ERROR.toString()));
        parameters.put(ErrorView.QUERY_PARAMETER_ERROR_MESSAGE, List.of("An unexpected error occurred. Please contact support."));

        beforeEnterEvent.rerouteTo(ErrorView.class, new QueryParameters(parameters));
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }
}
