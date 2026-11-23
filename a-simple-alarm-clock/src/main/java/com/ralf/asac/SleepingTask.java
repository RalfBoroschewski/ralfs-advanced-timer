package com.ralf.asac;

import java.time.LocalDateTime;
import java.util.List;

import javafx.concurrent.Task;

class SleepingTask {
	private final List<AlarmData> alarmDatas;
	private final LocalDateTime nextAlertTime;
	private final ISleepingTaskController sleepingTaskController;
	private final IBell bell;
	private final MainClass mainClass;
	private MyWorker myWorker;

	static final int INTERVAL_LENGTH_IN_SECONDS = 15;

	SleepingTask(final List<AlarmData> alarmDatas, final LocalDateTime nextAlertTime,
			final ISleepingTaskController sleepingTaskController, final IBell bell, final MainClass mainClass) {
		this.alarmDatas = alarmDatas;
		this.nextAlertTime = nextAlertTime;
		this.sleepingTaskController = sleepingTaskController;
		this.bell = bell;
		this.mainClass = mainClass;
	}

	void start() {
		myWorker = new MyWorker(alarmDatas, nextAlertTime, sleepingTaskController, bell, mainClass);
		new Thread(myWorker).start();
	}

	void stop() {
		myWorker.isActive = false;
	}

	static class MyWorker extends Task<Integer> {

		boolean isActive;
		private final List<AlarmData> alarmDatas;
		private final LocalDateTime nextAlertTime;
		private final ISleepingTaskController sleepingTaskController;
		private final IBell bell;
		private final MainClass mainClass;

		MyWorker(final List<AlarmData> alarmDatas, final LocalDateTime nextAlertTime,
				final ISleepingTaskController sleepingTaskController, final IBell bell, final MainClass mainClass) {
			this.alarmDatas = alarmDatas;
			this.nextAlertTime = nextAlertTime;
			this.sleepingTaskController = sleepingTaskController;
			this.bell = bell;
			this.mainClass = mainClass;
		}

		@Override
		protected Integer call() throws Exception {
			workerCore();
			return null;
		}

		@SuppressWarnings("java:S2589")
		void workerCore() {
			LocalDateTime now = sleepingTaskController.getCurrentDateTime();
			isActive = true;
			while (now.compareTo(nextAlertTime) < 0 && isActive) {
				final int nowSeconds = now.getSecond();
				final int nextIntervalSeconds = ((nowSeconds + INTERVAL_LENGTH_IN_SECONDS) / INTERVAL_LENGTH_IN_SECONDS)
						* INTERVAL_LENGTH_IN_SECONDS;

				final int durationInSeconds = nextIntervalSeconds - nowSeconds;

				if (durationInSeconds > 0) {
					sleepingTaskController.sleep(durationInSeconds);
				} else {
					break;
				}

				now = sleepingTaskController.getCurrentDateTime();
			}

			int nextSeconds = nextAlertTime.getSecond();
			int nowSeconds = now.getSecond();
			if (nextSeconds - nowSeconds < -10) { // Test whether there is a short overtime after the computer waked up
													// after a sleeping period

				if (mainClass != null) {
					mainClass.update();
				}
				return;
			}

			if (isActive) {
				bell.play(alarmDatas);
				if (mainClass != null) {
					Asac.sleep(1000);
					mainClass.update();
				}

			}
		}
	}

}
