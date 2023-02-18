package io.github.mathieusoysal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;

import io.github.mathieusoysal.exceptions.ConfigFileNotFoundException;

/**
 * This class is used to extract descriptions from the template-guider.md file.
 * 
 * 
 */
public class DescriptionExctractor {
	private Map<String, String> descriptions;

	/**
	 * This is the constructor of the class.
	 * 
	 * @param repo The GitHub repository from which the template-guider.md file is
	 * @throws ConfigFileNotFoundException If the .template-guider.md file is not
	 *                                     found
	 * @throws IOException                 If the .template-guider.md file cannot be
	 *                                     read
	 */
	public DescriptionExctractor(GHRepository repo) throws ConfigFileNotFoundException, IOException {
		this(new InputStreamReader(getTemplateGuidorFile(repo).read(), StandardCharsets.UTF_8));
	}

	DescriptionExctractor(InputStreamReader inputStreamReader) throws IOException {
		this.descriptions = new HashMap<>();

		var br = new BufferedReader(inputStreamReader);
		var currentKey = "";
		for (var line : br.lines().collect(Collectors.toList())) {
			if (line.contains("## "))
				currentKey = line.replace("## ", "");
			else
				descriptions.compute(currentKey, (k, v) -> v == null ? line : v + "\n" + line);
		}
		br.close();
	}

	/**
	 * This is a static method of the class.
	 * It takes a GitHub repository as an argument.
	 * It returns the template-guider.md file from the GitHub repository.
	 * It returns the template-guider.md file from the GitHub repository, which is
	 * used to give the user an overview of the variables that can b
	 * If the template-guider.md file is not found, it throws a
	 * ConfigFileNotFoundException.
	 * 
	 * @param repo The GitHub repository from which the template-guider.md file is
	 * @return The template-guider.md file from the GitHub repository
	 * @throws ConfigFileNotFoundException If the .template-guider.md file is not
	 *                                     found
	 * @throws IOException                 If the .template-guider.md file cannot be
	 *                                     read
	 */
	static GHContent getTemplateGuidorFile(GHRepository repo) throws ConfigFileNotFoundException, IOException {
		return repo.getDirectoryContent("").stream()
				.filter(v -> v.isFile() && v.getName().equals(".template-guider.md")).findFirst()
				.orElseThrow(() -> new ConfigFileNotFoundException());
	}

	/**
	 * Retuns the description of the variable from the template-guider.md file.
	 * 
	 * @param key The name of the variable
	 * @return The description of the variable
	 */
	public String getDescription(String key) {
		return descriptions.get(key);
	}

	/**
	 * This is a getter method of the class.
	 * It takes a UserCustomization object as an argument.
	 * It returns the description of the variable from the .template-guider.md file.
	 * 
	 * @param userCustomization The UserCustomization object
	 * @return The description of the variable with the same name as the
	 *         UserCustomization object that was passed to it
	 */
	public String getDescription(UserCustomization userCustomization) {
		return getDescription(userCustomization.getVariableName());
	}
}