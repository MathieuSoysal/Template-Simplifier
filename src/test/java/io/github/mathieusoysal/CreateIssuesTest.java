package io.github.mathieusoysal;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHEvent;
import org.mockito.Mockito;

import io.quarkiverse.githubapp.testing.GitHubAppTest;
import io.quarkiverse.githubapp.testing.GitHubAppTesting;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@GitHubAppTest
public class CreateIssuesTest {
	@Test
	void testIssueOpened() throws IOException {
		GitHubAppTesting.when()
				.payloadFromClasspath("/repo-created.json")
				.event(GHEvent.REPOSITORY)
	
				.then().github(mocks -> Mockito.verify(mocks.repository("MathieuSoysal/testtestss"))
						.createIssue("Hello from my GitHub App").body("Test").label("Init project"));
	}
}
