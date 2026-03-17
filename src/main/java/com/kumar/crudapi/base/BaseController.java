package com.kumar.crudapi.base;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Optional;

public class BaseController {

    private static final String HEADER_X_TOTAL_COUNT = "X-Total-Count";
    private static final String HEADER_LINK_FORMAT = "<{0}>; rel=\"{1}\"";


    /**
     * <p>createHeaderAlert. Its adds three headers viz. "X-app-alert", "X-app-alert-message" & "X-app-params"</p>
     *
     * @param message        a {@link String} object.
     * @param param          a {@link String} object.
     * @param defaultMessage a {@link String} object.
     * @return a {@link HttpHeaders} object.
     */
    protected HttpHeaders createHeaderAlert(String message, String param, String defaultMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-app-alert", message);
        headers.add("X-app-alert-message", defaultMessage);
        try {
            headers.add("X-app-params", URLEncoder.encode(param, StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException e) {
            // StandardCharsets are supported by every Java implementation so this exception will never happen
        }
        return headers;
    }

    /**
     * <p>createFailureHeaderAlert. adds 3 header viz. "X-app-error", "X-app-params", "X-app-error-message"</p>
     *
     * @param errorKey       a {@link String} object.
     * @param defaultMessage a {@link String} object.
     * @return a {@link HttpHeaders} object.
     */
    protected HttpHeaders createFailureHeaderAlert(String errorKey, String param, String defaultMessage) {

        String message = "error." + errorKey;

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-app-error", message);
        headers.add("X-app-params", param);
        headers.add("X-app-error-message", defaultMessage);

        return headers;
    }

    /**
     * Generate pagination headers for a Spring Data {@link Page} object.
     *
     * @param request The ServerHttpRequest .
     * @param page    The page.
     * @param <T>     The type of object.
     * @return http header.
     */
    protected <T> HttpHeaders generatePaginationHttpHeaders(HttpServletRequest request, Page<T> page) {

        //UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpRequest(request);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(request.getContextPath());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_X_TOTAL_COUNT, Long.toString(page.getTotalElements()));
        int pageNumber = page.getNumber();
        int pageSize = page.getSize();
        StringBuilder link = new StringBuilder();
        if (pageNumber < page.getTotalPages() - 1) {
            link.append(prepareLink(uriBuilder, pageNumber + 1, pageSize, "next"))
                    .append(",");
        }
        if (pageNumber > 0) {
            link.append(prepareLink(uriBuilder, pageNumber - 1, pageSize, "prev"))
                    .append(",");
        }
        link.append(prepareLink(uriBuilder, page.getTotalPages() - 1, pageSize, "last"))
                .append(",")
                .append(prepareLink(uriBuilder, 0, pageSize, "first"));
        headers.add(HttpHeaders.LINK, link.toString());
        return headers;
    }

    /**
     * Wrap the optional into a {@link ResponseEntity} with an {@link HttpStatus#OK} status, or if it's empty, it
     * returns a {@link ResponseEntity} with {@link HttpStatus#NOT_FOUND}.
     *
     * @param <X>           type of the response
     * @param maybeResponse response to return if present
     * @return response containing {@code maybeResponse} if present or {@link HttpStatus#NOT_FOUND}
     */
    protected <X> ResponseEntity<X> wrapOrNotFound(Optional<X> maybeResponse) {
        return wrapOrNotFound(maybeResponse, null);
    }

    /**
     * Wrap the optional into a {@link ResponseEntity} with an {@link HttpStatus#OK} status with the headers, or if it's
     * empty, throws a {@link ResponseStatusException} with status {@link HttpStatus#NOT_FOUND}.
     *
     * @param <X>           type of the response
     * @param maybeResponse response to return if present
     * @param header        headers to be added to the response
     * @return response containing {@code maybeResponse} if present
     */
    protected <X> ResponseEntity<X> wrapOrNotFound(Optional<X> maybeResponse, HttpHeaders header) {
        return maybeResponse.map(response -> ResponseEntity.ok().headers(header).body(response))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private String prepareLink(UriComponentsBuilder uriBuilder, int pageNumber, int pageSize, String relType) {
        String link = uriBuilder.replaceQueryParam("page", Integer.toString(pageNumber))
                .replaceQueryParam("size", Integer.toString(pageSize))
                .toUriString()
                .replace(",", "%2C")
                .replace(";", "%3B");


        return MessageFormat.format(HEADER_LINK_FORMAT, link, relType);
    }

    /**
     * Generate URI by extending the current request path.
     *
     * @param request       The ServerHttpRequest .
     * @param pathExtension the extension to the current path.
     * @return URI.
     */
    protected URI extendRequestPath(HttpRequest request, String pathExtension) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(request.getURI().getPath());
        uriBuilder.path("/" + pathExtension);
        return uriBuilder.build().toUri();
    }

    /**
     * Generate URI by extending the current request path.
     *
     * @param request       The ServerHttpRequest .
     * @param pathExtension the extension to the current path.
     * @return URI.
     */
    protected URI extendRequestPath(HttpServletRequest request, String pathExtension) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(request.getContextPath());
        uriBuilder.path("/" + pathExtension);
        return uriBuilder.build().toUri();
    }
}