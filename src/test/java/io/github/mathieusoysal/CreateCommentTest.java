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
class CreateCommentTest {
	@Test
	void testIssueOpened() throws IOException {
		GitHubAppTesting.when()
				.payloadFromClasspath("/issue-opened.json")
				.event(GHEvent.ISSUES)
				.then().github(mocks -> {
					Mockito.verify(mocks.issue(1586070108))
							.comment("Hello from my GitHub App");
				});
	}
}