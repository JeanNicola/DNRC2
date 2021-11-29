package gov.mt.wris.util;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.StatusResultMatchers;

public class TestResponse {

    private ResultActions actions;
    private ObjectMapper mapper;

    public TestResponse(ResultActions actions, ObjectMapper mapper) {
        this.actions = actions;
        this.mapper = mapper;
    }

    public TestResponse expect(ResultMatcher matcher) throws Exception {
        actions = actions.andExpect(matcher);
        return this;
    }

    public TestResponse then(ResultHandler handler) throws Exception {
        actions = actions.andDo(handler);
        return this;
    }

    public TestStatusMatcher expectStatus() {
        return (TestStatusMatcher) Proxy.newProxyInstance(
            TestStatusMatcher.class.getClassLoader(),
            new Class[] { TestStatusMatcher.class },
            (proxy, method, args) -> {
                Method matcher =
                    StatusResultMatchers.class.getMethod(method.getName());
                actions =
                    actions.andExpect(
                        (ResultMatcher) matcher.invoke(status(), args)
                    );
                return this;
            }
        );
    }

    public TestResponse expectBody(Class<?> type) throws Exception {
        try {
            String result = actions
                .andReturn()
                .getResponse()
                .getContentAsString();

            if (type.equals(Void.class) && result.isEmpty()) {
                return this;
            }

            mapper.readValue(result, type);
        } catch (Exception error) {
            fail("Invalid response body, expected " + type.getName(), error);
        }

        return this;
    }

    public <T> TestResponse with(Class<T> type, Consumer<T> test)
        throws Exception {
        test.accept(parse(type));
        return this;
    }

    public <T> T parse(Class<T> type) throws Exception {
        String result = actions.andReturn().getResponse().getContentAsString();
        return mapper.readValue(result, type);
    }

    public <T> List<T> collect(Class<T> type) throws Exception {
        String result = actions.andReturn().getResponse().getContentAsString();
        CollectionType collection = mapper
            .getTypeFactory()
            .constructCollectionType(ArrayList.class, type);
        return mapper.readValue(result, collection);
    }
}
