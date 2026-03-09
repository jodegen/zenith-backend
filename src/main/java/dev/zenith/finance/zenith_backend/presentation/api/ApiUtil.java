package dev.zenith.finance.zenith_backend.presentation.api;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.IOException;

/**
 * Utility class used by the generated OpenAPI default interface methods
 * to set example responses on the HTTP response.
 *
 * This file is intentionally NOT generated (generateSupportingFiles=false)
 * so it will never be overwritten during a build.
 */
public final class ApiUtil {

    private ApiUtil() {
        // utility class
    }

    public static void setExampleResponse(NativeWebRequest req, String contentType, String example) {
        try {
            HttpServletResponse res = req.getNativeResponse(HttpServletResponse.class);
            if (res != null) {
                res.setCharacterEncoding("UTF-8");
                res.addHeader("Content-Type", contentType);
                res.getWriter().print(example);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to set example response", e);
        }
    }
}

