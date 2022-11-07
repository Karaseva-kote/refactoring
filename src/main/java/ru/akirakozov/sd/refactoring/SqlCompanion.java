package ru.akirakozov.sd.refactoring;

import com.google.common.base.Splitter;
import com.google.common.io.Resources;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.io.Resources.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

public class SqlCompanion {
	private static final Pattern MARKER_LINE = Pattern.compile("^\\s*--\\s*@([A-Z0-9_]{3,})\\s?.*$");

	private final Map<String, String> fragmentMap;
	private final String text;

	private SqlCompanion(String text, Map<String, String> fragmentMap) {
		this.fragmentMap = copyOf(fragmentMap);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public String getQuery(String name) {
		return fragmentMap.get(name);
	}

	public String getRequiredQuery(String name) {
		return requireNonNull(getQuery(name), () -> "Query " + name + " is not found");
	}

	public static SqlCompanion forClass(Class<?> clazz) {
		Map<String, String> fragments = new HashMap<>();
		String text = getText(clazz);

		List<String> lines = Splitter.on("\n").splitToList(text);
		Deque<String> fragment = new LinkedList<>();

		for (int i = lines.size() - 1; i >= 0; i--) {
			String line = lines.get(i);

			Matcher matcher = MARKER_LINE.matcher(line);
			if (matcher.matches()) {
				fragments.put(
						matcher.group(1), String.join("\n", fragment)
				);
				fragment.clear();
				continue;
			}

			fragment.addFirst(line);
		}

		return new SqlCompanion(text, fragments);
	}

	private static String getText(Class<?> clazz) {
		try {
			return Resources.toString(
					getResource(clazz.getSimpleName() + ".sql"),
					UTF_8
			);
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}
}