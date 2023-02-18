package io.github.mathieusoysal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UserCustomizationTest {
	
	@Test
	void test_matcher() {
		UserCustomization userCustomization = new UserCustomization(" dsqf sq $$Template-guider$$Hello$$ fdsf sqq", null);
		assertEquals("Hello", userCustomization.getVariableName());
	}

}
