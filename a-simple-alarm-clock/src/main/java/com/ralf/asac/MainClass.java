package com.ralf.asac;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainClass extends Application {

	static final ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.getDefault());

	private Stage stage;

	private StackPane stackPane;
	private VBox vBox;
	private Button addButton;
	private ArrayList<AlarmsLine> alarmLines;
	private SleepingTask sleepingTask;

	public static void start(final String[] args) {
		launch(args);
	}

	@SuppressWarnings("unused")
	@Override
	public void start(final Stage stage) throws Exception {
		this.stage = stage;
		vBox = new VBox(5);

		final ScrollPane scrollPane = new ScrollPane(vBox);
		scrollPane.setFitToWidth(true);

		final StackPane frame = new StackPane(scrollPane);
		frame.setStyle("-fx-border-color: gray; -fx-border-radius: 4; " + "-fx-padding: 15 5 5 5;");

		stackPane = new StackPane(frame);

		stackPane.setPadding(new Insets(20));

		alarmLines = new ArrayList<>();

		final List<AlarmData> alarmDatas = Preferences.getAlarms();

		for (final AlarmData alarmData : alarmDatas) {
			final AlarmsLine alarmLine = new AlarmsLine(this);
			alarmLines.add(alarmLine);
			alarmLine.alarmData = alarmData;

			alarmLine.isActive.setSelected(alarmData.isActive());

			alarmLine.name.setText(alarmData.name);
		}

		addButton = new Button(MainClass.messages.getString("add"));

		update();

		addButton.setOnAction(event -> {
			final AlarmsLine alarmLine = new AlarmsLine(this);
			AlarmManager alarmManager = new AlarmManager(this, alarmLine.alarmData);
			if (alarmManager.addButtonWasFired()) {
				AlarmsLine alarmsLine = new AlarmsLine(this);
				alarmsLine.alarmData = alarmManager.getData();
				alarmsLine.name.setText(alarmsLine.alarmData.name);
				alarmsLine.isActive.setSelected(alarmsLine.alarmData.isActive());
				alarmLines.add(alarmsLine);
				update();
			}
		});

		final Scene scene = new Scene(stackPane);

		stage.setTitle(MainClass.messages.getString("MainClass.title"));
		setIcon();
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
		Platform.runLater(stage::centerOnScreen);
	}

	Stage getStage() {
		return stage;
	}

	@SuppressWarnings("java:S9391")
	void update() {
		vBox.getChildren().clear();

		if (alarmLines.isEmpty()) {
			alarmLines.add(new AlarmsLine(this));
		}

		for (final AlarmsLine alarmLine : alarmLines) {
			vBox.getChildren().add(alarmLine.pane);
		}
		vBox.getChildren().add(addButton);

		stage.sizeToScene();

		final List<AlarmData> alarmDatas = new ArrayList<>();
		for (final AlarmsLine alarmLine : alarmLines) {
			alarmDatas.add(alarmLine.alarmData);
		}

		final Scheduler scheduler = new Scheduler(alarmDatas, LocalDateTime.now(ZoneId.systemDefault()));

		if (sleepingTask != null) {
			sleepingTask.stop();
		}

		final List<AlarmData> nextAlarms = scheduler.getNextAlarms();

		sleepingTask = new SleepingTask(nextAlarms, scheduler.getMinimalTime(), new SleepingTaskController(),
				new Bell(), this);
		sleepingTask.start();

		final List<AlarmData> alarmsToBeDiscard = scheduler.geteAlarmsToBeDiscard();

		for (final AlarmData alarmToBeDiscard : alarmsToBeDiscard) {
			final Iterator<AlarmsLine> iterator = alarmLines.iterator();
			while (iterator.hasNext()) {
				final AlarmsLine alarmLine = iterator.next();
				if (alarmLine.alarmData == alarmToBeDiscard) {
					iterator.remove();
					vBox.getChildren().remove(alarmLine.pane);
				}
			}
		}

		Preferences.setAlarms(alarmDatas);

	}

	@SuppressWarnings("java:S4507")
	void setIcon() {
		final URL url = ClassLoader.getSystemResource("asac.png");
		Image image = null;
		try {
			final InputStream inputStream = url.openStream();
			image = new Image(inputStream);
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		if (image != null) {
			stage.getIcons().add(image);
		}
	}

	public static void showConfirmation(final String message, final Consumer<Boolean> callback) {
		Platform.runLater(() -> {
			final Alert alert = new Alert(AlertType.CONFIRMATION, message);
			final Optional<ButtonType> result = alert.showAndWait();
			callback.accept(result.isPresent() && result.get() == ButtonType.OK);
		});
	}

	static class AlarmsLine {
		private final TextField name;
		private final CheckBox isActive;
		private final Button editButton;
		private final Button deleteButton;
		private final HBox pane;
		private AlarmData alarmData;

		@SuppressWarnings("unused")
		AlarmsLine(final MainClass mainClass) {
			alarmData = new AlarmData();
			name = new TextField();
			name.setEditable(false);

			isActive = new CheckBox(MainClass.messages.getString("is.active"));

			editButton = new Button(MainClass.messages.getString("edit"));
			deleteButton = new Button(MainClass.messages.getString("delete"));

			isActive.minHeightProperty().bind(editButton.heightProperty());
			isActive.prefHeightProperty().bind(editButton.heightProperty());
			isActive.maxHeightProperty().bind(editButton.heightProperty());

			pane = new HBox(5, name, isActive, editButton, deleteButton);

			editButton.setOnAction(event -> {
				AlarmManager alarmManager = new AlarmManager(mainClass, alarmData);
				if (alarmManager.addButtonWasFired()) {
					alarmData = alarmManager.getData();
					name.setText(alarmData.name);
					isActive.setSelected(alarmData.isActive());
					mainClass.update();
				}
			});

			deleteButton.setOnAction(event -> {
				mainClass.alarmLines.remove(this);
				mainClass.update();
			});

			isActive.setOnAction(event -> {
				alarmData.setActive(isActive.isSelected());
				mainClass.update();
			});

		}

	}

	static class SleepingTaskController implements ISleepingTaskController {

		@Override
		@SuppressWarnings({ "java:S2142", "java:S4507" })
		public void sleep(int durationSeconds) {
			Asac.sleep(durationSeconds * 1000l);
		}

		@Override
		public LocalDateTime getCurrentDateTime() {
			return LocalDateTime.now(ZoneId.systemDefault());
		}

	}

}
