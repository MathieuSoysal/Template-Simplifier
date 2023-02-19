package io.github.mathieusoysal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHContent;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserCustomizationTest {

	@Test
	void test_matcher() {
		UserCustomization userCustomization = new UserCustomization(" dsqf sq $$Template-guider$$Hello$$ fdsf sqq",
				null);
		assertEquals("Hello", userCustomization.getVariableName());
	}

	@Test
	void test_combine(){
		var ghContent = mock(GHContent.class);
		when(ghContent.getHtmlUrl()).thenReturn("url");

		var userC1 = new UserCustomization(" dsqf sq $$Template-guider$$Hello$$ fdsf sqq", ghContent, 1);
		var userC2 = new UserCustomization(" dsqf sq $$Template-guider$$Hello$$ fdsf sqq", ghContent, 2);

		userC1.combine(userC2);


		assertEquals("url#L1#L2", userC1.getGetUrls().get(0));
	}

}
