package io.github.mathieusoysal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHEvent;
import org.kohsuke.github.GHIssueBuilder;
import org.kohsuke.github.GHRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkiverse.githubapp.testing.GitHubAppTest;
import io.quarkiverse.githubapp.testing.GitHubAppTesting;
import io.quarkiverse.githubapp.testing.dsl.GitHubMockSetupContext;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@GitHubAppTest
@ExtendWith(MockitoExtension.class)
public class CreateIssuesTest {

	private GHRepository mockGHRepo;
	private GHContent mockGHContent;
	private GHIssueBuilder mockGHIssueBuilder;

	private void setupMock() {
		mockGHRepo = mock(GHRepository.class);
		mockGHContent = mock(GHContent.class);
	}

	private void setupMockForEvent(GitHubMockSetupContext mocks, String repository) {
		mockGHRepo = mocks.repository(repository);
		mockGHContent = mock(GHContent.class);
		mockGHIssueBuilder = mock(GHIssueBuilder.class);
		when(mockGHRepo.createIssue(any())).thenReturn(mockGHIssueBuilder);
		when(mockGHIssueBuilder.label(any())).thenReturn(mockGHIssueBuilder);
		when(mockGHIssueBuilder.body(any())).thenReturn(mockGHIssueBuilder);
	}

	@Test
	void test_onCreate_withEmptyFolder() throws RuntimeException, IOException {
		GitHubAppTesting.given().github((mocks) -> {
			setupMockForEvent(mocks, "MathieuSoysal/testtestss");
		}).when()
				.payloadFromClasspath("/repo-created.json")
				.event(GHEvent.REPOSITORY)
				.then().github(mocks -> {
					Mockito.verify(mockGHRepo).createIssue("Hello from my GitHub App");
					ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
					ArgumentCaptor<String> labelCaptor = ArgumentCaptor.forClass(String.class);
					Mockito.verify(mockGHIssueBuilder).body(bodyCaptor.capture());
					Mockito.verify(mockGHIssueBuilder).label(labelCaptor.capture());
					assertEquals(
							"Config file not found. Please create a .template-guider.md file in the root of your repository.",
							bodyCaptor.getValue());
					assertEquals("Init project", labelCaptor.getValue());
				});
	}

	@Test
	void test_onCreate_withNotEmptyFolder() throws RuntimeException, IOException {
		GitHubAppTesting.given().github((mocks) -> {
			setupMockForEvent(mocks, "MathieuSoysal/testtestss");
			when(mockGHContent.read()).thenReturn(new FileInputStream("src/test/resources/.template-guider.md"));
			when(mockGHContent.isFile()).thenReturn(true);
			when(mockGHContent.getName()).thenReturn(".template-guider.md");
			var mockGHContent2 = mock(GHContent.class);
			when(mockGHContent2.isFile()).thenReturn(true);
			when(mockGHContent2.read()).thenReturn(new FileInputStream("src/test/resources/exempleWithUserCustom.txt"));
			when(mockGHRepo.getDirectoryContent("")).thenReturn(Arrays.asList(mockGHContent, mockGHContent2));
		}).when()
				.payloadFromClasspath("/repo-created.json")
				.event(GHEvent.REPOSITORY)
				.then().github(mocks -> {
					Mockito.verify(mockGHRepo).createIssue("Hello from my GitHub App");
					ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
					ArgumentCaptor<String> labelCaptor = ArgumentCaptor.forClass(String.class);
					Mockito.verify(mockGHIssueBuilder).body(bodyCaptor.capture());
					Mockito.verify(mockGHIssueBuilder).label(labelCaptor.capture());
					assertEquals(
							"""
									## TEST44
									null
									### Links:
									null#L1""",
							bodyCaptor.getValue());
					assertEquals("Init project", labelCaptor.getValue());
				});
	}

	@Nested
	@DisplayName("tests for getUserCustomizationsFromSingleFile")
	class tests_getUserCustomizationsFromManyFiles {

		private GHContent ghContent1;
		private GHContent ghContent2;
		private GHContent ghContent3;
		private List<GHContent> ghContents;

		@BeforeEach
		void beforeAll() throws FileNotFoundException, IOException {
			setupMock();

			ghContent1 = mock(GHContent.class);
			ghContent2 = mock(GHContent.class);
			ghContent3 = mock(GHContent.class);
			ghContents = Arrays.asList(ghContent1, ghContent2, ghContent3);
			when(ghContent1.read()).thenReturn(new FileInputStream("src/test/resources/exempleWithUserCustom.txt"));
			when(ghContent2.read()).thenReturn(new FileInputStream("src/test/resources/exempleWithUserCustom2.txt"));
			when(ghContent3.read()).thenReturn(new FileInputStream("src/test/resources/emptyFile.txt"));
		}

		@Test
		void test_getUserCustomizationsFromManyFiles_goodSize() throws IOException {
			var userCustomizations = CreateIssues.getUserCustomizationsFromManyFiles(ghContents);

			assertEquals(2, userCustomizations.size());
		}

		@Test
		void test_getUserCustomizationsFromManyFiles_goodContent() throws IOException {
			var userCustomizations = CreateIssues.getUserCustomizationsFromManyFiles(ghContents);

			assertEquals("TEST44", userCustomizations.get(0).getVariableName());
			assertEquals("TEST1", userCustomizations.get(1).getVariableName());
		}

		@Test
		void test_getUserCustomizationsFromManyFiles_goodNumberUrls() throws IOException {
			when(ghContent1.getHtmlUrl()).thenReturn("Custom.txt");
			when(ghContent2.getHtmlUrl()).thenReturn("Custom2.txt");

			var userCustomizations = CreateIssues.getUserCustomizationsFromManyFiles(ghContents);

			assertEquals(2, userCustomizations.get(0).getGetUrls().size());
			assertEquals(1, userCustomizations.get(1).getGetUrls().size());
		}

		@Test
		void test_getUserCustomizationsFromManyFiles_goodUrls() throws IOException {
			when(ghContent1.getHtmlUrl()).thenReturn("Custom.txt");
			when(ghContent2.getHtmlUrl()).thenReturn("Custom2.txt");

			var userCustomizations = CreateIssues.getUserCustomizationsFromManyFiles(ghContents);


			assertTrue( userCustomizations.get(0).getGetUrls().contains("Custom.txt#L1"));
			assertTrue(userCustomizations.get(1).getGetUrls().contains("Custom2.txt#L9"));
		}

	}

	@Test
	void test_getUserCustomizationsFromSingleFile_withSingleKey() throws IOException {
		setupMock();

		when(mockGHContent.read()).thenReturn(new FileInputStream("src/test/resources/exempleWithUserCustom.txt"));

		var userCustomizations = CreateIssues.getUserCustomizationsFromSingleFile(mockGHContent);

		verify(mockGHContent, times(1)).read();
		assertEquals(1, userCustomizations.size());
	}

}
