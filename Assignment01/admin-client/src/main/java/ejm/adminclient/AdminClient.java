package ejm.adminclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

/**
 * @author Ken Finnigan
 */
public class AdminClient {
    private String url;

    public AdminClient(String url) {
        this.url = url;
    }

    public Category getCategory(final Integer categoryId) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/admin/category/" + categoryId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String jsonResponse =
                Request
                        .Get(uriBuilder.toString())
                        .execute()
                        .returnContent().asString();

        if (jsonResponse.isEmpty()) {
            return null;
        }

        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .readValue(jsonResponse, Category.class);
    }

    public Collection<Category> getAllCategories() throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/admin/category");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String jsonResponse =
                Request
                        .Get(uriBuilder.toString())
                        .execute()
                        .returnContent().asString();

        if (jsonResponse.isEmpty()) {
            return null;
        }

        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .readValue(jsonResponse, new TypeReference<Collection<Category>>() {
                });
    }

    public Category createCategory(final Category category) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/admin/category");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        String jsonResponse =
                Request
                        .Post(uriBuilder.toString())
                        .bodyString(objectMapper.writeValueAsString(category), ContentType.APPLICATION_JSON)
                        .execute()
                        .returnContent().asString();

        if (jsonResponse.isEmpty()) {
            return null;
        }

        return objectMapper.readValue(jsonResponse, Category.class);
    }

    public Category updateCategory(final Category category) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/admin/category/" + category.getId());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        String jsonResponse =
                Request
                        .Put(uriBuilder.toString())
                        .bodyString(objectMapper.writeValueAsString(category), ContentType.APPLICATION_JSON)
                        .execute()
                        .returnContent().asString();

        if (jsonResponse.isEmpty()) {
            return null;
        }

        return objectMapper.readValue(jsonResponse, Category.class);
    }

    public Response deleteCategory(final Integer categoryId) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/admin/category/" + categoryId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return Request
                .Delete(uriBuilder.toString())
                .execute();
    }
}
