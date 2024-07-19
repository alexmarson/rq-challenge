package com.example.rqchallenge.config;

import com.example.rqchallenge.exception.RqChallengeApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class RqChallengeErrorHandler implements ResponseErrorHandler {

    Logger logger = LoggerFactory.getLogger(RqChallengeErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is4xxClientError()) {
            logger.error("Client error received, code :: " + response.getStatusCode());
            throw new RqChallengeApplicationException(response.getStatusText(), response.getStatusCode());
        }

        if (response.getStatusCode().is5xxServerError()) {
            logger.error("Server error received, code :: " + response.getStatusCode());
            throw new RqChallengeApplicationException(response.getStatusText(), response.getStatusCode());
        }
    }
}
