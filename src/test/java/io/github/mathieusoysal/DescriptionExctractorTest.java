package io.github.mathieusoysal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;

public class DescriptionExctractorTest {

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

}
