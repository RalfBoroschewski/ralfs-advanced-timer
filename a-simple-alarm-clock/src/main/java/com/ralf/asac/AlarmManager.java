package com.ralf.asac;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

class AlarmManager {
	private final CheckBox isActive;
	private final ComboBox<AlarmTypeItem> alarmType;
	private final EnumMap<AlarmData.AlarmType, String> alarmTypeStrings;
	private final TextField name;
	private final TimeTextField timeTextField;
	private final WeekdaysPane weekdaysPane;
	private final DayOfMonthPane dayOfMonthPane;
	private final DatePane datePane;
	private final VBox mainPane;

	final Stage modalStage;
	private boolean addButtonWasFired;

	@SuppressWarnings("unused")
	AlarmManager(final MainClass mainClass, final AlarmData alarmData) {

		name = new TextField();
		mainPane = new VBox();

		alarmType = new ComboBox<>();

		alarmTypeStrings = new EnumMap<>(AlarmData.AlarmType.class);

		alarmTypeStrings.put(AlarmData.AlarmType.DAYS_OF_WEEKS,
				MainClass.messages.getString("AlarmManager.alarmtype.days.of.weeks"));
		alarmTypeStrings.put(AlarmData.AlarmType.DAY_OF_MONTH,
				MainClass.messages.getString("AlarmManager.alarmtype.day.of.month"));
		alarmTypeStrings.put(AlarmData.AlarmType.DATE, MainClass.messages.getString("AlarmManager.alarmtype.date"));

		Map<AlarmData.AlarmType, AlarmTypeItem> alarmTypeItems = new EnumMap<>(AlarmData.AlarmType.class);
		AlarmTypeItem alarmTypeItem;
		AlarmData.AlarmType alarmTypeValue;

		alarmTypeValue = AlarmData.AlarmType.DAYS_OF_WEEKS;
		alarmTypeItem = new AlarmTypeItem(alarmTypeValue, alarmTypeStrings.get(alarmTypeValue));
		alarmType.getItems().add(alarmTypeItem);
		alarmTypeItems.put(alarmTypeValue, alarmTypeItem);

		alarmTypeValue = AlarmData.AlarmType.DAY_OF_MONTH;
		alarmTypeItem = new AlarmTypeItem(alarmTypeValue, alarmTypeStrings.get(alarmTypeValue));
		alarmType.getItems().add(alarmTypeItem);
		alarmTypeItems.put(alarmTypeValue, alarmTypeItem);

		alarmTypeValue = AlarmData.AlarmType.DATE;
		alarmTypeItem = new AlarmTypeItem(alarmTypeValue, alarmTypeStrings.get(alarmTypeValue));
		alarmType.getItems().add(alarmTypeItem);
		alarmTypeItems.put(alarmTypeValue, alarmTypeItem);

		if (alarmData.alarmType == null) {
			alarmType.setValue(new AlarmTypeItem(null, MainClass.messages.getString("AlarmManager.alarmtype.select")));
		} else {
			alarmType.setValue(alarmTypeItems.get(alarmData.alarmType));
		}

		alarmType.setOnAction(event -> showPanels());

		weekdaysPane = new WeekdaysPane();
		dayOfMonthPane = new DayOfMonthPane();
		datePane = new DatePane();
		timeTextField = new TimeTextField();

		isActive = new CheckBox();
		isActive.setSelected(true);

		modalStage = new Stage();
		modalStage.initModality(Modality.WINDOW_MODAL);
		modalStage.initOwner(mainClass.getStage());
		modalStage.setScene(new Scene(mainPane));
		modalStage.setTitle(MainClass.messages.getString("AlarmManager.title"));

		if (alarmData != null) {
			setAlarmData(alarmData);
		}

		showPanels();

		modalStage.sizeToScene();
		modalStage.showAndWait();
	}

