package com.ralf.asac;

import java.time.LocalTime;
import java.util.regex.Pattern;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;

public class TimeTextField extends TextField {

	private static final Pattern PARTIAL = Pattern.compile("^(?:[01]?\\d|2[0-3])?(?::[0-5]?\\d?)?$");

	private static final Pattern COMPLETE = Pattern.compile("^(?:[01]?\\d|2[0-3]):[0-5]\\d$");

	public TimeTextField() {
		this(null);
	}

	@SuppressWarnings("unused")
	public TimeTextField(final LocalTime time) {
		setPromptText("HH:mm");
		setPrefColumnCount(5);

		setTextFormatter(new TextFormatter<String>(this::filter));

		focusedProperty().addListener((obs, wasFocused, isFocused) -> {
			if (isFocused == null || !isFocused) {
				normalize();
			}
		});

		if (time != null) {
			setTime(time);
		}
	}

	private Change filter(final Change change) {
		if (!change.isContentChange()) {
			return change;
		}

		change.setText(change.getText().replace('.', ':').replace(',', ':').replace(';', ':'));

		final String newText = change.getControlNewText();

		return PARTIAL.matcher(newText).matches() ? change : null;
	}

	private void normalize() {
		final String text = getText();
		if (text == null || text.isEmpty()) {
			return;
		}
		final String[] parts = text.split(":", -1);
		final int hours = Integer.parseInt(parts[0]);
		final int minutes = (parts.length > 1 && !parts[1].isEmpty()) ? Integer.parseInt(parts[1]) : 0;
		setTime(LocalTime.of(hours, minutes));
	}

	LocalTime getTime() {
		final String text = getText();
		if (text == null || !COMPLETE.matcher(text).matches()) {
			return null;
		}
		String[] parts = text.split(":");
		return LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
	}

	void setTime(final LocalTime time) {
		setText(time == null ? "" : String.format("%02d:%02d", time.getHour(), time.getMinute()));
	}
}