package ejm.adminclient;

import au.com.dius.pact.consumer.ConsumerPactTestMk2;
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslResponse;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactSpecVersion;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Lists;
import org.apache.http.client.fluent.Response;
import org.junit.Before;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Ken Finnigan
 */
public class ConsumerPactTest extends ConsumerPactTestMk2 {
    Category top, transport, autos, cars, toyotas;
    List<Category> categories;

    @Before
    public void init() {
        top = createCategory(0, "Top");

        transport = createCategory(1000, "Transportation");
        transport.setParent(top);

        autos = createCategory(1002, "Automobiles");
        autos.setParent(transport);

        cars = createCategory(1009, "Cars");
        cars.setParent(autos);

        toyotas = createCategory(1015, "Toyota Cars");
        toyotas.setParent(cars);

        categories = Lists.newArrayList(top, transport, autos, cars, toyotas);

    }

    private Category createCategory(Integer id, String name) {
        Category cat = new TestCategoryObject(id, LocalDateTime.parse("2002-01-01T00:00:00"), 1);
        cat.setName(name);
        cat.setVisible(Boolean.TRUE);
        cat.setHeader("header");
        cat.setImagePath("n/a");

        return cat;
    }

    @Override
    protected RequestResponsePact createPact(PactDslWithProvider builder) {


        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        try {
            // get specific
            PactDslResponse pactDslResponse = builder
                    .uponReceiving("Retrieve a category")
                    .path("/admin/category/" + toyotas.getId())
                    .method("GET")
                    .willRespondWith()
                    .status(200)
                    .body(mapper.writeValueAsString(toyotas));

            // get all
            pactDslResponse = pactDslResponse
                    .uponReceiving("Retrieve all categories")
                    .path("/admin/category")
                    .method("GET")
                    .willRespondWith()
                    .status(200)
                    .body(mapper.writeValueAsString(categories)); // write all categories

            // create
            pactDslResponse = pactDslResponse
                    .uponReceiving("Creating a category")
                    .path("/admin/category")
                    .method("POST")
                    .body(mapper.writeValueAsString(transport)) // receive "transport" category in body
                    .willRespondWith()
                    .status(200)
                    .body(mapper.writeValueAsString(transport)); // return the same "transport" category

            // update
            pactDslResponse = pactDslResponse
                    .uponReceiving("Updating a category")
                    .path("/admin/category/" + autos.getId()) // update "autos" category
                    .method("PUT")
                    .body(mapper.writeValueAsString(autos)) // receive "autos" category in body
                    .willRespondWith()
                    .status(200)
                    .body(mapper.writeValueAsString(autos)); // return the same "transport" category

            // remove
            pactDslResponse = pactDslResponse
                    .uponReceiving("Delete a category")
                    .path("/admin/category/" + cars.getId()) // delete cars category
                    .method("DELETE")
                    .willRespondWith()
                    .status(204);

            return pactDslResponse.toPact();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected String providerName() {
        return "admin_service_provider";
    }

    @Override
    protected String consumerName() {
        return "admin_client_consumer";
    }

    @Override
    protected PactSpecVersion getSpecificationVersion() {
        return PactSpecVersion.V3;
    }

    @Override
    protected void runTest(MockServer mockServer) throws IOException {
        AdminClient adminClient = new AdminClient(mockServer.getUrl());

        // get
        Category cat = adminClient.getCategory(toyotas.getId());
        assertForCategories(cat, toyotas);

        // get all
        Collection<Category> allCategories = adminClient.getAllCategories();
        assertThat(allCategories).hasSize(categories.size());

        // create
        Category createdCategory = adminClient.createCategory(transport);
        assertForCategories(createdCategory, transport);

        // update
        Category updateCategory = adminClient.updateCategory(autos);
        assertForCategories(updateCategory, autos);

        // delete
        Response response = adminClient.deleteCategory(cars.getId());
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(204);
    }

    private void assertForCategories(Category found, Category expected) {
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(expected.getId());
        assertThat(found.getName()).isEqualTo(expected.getName());
        assertThat(found.getHeader()).isEqualTo(expected.getHeader());
        assertThat(found.getImagePath()).isEqualTo(expected.getImagePath());
        assertThat(found.isVisible()).isEqualTo(expected.isVisible());

        if (expected.getParent() == null) {
            assertThat(found.getParent()).isNull();
        } else {
            assertThat(found.getParent()).isNotNull();
            assertThat(found.getParent().getId()).isEqualTo(expected.getParent().getId());
        }
    }
}
