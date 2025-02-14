package pl.allegro.tech.hermes.integration.management;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;
import pl.allegro.tech.hermes.api.ErrorCode;
import pl.allegro.tech.hermes.api.OAuthProvider;
import pl.allegro.tech.hermes.api.PatchData;
import pl.allegro.tech.hermes.integration.IntegrationTest;

import java.util.List;
import javax.ws.rs.core.Response;

import static pl.allegro.tech.hermes.api.PatchData.patchData;
import static pl.allegro.tech.hermes.integration.test.HermesAssertions.assertThat;
import static pl.allegro.tech.hermes.test.helper.builder.OAuthProviderBuilder.oAuthProvider;

public class OAuthProviderManagementTest extends IntegrationTest {

    @Test
    public void shouldCreateOAuthProvider() {
        // given
        OAuthProvider oAuthProvider = oAuthProvider("myProvider").withSocketTimeout(200).build();

        // when
        Response response = management.oAuthProvider().create(oAuthProvider);

        // then
        assertThat(response).hasStatus(Response.Status.CREATED);
        OAuthProvider createdProvider = management.oAuthProvider().get(oAuthProvider.getName());
        Assertions.assertThat(createdProvider.getSocketTimeout()).isNotNull();
        Assertions.assertThat(createdProvider.getSocketTimeout()).isEqualTo(200);
    }

    @Test
    public void shouldNotAllowCreatingTheSameOAuthProviderTwice() {
        // given
        OAuthProvider oAuthProvider = oAuthProvider("originalProvider").build();
        Response response = management.oAuthProvider().create(oAuthProvider);
        assertThat(response).hasStatus(Response.Status.CREATED);

        // when
        Response secondTryResponse = management.oAuthProvider().create(oAuthProvider);

        // then
        assertThat(secondTryResponse).hasStatus(Response.Status.BAD_REQUEST)
                .hasErrorCode(ErrorCode.OAUTH_PROVIDER_ALREADY_EXISTS);
    }

    @Test
    public void shouldUpdateOAuthProvider() {
        // given
        OAuthProvider oAuthProvider = oAuthProvider("myOtherProvider").build();
        Response response = management.oAuthProvider().create(oAuthProvider);
        assertThat(response).hasStatus(Response.Status.CREATED);

        // when
        PatchData patch = patchData()
                .set("tokenEndpoint", "http://other.example.com/other")
                .set("socketTimeout", 100).build();
        Response updateResponse = management.oAuthProvider().update(oAuthProvider.getName(), patch);

        // then
        assertThat(updateResponse).hasStatus(Response.Status.OK);
        OAuthProvider updatedProvider = management.oAuthProvider().get(oAuthProvider.getName());
        assertThat(updatedProvider.getTokenEndpoint()).isEqualTo("http://other.example.com/other");
        assertThat(updatedProvider.getSocketTimeout()).isEqualTo(100);
    }

    @Test
    public void shouldRemoveOAuthProvider() {
        // given
        OAuthProvider oAuthProvider = oAuthProvider("myProviderForRemoval").build();
        Response response = management.oAuthProvider().create(oAuthProvider);
        assertThat(response).hasStatus(Response.Status.CREATED);

        // when
        Response removalResponse = management.oAuthProvider().remove(oAuthProvider.getName());

        // then
        assertThat(removalResponse).hasStatus(Response.Status.OK);
        List<String> providers = management.oAuthProvider().list();
        assertThat(providers).doesNotContain(oAuthProvider.getName());
    }
}
