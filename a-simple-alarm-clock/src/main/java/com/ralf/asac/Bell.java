package com.ralf.asac;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.StrokeTransition;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Bell implements IBell {

	private AudioOutput audioOutput;

	@SuppressWarnings({ "unused", "java:S2142", "java:S4507" })
	@Override
	public void play(final List<AlarmData> alarmDatas) {
		Platform.runLater(() -> {

			final URL url = ClassLoader.getSystemResource("default.wav");

			if (url != null) {
				audioOutput = new AudioOutput(url);
				audioOutput.play();
			}
			final Stage stage = new Stage();
			stage.initStyle(StageStyle.TRANSPARENT);

			final List<Node> nodes = new ArrayList<>();

			final int width = 256;
			final int height = 128;

			final double rectangleStrokeWidth = 8;

			final Rectangle rectangle = new Rectangle(width, height);
			rectangle.setStroke(Color.BLUE);
			rectangle.setStrokeWidth(rectangleStrokeWidth);
			rectangle.setFill(Color.WHITE);
			nodes.add(rectangle);

			final Font font = new Font(new Text("Dummy").getFont().getName(), 15);

			final List<String> names = getTexts(alarmDatas, width, rectangleStrokeWidth, font);

			final StringBuilder allNames = new StringBuilder();

			final int maxNumberNames = 5;

			for (int index = 0; index < maxNumberNames && index < names.size(); index++) {
				if (index != 0) {
					allNames.append("\n");
				}
				allNames.append(names.get(index));
			}

			if (maxNumberNames < names.size()) {
				allNames.append("\n(...)");
			}

			final Text javaFxText = new Text(allNames.toString());
			nodes.add(javaFxText);

			final StackPane stackPane = new StackPane();
			stackPane.setMinSize(width, height);
			stackPane.setPrefSize(width, height);
			stackPane.setMaxSize(width, height);
			stackPane.getChildren().addAll(nodes);
			stackPane.setStyle("-fx-background-color: transparent;");

			final StrokeTransition transition = new StrokeTransition(new Duration(100.), rectangle);

			transition.setFromValue(Color.RED);
			transition.setToValue(Color.BLUE);
			transition.setCycleCount(Animation.INDEFINITE);
			transition.setAutoReverse(true);
			transition.play();

			final Scene scene = new Scene(stackPane);
			scene.setFill(Color.TRANSPARENT); // Background transparent
			stage.setScene(scene);

			scene.setOnMouseClicked(event -> {
				audioOutput.stopPlaying();
				stage.close();
			});

			stage.setOnCloseRequest(event -> audioOutput.stopPlaying());

			stage.setAlwaysOnTop(true);
			stage.show();

			final Point2D point = Asac.getCoordinatesofMiddleOfTheScreen(stage);

			stage.setX(point.getX());
			stage.setY(point.getY() - 200); // 200 is for avoiding that this window is
											// overlapping the main window

			try {
				Thread.sleep(1000);
			} catch (InterruptedException exception) {
				exception.printStackTrace();
			}
		});
	}

	List<String> getTexts(final List<AlarmData> alarmDatas, final double width, final double rectangleStrokeWidth,
			final Font font) {

		final List<String> names = new ArrayList<String>();

		final double minimalDistanceRectangleText = 1;

		for (final AlarmData alarmData : alarmDatas) {
			String nameToBeShown = alarmData.name;
			double textWidth = 0;
			do {
				textWidth = getWidthText(nameToBeShown + "...", font);

				if (textWidth > width - 2 * (rectangleStrokeWidth + minimalDistanceRectangleText)) {
					nameToBeShown = nameToBeShown.substring(0, nameToBeShown.length() - 2);
				}

			} while (textWidth > width);

			if (!alarmData.name.equals(nameToBeShown)) {
				nameToBeShown += "...";
			}

			names.add(nameToBeShown);
		}

		return names;

	}

	private double getWidthText(final String text, final Font font) {
		final Text textForDefiningTheWidthOfTheText = new Text(text + "...");
		textForDefiningTheWidthOfTheText.setFont(font);
		final Bounds bounds = textForDefiningTheWidthOfTheText.getLayoutBounds();
		return bounds.getWidth();
	}

}
