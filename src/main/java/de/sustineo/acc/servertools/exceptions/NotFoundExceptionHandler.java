package de.sustineo.acc.servertools.exceptions;

import com.vaadin.flow.router.*;
import de.sustineo.acc.servertools.views.ErrorView;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Log
@Component
public class NotFoundExceptionHandler extends RouteNotFoundError {
    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        log.fine("Non existing view requested with name: /" + event.getLocation().getPath());

        HashMap<String, List<String>> parameters = new HashMap<>();
        parameters.put(ErrorView.QUERY_PARAMETER_HTTP_STATUS, List.of(HttpStatus.NOT_FOUND.toString()));
        parameters.put(ErrorView.QUERY_PARAMETER_ERROR_MESSAGE, List.of(parameter.getException().getMessage()));

        event.rerouteTo(ErrorView.class, new QueryParameters(parameters));

        return HttpServletResponse.SC_NOT_FOUND;
    }
}
