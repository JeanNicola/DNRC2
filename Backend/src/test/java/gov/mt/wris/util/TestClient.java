package gov.mt.wris.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@Component(value = "client")
public class TestClient {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired(required = false)
    String token;

    public TestRequestBuilder get(String uri, Object... params) {
        return new TestRequestBuilder(
            MockMvcRequestBuilders.get(uri, params),
            mockMvc,
            mapper,
            token
        );
    }

    public TestRequestBuilder post(String uri, Object... params) {
        return new TestRequestBuilder(
            MockMvcRequestBuilders.post(uri, params),
            mockMvc,
            mapper,
            token
        );
    }

    public TestRequestBuilder put(String uri, Object... params) {
        return new TestRequestBuilder(
            MockMvcRequestBuilders.put(uri, params),
            mockMvc,
            mapper,
            token
        );
    }

    public TestRequestBuilder delete(String uri, Object... params) {
        return new TestRequestBuilder(
            MockMvcRequestBuilders.delete(uri, params),
            mockMvc,
            mapper,
            token
        );
    }
}
