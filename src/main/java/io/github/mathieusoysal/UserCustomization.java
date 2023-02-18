package io.github.mathieusoysal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.kohsuke.github.GHContent;

public class UserCustomization {
	private static final Pattern REGEX = Pattern.compile("(?<=\\$\\$Template-guider\\$\\$).*(?=\\$\\$)",
			Pattern.MULTILINE);
	private Map<GHContent, List<Integer>> matches;
	private String variableName;

	public UserCustomization(String line, GHContent content, Integer... lines) {
		this.variableName = REGEX.matcher(line).results().toList().get(0).group();
		this.matches = new java.util.HashMap<>();
		this.matches.put(content, Arrays.asList(lines));
	}

	public String toString() {
		return "## " + this.variableName + "\n" + this.matches.entrySet().stream()
				.map(e -> e.getKey().getHtmlUrl() + "#L"
						+ e.getValue().stream().map(Object::toString).collect(Collectors.joining("#L")))
				.collect(Collectors.joining("\n"));
	}

	public List<String> getGetUrls() {
		return this.matches.entrySet().stream()
				.map(e -> e.getKey().getHtmlUrl() + "#L"
						+ e.getValue().stream().map(Object::toString).collect(Collectors.joining("#L")))
				.collect(Collectors.toList());
	}

	public void addMatch(GHContent content, Integer... lines) {
		this.matches.compute(content, (k, v) -> {
			if (v == null) {
				return Arrays.asList(lines);
			} else {
				v.addAll(Arrays.asList(lines));
				return v;
			}
		});
	}

	public void combine(UserCustomization other) {
		this.matches.putAll(other.matches);
	}

	public Map<GHContent, List<Integer>> getMatches() {
		return matches;
	}

	public String getVariableName() {
		return variableName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((variableName == null) ? 0 : variableName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserCustomization other = (UserCustomization) obj;
		if (variableName == null) {
			if (other.variableName != null)
				return false;
		} else if (!variableName.equals(other.variableName))
			return false;
		return true;
	}

}
