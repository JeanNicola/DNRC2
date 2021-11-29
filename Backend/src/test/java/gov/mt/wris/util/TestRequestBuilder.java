package gov.mt.wris.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class TestRequestBuilder {

    private MockHttpServletRequestBuilder builder;
    private MockMvc client;
    private ObjectMapper mapper;

    public TestRequestBuilder(
        MockHttpServletRequestBuilder builder,
        MockMvc client,
        ObjectMapper mapper,
        String token
    ) {
        this.client = client;
        this.mapper = mapper;
        this.builder = builder
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON);
    }

    public TestRequestBuilder body(Object dto) throws Exception {
        String content = mapper.writeValueAsString(dto);
        builder = builder.content(content);
        return this;
    }

    public TestRequestBuilder param(String key, Object value) {
        builder = builder.param(key, value.toString());
        return this;
    }

    public TestRequestBuilder queryParam(String key, Object value) {
        builder = builder.queryParam(key, value.toString());
        return this;
    }

    public TestRequestBuilder with(RequestPostProcessor processor) {
        builder = builder.with(processor);
        return this;
    }

    public TestResponse exchange() throws Exception {
        return new TestResponse(client.perform(builder), mapper);
    }
}
