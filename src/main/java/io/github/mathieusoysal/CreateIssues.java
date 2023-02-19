package io.github.mathieusoysal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHRepository;

import io.github.mathieusoysal.exceptions.ConfigFileNotFoundException;
import io.quarkiverse.githubapp.event.Repository;

public class CreateIssues {
	private static final String BALISE = "$$Template-guider$$";

	void onCreate(@Repository.Created GHEventPayload.Repository repositoryPayload) throws IOException {
		var repo = repositoryPayload.getRepository();

		// a arrayList with all the files in the repo
		var files = getAllFilesFromRepo(repo);

		List<UserCustomization> userCustomizations = getUserCustomizationsFromManyFiles(files);
		String body = "";
		try {
			DescriptionExctractor descriptions = new DescriptionExctractor(repo);
			body = userCustomizations.stream()
					.map((u) -> "## " + u.getVariableName() + "\n" + descriptions.getDescription(u) + "\n### Links:\n"
							+ String.join("\n", u.getGetUrls()))
					.collect(Collectors.joining("\n"));
		} catch (ConfigFileNotFoundException e) {
			body = e.getMessage();
		}

		repo.createIssue("Hello from my GitHub App")
				.body(body)
				.label("Init project")
				.create();
	}

	/**
	 * Get all the files from a repository.
	 * 
	 * @param repo
	 * @return List<GHContent> with all the files in the repo
	 * @throws IOException
	 */
	private List<GHContent> getAllFilesFromRepo(GHRepository repo) throws IOException {
		var files = new ArrayList<GHContent>();
		Queue<GHContent> ghContents = new LinkedList<GHContent>();
		repo.getDirectoryContent("").forEach(v -> {
			if (v.isFile()) {
				files.add(v);
			} else {
				ghContents.add(v);
			}
		});
		while (!ghContents.isEmpty()) {
			var directory = ghContents.poll();
			var directoryFiles = directory.listDirectoryContent();
			for (var file : directoryFiles) {
				if (file.isFile()) {
					files.add(file);
				} else {
					ghContents.addAll(file.listDirectoryContent().toList());
				}
			}
		}
		return files;
	}

	static List<UserCustomization> getUserCustomizationsFromManyFiles(List<GHContent> files) {
		Map<UserCustomization, UserCustomization> userCustomizations = new HashMap<UserCustomization, UserCustomization>();
		files.parallelStream()
				.map((file) -> {
					try {
						return getUserCustomizationsFromSingleFile(file);
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				})
				.flatMap(List::stream)
				.collect(Collectors.toList())
				.forEach((v) -> userCustomizations.compute(v, (k, u) -> {
					if (u == null) {
						return v;
					} else {
						u.combine(v);
						return u;
					}
				}));
		return new ArrayList<UserCustomization>(userCustomizations.values());
	}

	/**
	 * Search all BALISE in a given file.
	 * 
	 * @param file
	 * @return List<UserCustomization> with all the customizations needed in the
	 *         file
	 * @throws IOException
	 */
	static List<UserCustomization> getUserCustomizationsFromSingleFile(GHContent file) throws IOException {
		var Usercustomizations = new ArrayList<UserCustomization>();
		InputStreamReader isr = new InputStreamReader(file.read(),
				StandardCharsets.UTF_8);
		BufferedReader br = new BufferedReader(isr);
		var cpt = 1;
		for (var line : br.lines().collect(Collectors.toList())) {
			if (line.contains(BALISE)) {
				Usercustomizations.add(new UserCustomization(line, file, cpt));
			}
			cpt++;
		}
		return Usercustomizations;
	}

}
