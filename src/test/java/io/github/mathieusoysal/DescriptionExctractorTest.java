package io.github.mathieusoysal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.mathieusoysal.exceptions.ConfigFileNotFoundException;
import io.quarkiverse.githubapp.testing.GitHubAppTest;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@GitHubAppTest
@ExtendWith(MockitoExtension.class)
public class DescriptionExctractorTest {

	private GHRepository mockGHRepo;
	private GHContent mockGHContent;

	@Test
	void test_getDescription() throws IOException {
		String expectedResult = "Hello test1";
		DescriptionExctractor descriptionExctractor = new DescriptionExctractor(
				new InputStreamReader(new ByteArrayInputStream(("## Hello\n" + expectedResult).getBytes())));
		String description = descriptionExctractor.getDescription("Hello");
		assertEquals(description, expectedResult);
	}

	@Test
	void test_getDescription2() throws IOException {
		String expectedResult = "Hello test1\nDescription\nlong";
		DescriptionExctractor descriptionExctractor = new DescriptionExctractor(
				new InputStreamReader(new ByteArrayInputStream(("## Hello\n" + expectedResult).getBytes())));
		String description = descriptionExctractor.getDescription("Hello");
		assertEquals(description, expectedResult);
	}

	@Test
	void test_getDescription_withSeveralKeys() throws IOException {
		String expectedResult = "Hello test1\nDescription\nlong";
		DescriptionExctractor descriptionExctractor = new DescriptionExctractor(new InputStreamReader(
				new ByteArrayInputStream(
						("## Hello\ndsf sqfqdsfqdsfdqsf\ndsfqdsfqsdfdsq\nfdsqfq" + "\n## Hello2\n" + expectedResult)
								.getBytes())));
		String description = descriptionExctractor.getDescription("Hello2");
		assertEquals(description, expectedResult);
	}

	@Test
	void test_getDescription_withFile() throws IOException {
		// take the file .test-template-guidor.md
		FileReader fileReader = new FileReader("src/test/resources/.test-template-guidor.md");
		var descriptions = new DescriptionExctractor(fileReader);

		assertEquals(descriptions.getDescription("Test1"), "This is test1\n");
	}

	@Test
	void test_getDescription_withGitHubRepo() throws FileNotFoundException, IOException, ConfigFileNotFoundException {
		setupMock();
		when(mockGHContent.getName()).thenReturn(".template-guider.md");
		when(mockGHContent.isFile()).thenReturn(true);
		when(mockGHContent.read()).thenReturn(new FileInputStream("src/test/resources/.test-template-guidor.md"));

		var description = new DescriptionExctractor(mockGHRepo);

		assertEquals("This is test1\n", description.getDescription("Test1"));
		assertEquals("This is test2", description.getDescription("Test2"));
		verify(mockGHRepo, times(1)).getDirectoryContent(eq(""));
		verify(mockGHContent, times(1)).read();
	}

	@Test
	void test_getDescription_withGitHubRepoAndConfigFileNotFound()
			throws IOException, ConfigFileNotFoundException {
		setupMock();
		when(mockGHContent.isFile()).thenReturn(false);

		assertThrows(ConfigFileNotFoundException.class, () -> new DescriptionExctractor(mockGHRepo));
	}

	private void setupMock() throws FileNotFoundException, IOException {
		mockGHContent = mock(GHContent.class);

		mockGHRepo = mock(GHRepository.class);
		when(mockGHRepo.getDirectoryContent(any())).thenAnswer((s)-> Arrays.asList(mockGHContent));
	}

}
