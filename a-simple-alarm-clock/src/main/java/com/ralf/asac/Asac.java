package com.ralf.asac;

import java.util.Optional;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Asac {

	public static void main(final String[] args) {
		MainClass.start(args);
	}

	static Point2D getCoordinatesofMiddleOfTheScreen(final Stage stage) {
		final Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
		final double stageWidth = stage.getWidth();
		final double stageHeight = stage.getHeight();
		final double windowsPositionX = (primScreenBounds.getWidth() - stageWidth) / 2;
		final double windowsPositionY = (primScreenBounds.getHeight() - stageHeight) / 2;

		return new Point2D(windowsPositionX, windowsPositionY);
	}

	static void askConfirmation(final String message, Consumer<Boolean> callback) {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.CONFIRMATION, message);
			Optional<ButtonType> result = alert.showAndWait();
			callback.accept(result.isPresent() && result.get() == ButtonType.OK);
		});
	}

	static void showError(final Stage owner, final String message) {
		Runnable task = () -> {
			final Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle(MainClass.messages.getString("error"));
			alert.setHeaderText(null);
			alert.setContentText(message);
			if (owner != null) {
				alert.initOwner(owner);
			}
			alert.showAndWait();
		};

		if (Platform.isFxApplicationThread()) {
			task.run();
		} else {
			Platform.runLater(task);
		}
	}

	@SuppressWarnings({ "java:S2142", "java:S4507" })
	static void sleep(long milliseconds) {

		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException exception) {
			exception.printStackTrace();
		}
	}
}
