package com.chanseok.common.http.wrapper;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * Filter, Interceptor 등에서 HttpServletResponse 의 바디(OutputStream) 부분을 가져올 경우 한 번 가져올 때 사라지기 때문에 여러 번 사용하기 위해 Caching 하는 Wrappe
 */
public class CachingResponseWrapper extends ContentCachingResponseWrapper {

    /**
     * 생성자를 통해서 HttpServletResponse 를 전달받아 초기화
     * @param response
     */
    public CachingResponseWrapper(HttpServletResponse response) {
        super(response);
    }
}
