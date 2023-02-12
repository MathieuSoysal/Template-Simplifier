package io.github.mathieusoysal;

import java.io.IOException;
import org.kohsuke.github.GHEventPayload;
import io.quarkiverse.githubapp.event.Issue;

public class CreateComment {

	void onOpen(@Issue.Opened GHEventPayload.Issue issuePayload) throws IOException {
		issuePayload.getIssue().comment("Hello from my GitHub App");
	}
}