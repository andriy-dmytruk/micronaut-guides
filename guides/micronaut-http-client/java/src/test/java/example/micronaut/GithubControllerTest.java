package example.micronaut;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest // <1>
class GithubControllerTest {

    @Inject
    @Client("/")
    HttpClient client; // <2>

    private static Pattern MICRONAUT_RELEASE = Pattern.compile("Micronaut [0-9].[0-9].[0-9]([0-9])?( (RC|M)[0-9])?");

    @Test
    public void verifyGithubReleasesCanBeFetchedWithLowLevelHttpClient() {
        //when:
        HttpRequest<Object> request = HttpRequest.GET("/github/releases-lowlevel");

        HttpResponse<List<GithubRelease>> rsp = client.toBlocking().exchange(request, // <3>
                Argument.listOf(GithubRelease.class)); // <4>

        //then: 'the endpoint can be accessed'
        assertEquals(HttpStatus.OK, rsp.getStatus());   // <5>
        assertNotNull(rsp.body()); // <6>

        //when:
        List<GithubRelease> releases = rsp.body();

        //then:
        assertNotNull(releases);
        assertTrue(releases.stream()
                .map(GithubRelease::getName)
                .allMatch(name -> MICRONAUT_RELEASE.matcher(name)
                        .find()));
    }

    @Test
    public void verifyGithubReleasesCanBeFetchedWithCompileTimeAutoGeneratedAtClient() {
        //when:
        HttpRequest<Object> request = HttpRequest.GET("/github/releases-lowlevel");

        List<GithubRelease> githubReleases = client.toBlocking().retrieve(request, Argument.listOf(GithubRelease.class)); // <7>

        //then:
        assertTrue(githubReleases.stream()
                .map(GithubRelease::getName)
                .allMatch(name -> MICRONAUT_RELEASE.matcher(name)
                        .find()));
    }
}
