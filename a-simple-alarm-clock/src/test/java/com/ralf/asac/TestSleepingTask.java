package com.ralf.asac;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class TestSleepingTask {

	@Test
	void test() {

		List<AlarmData> alarmDatas = new ArrayList<AlarmData>();
		alarmDatas.add(new AlarmData(false, null, "Name 1", null, null, 1, null, null));
		alarmDatas.add(new AlarmData(false, null, "Name 2", null, null, 1, null, null));

		LocalDateTime startDateTime = LocalDateTime.of(2026, Month.JUNE, 5, 12, 29);
		LocalDateTime nextAlertTime = LocalDateTime.of(2026, Month.JUNE, 6, 12, 30);

		TestSleepingTaskController controller1 = new TestSleepingTaskController(startDateTime, nextAlertTime, false, 0);
		TestBell testBell1 = new TestBell();
		SleepingTask.MyWorker myWorker1 = new SleepingTask.MyWorker(alarmDatas, nextAlertTime, controller1, testBell1,
				null);
		myWorker1.workerCore();
		assertEquals(nextAlertTime.minusSeconds(0), controller1.currentDateTime);
		assertTrue(testBell1.playWasInvoked);

		TestBell testBell2 = new TestBell();
		TestSleepingTaskController controller2 = new TestSleepingTaskController(startDateTime, nextAlertTime, true, 0);
		SleepingTask.MyWorker myWorker2 = new SleepingTask.MyWorker(alarmDatas, nextAlertTime, controller2, testBell2,
				null);
		controller2.myWorker = myWorker2;
		myWorker2.workerCore();
		assertFalse(testBell2.playWasInvoked);

		startDateTime = LocalDateTime.of(2026, Month.JUNE, 5, 12, 39);
		nextAlertTime = LocalDateTime.of(2026, Month.JUNE, 6, 12, 30);

		TestBell testBell3 = new TestBell();
		TestSleepingTaskController controller3 = new TestSleepingTaskController(startDateTime, nextAlertTime, false,
				10);
		SleepingTask.MyWorker myWorker3 = new SleepingTask.MyWorker(alarmDatas, nextAlertTime, controller3, testBell3,
				null);
		controller3.myWorker = myWorker3;
		myWorker3.workerCore();
		assertTrue(testBell3.playWasInvoked);

		TestBell testBell4 = new TestBell();
		TestSleepingTaskController controller4 = new TestSleepingTaskController(startDateTime, nextAlertTime, false,
				11);
		SleepingTask.MyWorker myWorker4 = new SleepingTask.MyWorker(alarmDatas, nextAlertTime, controller4, testBell4,
				null);
		controller4.myWorker = myWorker4;
		myWorker4.workerCore();
		assertTrue(testBell4.playWasInvoked);

	}

}

class TestBell implements IBell {
	boolean playWasInvoked;

	@Override
	public void play(final List<AlarmData> alarmDatas) {
		playWasInvoked = true;
	}

}

class TestSleepingTaskController implements ISleepingTaskController {

	int sleepCounter;
	final LocalDateTime startDateTime;
	LocalDateTime currentDateTime;
	final LocalDateTime nextAlarmDateTime;
	final boolean hasAborted;
	SleepingTask.MyWorker myWorker;
	final int additionalSecondsCurrentDate;

	TestSleepingTaskController(final LocalDateTime startDateTime, final LocalDateTime nextAlarmDateTime,
			final boolean hasAborted, final int additionalSecondsCurrentDate) {
		this.startDateTime = startDateTime;
		this.nextAlarmDateTime = nextAlarmDateTime;
		this.hasAborted = hasAborted;
		this.additionalSecondsCurrentDate = additionalSecondsCurrentDate;
		currentDateTime = startDateTime;
	}

	@Override
	public void sleep(int durationSeconds) {
		sleepCounter += 1;
	}

	@Override
	public LocalDateTime getCurrentDateTime() {
		if (sleepCounter == 10) {
			currentDateTime = currentDateTime.plusSeconds(100 * SleepingTask.INTERVAL_LENGTH_IN_SECONDS);
		} else {
			currentDateTime = currentDateTime.plusSeconds(10);
		}

		if (hasAborted && sleepCounter == 20) {
			myWorker.isActive = false;
		}

		assertFalse(currentDateTime.isAfter(nextAlarmDateTime), currentDateTime + " " + nextAlarmDateTime);
		return currentDateTime.plusSeconds(additionalSecondsCurrentDate);
	}

}