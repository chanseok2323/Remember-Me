package com.chanseok.common.http.wrapper;

import ch.qos.logback.core.util.StringUtil;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter, Interceptor 등에서 HttpServletRequest 의 바디(InputStream) 부분을 가져올 경우 한 번 가져올 때 사라지기 때문에 여러 번 사용하기 위해 Caching 하는 Wrapper
 */
public class CachingRequestWrapper extends HttpServletRequestWrapper {
    private final Logger logger = LoggerFactory.getLogger(CachingRequestWrapper.class);
    private final Map<String, Object> params;
    private final Charset encoding;
    private final byte[] rawData;

    /**
     * 생성자를 통해서 HttpServletRequest 를 전달받아 초기화
     * @param request
     * @throws IOException
     */
    public CachingRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        this.params = new HashMap<>(request.getParameterMap());

        String characterEncoding = request.getCharacterEncoding();
        this.encoding = StringUtils.hasText(characterEncoding) ? Charset.forName(characterEncoding) : StandardCharsets.UTF_8;

        InputStream inputStream = request.getInputStream();
        this.rawData = StreamUtils.copyToByteArray(inputStream);
    }

    /**
     * 캐싱한 InputStream 을 BufferedReader 로 변환하여 반환
     * @return
     * @throws IOException
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream(), this.encoding));
    }

    /**
     * 캐싱한 InputStream 을 ServletInputStream 으로 변환하여 반환
     * @return
     * @throws IOException
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStream() {
            private InputStream buffer = new ByteArrayInputStream(rawData);

            @Override
            public boolean isFinished() {
                try {
                    return buffer.available() == 0;
                } catch (IOException e) {
                    logger.error("IOException = {}", e);
                }
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException("not support");
            }

            @Override
            public int read() throws IOException {
                return buffer.read();
            }
        };
    }

    /**
     * 파라미터 이름에 해당하는 값을 반환
     * @param name
     * @return
     */
    public String getParameter(String name) {
        String result = null;
        String[] parameterValues = getParameterValues(name);
        if(parameterValues != null && parameterValues.length > 0) {
            result = parameterValues[0];
        }

        return result;
    }

    /**
     * 파라미터 이름에 해당하는 값을 배열로 반환
     * @param name
     * @return
     */
    public String[] getParameterValues(String name) {
        String[] result = null;
        String[] temp = (String[]) params.get(name);
        if(temp != null) {
            result = new String[temp.length];
            System.arraycopy(temp, 0, result, 0, temp.length);
        }
        return result;
    }

    /**
     * 파라미터 이름에 해당하는 값을 설정
     * @param name
     * @param value
     */
    public void setParameter(String name, String value) {
        String[] oneParam = {value};
        setParameter(name, oneParam);
    }

    /**
     * 파라미터 값 셋팅
     * @param name
     * @param value
     */
    public void setParameter(String name, String[] value) {
        params.put(name, value);
    }

    /**
     * 파라미터 제거
     * @param name
     */
    public void removeParameter(String name) {
        params.remove(name);
    }

    /**
     * 파라미터 맵 반환
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map getParameterMap() {
        return Collections.unmodifiableMap(params);
    }

    /**
     * 파라미터 이름 반환
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Enumeration getParameterNames() {
        return Collections.enumeration(params.keySet());
    }
}