	@SuppressWarnings("unused")
	void showPanels() {
		final ObservableList<Node> mainPaneChildren = mainPane.getChildren();
		mainPaneChildren.clear();
		mainPaneChildren.add(alarmType);
		final AlarmTypeItem alarmTypeItem = alarmType.getValue();

		final Label nameTitle = new Label(MainClass.messages.getString("AlarmManager.name"));
		nameTitle.setPadding(new Insets(30, 20, 10, 10));

		final Pane pane;
		if (alarmTypeItem == null) {
			pane = null;
		} else if (alarmTypeItem.alarmType != null) {
			switch (alarmTypeItem.alarmType) {
			case DAYS_OF_WEEKS:
				pane = weekdaysPane.pane;
				break;
			case DAY_OF_MONTH:
				pane = dayOfMonthPane.getPane();
				break;
			case DATE:
				pane = datePane.getPane();
				break;
			default:
				pane = null;
				break;
			}

		} else {
			pane = null;
		}

		Label placeHolder4showingTheTitleOfTheWindow = new Label("\u2003".repeat(105)); // &emsp;
		placeHolder4showingTheTitleOfTheWindow.setFont(Font.font(2.));

		if (pane != null) {
			final Label lableTime = new Label(MainClass.messages.getString("AlarmManager.time"));
			final Label labelIsActive = new Label(MainClass.messages.getString("is.active"));
			VBox.setMargin(lableTime, new Insets(20, 0, 0, 0));
			final VBox vBox = new VBox(5, nameTitle, name, pane, lableTime, timeTextField, labelIsActive, isActive,
					placeHolder4showingTheTitleOfTheWindow);
			final StackPane frame = new StackPane(vBox);
			frame.setStyle("-fx-border-color: gray; -fx-border-radius: 4; " + "-fx-padding: 15 5 5 5;");

			mainPaneChildren.add(frame);
		} else {
			mainPaneChildren.add(placeHolder4showingTheTitleOfTheWindow);
		}

		final HBox buttonsPane = new HBox(5);

		final Button okButton = new Button(MainClass.messages.getString("ok"));
		final Button cancelButton = new Button(MainClass.messages.getString("cancel"));

		if (pane == null) {
			buttonsPane.getChildren().addAll(cancelButton);
		} else {
			buttonsPane.getChildren().addAll(okButton, cancelButton);
		}

		VBox.setMargin(buttonsPane, new Insets(20, 0, 0, 0));
		mainPaneChildren.add(buttonsPane);
		mainPane.setPadding(new Insets(10, 20, 10, 10));
		okButton.setOnAction(event -> {
			if (timeTextField.getText() == null || timeTextField.getText().isEmpty()) {
				Asac.showError(modalStage, MainClass.messages.getString("AlarmManager.time.not.set"));
			} else if (alarmTypeItem.alarmType == AlarmData.AlarmType.DATE
					&& datePane.datePicker.getValue().equals(LocalDate.now(ZoneId.systemDefault()))
					&& timeTextField.getTime().isBefore(LocalTime.now(ZoneId.systemDefault()))) {
				Asac.showError(modalStage, MainClass.messages.getString("AlarmManager.datePane.error"));
			} else {
				addButtonWasFired = true;
				modalStage.close();
			}

		});

		modalStage.sizeToScene();
		cancelButton.setOnAction(event -> modalStage.close());
	}

	boolean addButtonWasFired() {
		return addButtonWasFired;
	}

	AlarmData getData() {
		final boolean[] days = new boolean[7];
		for (int day = 0; day < 7; day++) {
			days[day] = weekdaysPane.selectedDaysOfWeek[day].isSelected();
		}

		final int dayOfMonthValue;
		if (dayOfMonthPane.dayOfMonth.getValue() == null) {
			dayOfMonthValue = -1;
		} else {
			dayOfMonthValue = dayOfMonthPane.dayOfMonth.getValue().day;
		}

		final LocalDate selectedDate = datePane.datePicker.getValue();

		return new AlarmData(isActive.isSelected(), alarmType.getValue().alarmType, name.getText(),
				weekdaysPane.selectedWeek.getValue().week, days, dayOfMonthValue, selectedDate,
				timeTextField.getText() != null ? timeTextField.getTime() : null);
	}

	void setAlarmData(final AlarmData alarmData) {
		name.setText(alarmData.name);

		weekdaysPane.selectedWeek.setValue(
				new SelectedWeekItem(weekdaysPane.selectedWeekItems.get(alarmData.weekInMonth), alarmData.weekInMonth));
		weekdaysPane.selectedDaysOfWeekFired();

		for (int day = 0; day < 7; day++) {
			weekdaysPane.selectedDaysOfWeek[day].setSelected(alarmData.selectedDays[day]);
		}

		if (alarmData.dayOfMonth > 0) {
			final SelectedDayOfMonth selectedDayOfMonth = new SelectedDayOfMonth(
					MainClass.messages.getString("AlarmManager.dayInMonth." + alarmData.dayOfMonth),
					alarmData.dayOfMonth);
			dayOfMonthPane.dayOfMonth.setValue(selectedDayOfMonth);
		}

		datePane.datePicker.setValue(alarmData.selectedDate);

		timeTextField.setTime(alarmData.time);

		isActive.setSelected(alarmData.isActive());
	}

	static class AlarmTypeItem {
		final AlarmData.AlarmType alarmType;
		final String value;

