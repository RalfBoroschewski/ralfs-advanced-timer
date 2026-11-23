package com.ralf.asac.test;

import java.util.ArrayList;
import java.util.List;

import com.ralf.asac.AlarmData;
import com.ralf.asac.Bell;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

class TestBellMain extends Application {

	static void start(final String[] args) {
		launch(args);
	}

	@SuppressWarnings("unused")
	@Override
	public void start(Stage stage) throws Exception {
		CheckBox checkBox = new CheckBox("Has more lines");

		Button button = new Button("Test Bell");
		VBox root = new VBox(10, checkBox, button);
		root.setStyle("-fx-padding: 20;");

		stage.setScene(new Scene(root, 300, 120));
		stage.setTitle("Test Bell");
		stage.show();

		button.setOnAction(event -> {
			List<AlarmData> alarmDatas = new ArrayList<AlarmData>();
			alarmDatas.add(AlarmData.createForTest("First alarm 1"));
			alarmDatas.add(AlarmData.createForTest("First alarm 2"));
			alarmDatas.add(
					AlarmData.createForTest("First alarm 3 with long name 1234567890123456789012345678901234567890"));
			alarmDatas.add(AlarmData.createForTest("First alarm 4"));
			if (checkBox.isSelected()) {
				alarmDatas.add(AlarmData.createForTest("First alarm 5"));
				alarmDatas.add(AlarmData.createForTest("First alarm 6"));
			}

			Bell bell = new Bell();
			bell.play(alarmDatas);
		});

	}
}