		AlarmTypeItem(final AlarmData.AlarmType displayMode, final String value) {
			this.alarmType = displayMode;
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	static class WeekdaysPane {
		private final HBox pane;
		private final ComboBox<SelectedWeekItem> selectedWeek;
		private final EnumMap<AlarmData.WeekInMonth, String> selectedWeekItems;
		private final CheckBox[] selectedDaysOfWeek;
		private final CheckBox isDaily;

		@SuppressWarnings("unused")
		WeekdaysPane() {
			selectedWeekItems = new EnumMap<>(AlarmData.WeekInMonth.class);

			selectedWeekItems.put(AlarmData.WeekInMonth.EVERY_WEEK,
					MainClass.messages.getString("AlarmManager.SelectedWeekItem.every"));
			selectedWeekItems.put(AlarmData.WeekInMonth.FIRST_WEEK,
					MainClass.messages.getString("AlarmManager.SelectedWeekItem.first"));
			selectedWeekItems.put(AlarmData.WeekInMonth.SECOND_WEEK,
					MainClass.messages.getString("AlarmManager.SelectedWeekItem.second"));
			selectedWeekItems.put(AlarmData.WeekInMonth.THIRD_WEEK,
					MainClass.messages.getString("AlarmManager.SelectedWeekItem.third"));
			selectedWeekItems.put(AlarmData.WeekInMonth.FOURTH_WEEK,
					MainClass.messages.getString("AlarmManager.SelectedWeekItem.fourth"));
			selectedWeekItems.put(AlarmData.WeekInMonth.LAST_WEEK,
					MainClass.messages.getString("AlarmManager.SelectedWeekItem.last"));
			selectedWeekItems.put(AlarmData.WeekInMonth.WEEK_BEFORE_LAST,
					MainClass.messages.getString("AlarmManager.SelectedWeekItem.before.last.week"));

			pane = new HBox(5);
			selectedWeek = new ComboBox<>();

			final ObservableList<SelectedWeekItem> items = selectedWeek.getItems();

			items.add(new SelectedWeekItem(selectedWeekItems.get(AlarmData.WeekInMonth.EVERY_WEEK),
					AlarmData.WeekInMonth.EVERY_WEEK));
			items.add(new SelectedWeekItem(selectedWeekItems.get(AlarmData.WeekInMonth.FIRST_WEEK),
					AlarmData.WeekInMonth.FIRST_WEEK));
			items.add(new SelectedWeekItem(selectedWeekItems.get(AlarmData.WeekInMonth.SECOND_WEEK),
					AlarmData.WeekInMonth.SECOND_WEEK));
			items.add(new SelectedWeekItem(selectedWeekItems.get(AlarmData.WeekInMonth.THIRD_WEEK),
					AlarmData.WeekInMonth.THIRD_WEEK));
			items.add(new SelectedWeekItem(selectedWeekItems.get(AlarmData.WeekInMonth.FOURTH_WEEK),
					AlarmData.WeekInMonth.FOURTH_WEEK));
			items.add(new SelectedWeekItem(selectedWeekItems.get(AlarmData.WeekInMonth.FIRST_WEEK),
					AlarmData.WeekInMonth.FIRST_WEEK));
			items.add(new SelectedWeekItem(selectedWeekItems.get(AlarmData.WeekInMonth.LAST_WEEK),
					AlarmData.WeekInMonth.LAST_WEEK));
			items.add(new SelectedWeekItem(selectedWeekItems.get(AlarmData.WeekInMonth.WEEK_BEFORE_LAST),
					AlarmData.WeekInMonth.WEEK_BEFORE_LAST));

			selectedWeek.setValue(new SelectedWeekItem(selectedWeekItems.get(AlarmData.WeekInMonth.EVERY_WEEK),
					AlarmData.WeekInMonth.EVERY_WEEK));

			selectedDaysOfWeek = new CheckBox[7];

			final String[] daysCaption = new String[7];

			daysCaption[0] = MainClass.messages.getString("AlarmManager.monday");
			daysCaption[1] = MainClass.messages.getString("AlarmManager.tuesday");
			daysCaption[2] = MainClass.messages.getString("AlarmManager.wednesday");
			daysCaption[3] = MainClass.messages.getString("AlarmManager.thursday");
			daysCaption[4] = MainClass.messages.getString("AlarmManager.friday");
			daysCaption[5] = MainClass.messages.getString("AlarmManager.saturday");
			daysCaption[6] = MainClass.messages.getString("AlarmManager.sunday");

			final VBox vBoxSelectedWeek = new VBox(5);
			vBoxSelectedWeek.getChildren().addAll(new Label(""), selectedWeek);

			pane.getChildren().add(vBoxSelectedWeek);
			final VBox vBoxDaily = new VBox(5);
			isDaily = new CheckBox();
			isDaily.setOnAction(event -> {
				for (CheckBox selectedDayOfWeek : selectedDaysOfWeek) {
					selectedDayOfWeek.setSelected(isDaily.isSelected());
				}
			});

			isDaily.minHeightProperty().bind(selectedWeek.heightProperty());
			isDaily.prefHeightProperty().bind(selectedWeek.heightProperty());
			isDaily.maxHeightProperty().bind(selectedWeek.heightProperty());

			vBoxDaily.getChildren().addAll(new Label(MainClass.messages.getString("AlarmManager.daily")), isDaily);

			pane.getChildren().add(vBoxDaily);

			for (int day = 0; day < 7; day++) {
				final VBox vBoxDays = new VBox(5);
				vBoxDays.getChildren().add(new Label(daysCaption[day]));
				selectedDaysOfWeek[day] = new CheckBox();
				vBoxDays.getChildren().add(selectedDaysOfWeek[day]);

				selectedDaysOfWeek[day].minHeightProperty().bind(selectedWeek.heightProperty());
				selectedDaysOfWeek[day].prefHeightProperty().bind(selectedWeek.heightProperty());
				selectedDaysOfWeek[day].maxHeightProperty().bind(selectedWeek.heightProperty());

				pane.getChildren().add(vBoxDays);

				selectedDaysOfWeek[day].setOnAction(event -> selectedDaysOfWeekFired());
			}

		}

		Pane getWeekdaysPane() {
			return pane;
		}

		void selectedDaysOfWeekFired() {
			boolean hasAnyDeselected = false;
			boolean areAllSelected = true;

			for (final CheckBox selectedDayOfWeek : selectedDaysOfWeek) {
				if (selectedDayOfWeek.isSelected()) {
					hasAnyDeselected = true;
				} else {
					areAllSelected = false;
				}
			}

			if (areAllSelected) {
				isDaily.setSelected(true);
			} else

			if (hasAnyDeselected) {
				isDaily.setSelected(false);
			}
		}
	}

	static class DayOfMonthPane {

		final ComboBox<SelectedDayOfMonth> dayOfMonth;
		final HBox hBox;

		DayOfMonthPane() {

			dayOfMonth = new ComboBox<>();
			for (int day = 1; day <= 28; day++) {
				SelectedDayOfMonth selectedDayOfMonth = new SelectedDayOfMonth(
						MainClass.messages.getString("AlarmManager.dayInMonth." + day), day);
				dayOfMonth.getItems().add(selectedDayOfMonth);
			}

			Label every = new Label(MainClass.messages.getString("AlarmManager.every"));
			Label ofAmonth = new Label(MainClass.messages.getString("AlarmManager.of.a.month"));

			every.minHeightProperty().bind(dayOfMonth.heightProperty());
			every.prefHeightProperty().bind(dayOfMonth.heightProperty());
			every.maxHeightProperty().bind(dayOfMonth.heightProperty());

			ofAmonth.minHeightProperty().bind(dayOfMonth.heightProperty());
			ofAmonth.prefHeightProperty().bind(dayOfMonth.heightProperty());
			ofAmonth.maxHeightProperty().bind(dayOfMonth.heightProperty());

			hBox = new HBox();
			hBox.getChildren().addAll(every, dayOfMonth, ofAmonth);

		}

		Pane getPane() {
			return hBox;
		}
	}

	static class SelectedWeekItem {
		final String title;
		final AlarmData.WeekInMonth week;

		SelectedWeekItem(final String title, final AlarmData.WeekInMonth week) {
			this.title = title;
			this.week = week;
		}

		public String toString() {
			return title;
		}
	}

	static class SelectedDayOfMonth {
		final String text;
		final int day;

		SelectedDayOfMonth(final String text, final int day) {
			this.text = text;
			this.day = day;
		}

		public String toString() {
			return text;
		}
	}

	static class DatePane {
		final VBox vBox;
		final DatePicker datePicker;

		@SuppressWarnings("unused")
		DatePane() {
			vBox = new VBox(5);
			final Label datePickerCaption = new Label(MainClass.messages.getString("AlarmManager.date"));
			datePicker = new DatePicker();
			datePicker.setPrefWidth(150);
			datePicker.setMaxWidth(150);

			datePicker.setDayCellFactory(picker -> new DateCell() {
				@Override
				public void updateItem(LocalDate date, boolean empty) {
					super.updateItem(date, empty);
					if (date != null && !empty && date.isBefore(LocalDate.now(ZoneId.systemDefault()))) {
						setDisable(true);
						setStyle("-fx-background-color: #eeeeee; -fx-text-fill: #aaaaaa;");
					}
				}
			});

			vBox.getChildren().addAll(datePickerCaption, datePicker);
		}

		Pane getPane() {
			return vBox;
		}
	}
}
