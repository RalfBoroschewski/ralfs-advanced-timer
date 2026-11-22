package com.ralf.asac;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class TestScheduler {

	@Test
	void testDate() {
		System.out.println("======================== testDate =================");
		List<AlarmData> alarmDatas = getTestDatasFourDates();
		LocalDateTime currentDateTime;
		LocalDateTime expectedDateTime;
		Scheduler scheduler;
		List<AlarmData> nextAlarms;

		currentDateTime = LocalDateTime.of(2026, 6, 1, 12, 0);
		scheduler = new Scheduler(alarmDatas, currentDateTime);
		nextAlarms = scheduler.getNextAlarms();
		assertEquals(1, nextAlarms.size());
		assertEquals(alarmDatas.get(1), nextAlarms.get(0));
		expectedDateTime = LocalDateTime.of(2026, 6, 14, 12, 0);
		assertEquals(expectedDateTime, scheduler.getMinimalTime());

		currentDateTime = LocalDateTime.of(2026, 6, 21, 12, 0);
		scheduler = new Scheduler(alarmDatas, currentDateTime);
		nextAlarms = scheduler.getNextAlarms();
		assertEquals(2, nextAlarms.size());
		assertEquals(alarmDatas.get(2), nextAlarms.get(0));
		assertEquals(alarmDatas.get(3), nextAlarms.get(1));
		expectedDateTime = LocalDateTime.of(2026, 6, 25, 12, 0);
		assertEquals(expectedDateTime, scheduler.getMinimalTime());

		currentDateTime = LocalDateTime.of(2026, 6, 26, 12, 0);
		scheduler = new Scheduler(alarmDatas, currentDateTime);
		nextAlarms = scheduler.getNextAlarms();
		assertEquals(1, nextAlarms.size());
		assertEquals(alarmDatas.get(4), nextAlarms.get(0));
		expectedDateTime = LocalDateTime.of(2026, 6, 26, 12, 0);
		assertEquals(expectedDateTime, scheduler.getMinimalTime());
	}

	List<AlarmData> getTestDatasFourDates() {
		final List<AlarmData> alarmDatas = new ArrayList<AlarmData>();
		final boolean[] selectedDays = new boolean[7];

		LocalDate selectedDate;
		LocalTime time;

		selectedDate = LocalDate.of(2026, 6, 20);
		time = LocalTime.of(12, 00);
		alarmDatas.add(
				new AlarmData(true, AlarmData.AlarmType.DATE, "Alarm 1", null, selectedDays, -1, selectedDate, time));

		selectedDate = LocalDate.of(2026, 6, 14);
		time = LocalTime.of(12, 00);
		alarmDatas.add(
				new AlarmData(true, AlarmData.AlarmType.DATE, "Alarm 2", null, selectedDays, -1, selectedDate, time));

		selectedDate = LocalDate.of(2026, 6, 25);
		time = LocalTime.of(12, 00);
		alarmDatas.add(
				new AlarmData(true, AlarmData.AlarmType.DATE, "Alarm 3", null, selectedDays, -1, selectedDate, time));

		selectedDate = LocalDate.of(2026, 6, 25);
		time = LocalTime.of(12, 00);
		alarmDatas.add(
				new AlarmData(true, AlarmData.AlarmType.DATE, "Alarm 4", null, selectedDays, -1, selectedDate, time));

		selectedDate = LocalDate.of(2026, 6, 26);
		time = LocalTime.of(12, 00);
		alarmDatas.add(
				new AlarmData(true, AlarmData.AlarmType.DATE, "Alarm 5", null, selectedDays, -1, selectedDate, time));

		return alarmDatas;
	}

	@Test
	void testEveryWeek1() {
		System.out.println("======================== testEveryWeek1 =================");
		List<AlarmData> alarmDatas = getTestDatasFourEveryWeek();
		LocalDateTime currentDateTime;
		LocalDateTime expectedDateTime;
		Scheduler scheduler;
		List<AlarmData> nextAlarms;

		currentDateTime = LocalDateTime.of(2026, 6, 4, 12, 0);
		scheduler = new Scheduler(alarmDatas, currentDateTime);
		nextAlarms = scheduler.getNextAlarms();
		assertEquals(1, nextAlarms.size());
		assertEquals(alarmDatas.get(1), nextAlarms.get(0));
		expectedDateTime = LocalDateTime.of(2026, 6, 5, 12, 0);
		assertEquals(expectedDateTime, scheduler.getMinimalTime());

		currentDateTime = LocalDateTime.of(2026, 6, 6, 12, 0);
		scheduler = new Scheduler(alarmDatas, currentDateTime);
		nextAlarms = scheduler.getNextAlarms();
		assertEquals(1, nextAlarms.size());
		assertEquals(alarmDatas.get(0), nextAlarms.get(0));
		expectedDateTime = LocalDateTime.of(2026, 6, 10, 12, 0);
		assertEquals(expectedDateTime, scheduler.getMinimalTime());

		currentDateTime = LocalDateTime.of(2026, 6, 3, 12, 0);
		scheduler = new Scheduler(alarmDatas, currentDateTime);
		nextAlarms = scheduler.getNextAlarms();
		assertEquals(1, nextAlarms.size());
		assertEquals(alarmDatas.get(0), nextAlarms.get(0));
		expectedDateTime = LocalDateTime.of(2026, 6, 3, 12, 0);
		assertEquals(expectedDateTime, scheduler.getMinimalTime());
	}

	@Test
	void testEveryWeek2() {
		System.out.println("======================== testEveryWeek2 =================");
		LocalDateTime currentDateTime;
		LocalDateTime expectedDateTime;
		Scheduler scheduler;
		List<AlarmData> nextAlarms;
		LocalTime alarmTime;
		List<AlarmData> alarmDatas;
		boolean[] selectedDays;

		alarmTime = LocalTime.of(11, 59);
		selectedDays = new boolean[] { false, false, true, false, false, false, false };
		alarmDatas = new ArrayList<AlarmData>();
		alarmDatas.add(new AlarmData(true, AlarmData.AlarmType.DAYS_OF_WEEKS, "Alarm 6",
				AlarmData.WeekInMonth.EVERY_WEEK, selectedDays, -1, null, alarmTime));

		currentDateTime = LocalDateTime.of(2026, 6, 3, 11, 58);
		scheduler = new Scheduler(alarmDatas, currentDateTime);
		nextAlarms = scheduler.getNextAlarms();
		assertEquals(1, nextAlarms.size());
		assertEquals(alarmDatas.get(0), nextAlarms.get(0));
		expectedDateTime = LocalDateTime.of(2026, 6, 3, 11, 59);
		assertEquals(expectedDateTime, scheduler.getMinimalTime());

		alarmTime = LocalTime.of(11, 58);
		alarmDatas = new ArrayList<AlarmData>();
		alarmDatas.add(new AlarmData(true, AlarmData.AlarmType.DAYS_OF_WEEKS, "Alarm 6",
				AlarmData.WeekInMonth.EVERY_WEEK, selectedDays, -1, null, alarmTime));

		currentDateTime = LocalDateTime.of(2026, 6, 3, 11, 59);
		scheduler = new Scheduler(alarmDatas, currentDateTime);
		nextAlarms = scheduler.getNextAlarms();
		assertEquals(1, nextAlarms.size());
		assertEquals(alarmDatas.get(0), nextAlarms.get(0));
		expectedDateTime = LocalDateTime.of(2026, 6, 10, 11, 58);
		assertEquals(expectedDateTime, scheduler.getMinimalTime());

	}

	List<AlarmData> getTestDatasFourEveryWeek() {
		List<AlarmData> alarmDatas = new ArrayList<AlarmData>();
		boolean[] selectedDays;
		LocalTime time;

		time = LocalTime.of(12, 00);
		selectedDays = new boolean[] { false, false, true, false, false, false, false };
		alarmDatas.add(new AlarmData(true, AlarmData.AlarmType.DAYS_OF_WEEKS, "Alarm 10",
				AlarmData.WeekInMonth.EVERY_WEEK, selectedDays, -1, null, time));

		time = LocalTime.of(12, 00);
		selectedDays = new boolean[] { false, false, false, false, true, false, false };
		alarmDatas.add(new AlarmData(true, AlarmData.AlarmType.DAYS_OF_WEEKS, "Alarm 11",
				AlarmData.WeekInMonth.EVERY_WEEK, selectedDays, -1, null, time));

		return alarmDatas;
	}

	@Test
	void testBeginingWeek1() {
		System.out.println("======================== testEveryWeek1 =================");
		boolean[] selectedDays;
		LocalDateTime currentDateTime;

		currentDateTime = LocalDateTime.of(2026, 5, 31, 12, 0);
		selectedDays = new boolean[] { false, false, true, false, false, false, false };

		performTest(currentDateTime, AlarmData.WeekInMonth.FIRST_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 3, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.SECOND_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 10, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.THIRD_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 17, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.FOURTH_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 24, 12, 0));
	}

	@Test
	void testBeginingWeek2() {
		System.out.println("======================== testBeginingWeek2 =================");
		boolean[] selectedDays;
		LocalDateTime currentDateTime;

		currentDateTime = LocalDateTime.of(2026, 6, 1, 12, 0);
		selectedDays = new boolean[] { false, false, true, false, false, false, false };

		performTest(currentDateTime, AlarmData.WeekInMonth.FIRST_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 3, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.SECOND_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 10, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.THIRD_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 17, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.FOURTH_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 24, 12, 0));
	}

	@Test
	void testBeginingWeek3() {
		System.out.println("======================== testBeginingWeek3 =================");
		boolean[] selectedDays;
		LocalDateTime currentDateTime;

		currentDateTime = LocalDateTime.of(2026, 6, 3, 12, 0);
		selectedDays = new boolean[] { false, false, true, false, false, false, false };

		performTest(currentDateTime, AlarmData.WeekInMonth.FIRST_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 3, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.SECOND_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 10, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.THIRD_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 17, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.FOURTH_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 24, 12, 0));
	}

	@Test
	void testBeginingWeek4() {
		System.out.println("======================== testBeginingWeek4 =================");
		boolean[] selectedDays;
		LocalDateTime currentDateTime;

		currentDateTime = LocalDateTime.of(2026, 6, 4, 12, 0);
		selectedDays = new boolean[] { false, false, true, false, false, false, false };

		performTest(currentDateTime, AlarmData.WeekInMonth.FIRST_WEEK, selectedDays,
				LocalDateTime.of(2026, 7, 1, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.SECOND_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 10, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.THIRD_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 17, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.FOURTH_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 24, 12, 0));
	}

	@Test
	void testBeginingWeek5() {
		System.out.println("======================== testBeginingWeek5 =================");
		boolean[] selectedDays;
		LocalDateTime currentDateTime;

		currentDateTime = LocalDateTime.of(2026, 6, 27, 12, 0);
		selectedDays = new boolean[] { false, false, true, false, false, false, false };

		performTest(currentDateTime, AlarmData.WeekInMonth.FIRST_WEEK, selectedDays,
				LocalDateTime.of(2026, 7, 1, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.SECOND_WEEK, selectedDays,
				LocalDateTime.of(2026, 7, 8, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.THIRD_WEEK, selectedDays,
				LocalDateTime.of(2026, 7, 15, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.FOURTH_WEEK, selectedDays,
				LocalDateTime.of(2026, 7, 22, 12, 0));
	}

	@Test
	void testBeginingWeek6() {
		System.out.println("======================== testBeginingWeek6 =================");
		boolean[] selectedDays;
		LocalDateTime currentDateTime;

		currentDateTime = LocalDateTime.of(2026, 6, 10, 12, 0);
		selectedDays = new boolean[] { false, false, true, false, false, false, false };

		performTest(currentDateTime, AlarmData.WeekInMonth.FIRST_WEEK, selectedDays,
				LocalDateTime.of(2026, 7, 1, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.SECOND_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 10, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.THIRD_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 17, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.FOURTH_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 24, 12, 0));
	}

	@Test
	void testBeginingWeek7() {
		System.out.println("========================testBeginingWeek7 =================");
		boolean[] selectedDays;
		LocalDateTime currentDateTime;

		currentDateTime = LocalDateTime.of(2026, 6, 10, 12, 1);
		selectedDays = new boolean[] { false, false, true, false, false, false, false };
		//

		performTest(currentDateTime, AlarmData.WeekInMonth.FIRST_WEEK, selectedDays,
				LocalDateTime.of(2026, 7, 1, 12, 0));

		currentDateTime = LocalDateTime.of(2026, 6, 10, 11, 59);
		performTest(currentDateTime, AlarmData.WeekInMonth.SECOND_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 10, 12, 0));

		currentDateTime = LocalDateTime.of(2026, 6, 10, 12, 00);
		performTest(currentDateTime, AlarmData.WeekInMonth.SECOND_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 10, 12, 0));

		currentDateTime = LocalDateTime.of(2026, 6, 10, 12, 01);
		performTest(currentDateTime, AlarmData.WeekInMonth.SECOND_WEEK, selectedDays,
				LocalDateTime.of(2026, 7, 8, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.THIRD_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 17, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.FOURTH_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 24, 12, 0));
	}

	@Test
	void testEndingWeek1() {
		System.out.println("======================== testEndingWeek1 =================");
		boolean[] selectedDays;
		LocalDateTime currentDateTime;

		currentDateTime = LocalDateTime.of(2026, 6, 1, 12, 0);
		selectedDays = new boolean[] { false, false, true, false, false, false, false };

		performTest(currentDateTime, AlarmData.WeekInMonth.LAST_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 24, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.WEEK_BEFORE_LAST, selectedDays,
				LocalDateTime.of(2026, 6, 17, 12, 0));

	}

	@Test
	void testEndingWeek2() {
		System.out.println("======================== testEndingWeek2 =================");
		boolean[] selectedDays;
		LocalDateTime currentDateTime;

		currentDateTime = LocalDateTime.of(2026, 6, 24, 12, 0);
		selectedDays = new boolean[] { false, false, true, false, false, false, false };

		performTest(currentDateTime, AlarmData.WeekInMonth.LAST_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 24, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.WEEK_BEFORE_LAST, selectedDays,
				LocalDateTime.of(2026, 7, 22, 12, 0));

	}

	@Test
	void testEndingWeek3() {
		System.out.println("======================== testEndingWeek3 =================");
		boolean[] selectedDays;
		LocalDateTime currentDateTime;

		currentDateTime = LocalDateTime.of(2026, 6, 17, 12, 0);
		selectedDays = new boolean[] { false, false, true, false, false, false, false };

		performTest(currentDateTime, AlarmData.WeekInMonth.LAST_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 24, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.WEEK_BEFORE_LAST, selectedDays,
				LocalDateTime.of(2026, 6, 17, 12, 0));

	}

	@Test
	void testEndingWeek4() {
		System.out.println("======================== testEndingWeek4 =================");
		boolean[] selectedDays;
		LocalDateTime currentDateTime;

		currentDateTime = LocalDateTime.of(2026, 6, 28, 12, 0);
		selectedDays = new boolean[] { false, false, true, false, false, false, false };

		performTest(currentDateTime, AlarmData.WeekInMonth.LAST_WEEK, selectedDays,
				LocalDateTime.of(2026, 7, 29, 12, 0));

		performTest(currentDateTime, AlarmData.WeekInMonth.WEEK_BEFORE_LAST, selectedDays,
				LocalDateTime.of(2026, 7, 22, 12, 0));

	}

	@Test
	void testEndingWeek5() {
		System.out.println("======================== testEndingWeek5 =================");
		boolean[] selectedDays;
		LocalDateTime currentDateTime;

		selectedDays = new boolean[] { false, false, true, false, false, false, false };

		currentDateTime = LocalDateTime.of(2026, 6, 24, 12, 0);
		performTest(currentDateTime, AlarmData.WeekInMonth.LAST_WEEK, selectedDays,
				LocalDateTime.of(2026, 6, 24, 12, 0));

		currentDateTime = LocalDateTime.of(2026, 6, 24, 12, 1);
		performTest(currentDateTime, AlarmData.WeekInMonth.LAST_WEEK, selectedDays,
				LocalDateTime.of(2026, 7, 29, 12, 0));

		currentDateTime = LocalDateTime.of(2026, 6, 17, 12, 0);
		performTest(currentDateTime, AlarmData.WeekInMonth.WEEK_BEFORE_LAST, selectedDays,
				LocalDateTime.of(2026, 6, 17, 12, 0));

		currentDateTime = LocalDateTime.of(2026, 6, 17, 12, 1);
		performTest(currentDateTime, AlarmData.WeekInMonth.WEEK_BEFORE_LAST, selectedDays,
				LocalDateTime.of(2026, 7, 22, 12, 0));

	}

	@Test
	void testDayOfMonth() {
		System.out.println("======================== testDayOfMonth =================");
		LocalDateTime currentDateTime;
		List<AlarmData> alarmDatas;
		currentDateTime = LocalDateTime.of(2026, 6, 7, 12, 0);

		alarmDatas = new ArrayList<AlarmData>();
		LocalTime time = LocalTime.of(12, 00);
		alarmDatas.add(new AlarmData(true, AlarmData.AlarmType.DAY_OF_MONTH, "Alarm 30", null, null, 6, null, time));
		performTest(currentDateTime, alarmDatas, LocalDateTime.of(2026, 7, 6, 12, 0));

		alarmDatas = new ArrayList<AlarmData>();
		alarmDatas.add(new AlarmData(true, AlarmData.AlarmType.DAY_OF_MONTH, "Alarm 31", null, null, 7, null, time));
		performTest(currentDateTime, alarmDatas, LocalDateTime.of(2026, 6, 7, 12, 0));

		alarmDatas = new ArrayList<AlarmData>();
		alarmDatas.add(new AlarmData(true, AlarmData.AlarmType.DAY_OF_MONTH, "Alarm 32", null, null, 8, null, time));
		performTest(currentDateTime, alarmDatas, LocalDateTime.of(2026, 6, 8, 12, 0));
	}

	List<AlarmData> getTestDatasForBeginningWeeks(boolean[] selectedDays, AlarmData.WeekInMonth weekInMonth) {
		List<AlarmData> alarmDatas = new ArrayList<AlarmData>();
		LocalTime time;

		time = LocalTime.of(12, 00);

		alarmDatas.add(new AlarmData(true, AlarmData.AlarmType.DAYS_OF_WEEKS, "Alarm 20", weekInMonth, selectedDays, -1,
				null, time));

		return alarmDatas;
	}

	void performTest(LocalDateTime currentDateTime, List<AlarmData> alarmDatas, LocalDateTime expectedDateTime) {

		Scheduler scheduler = new Scheduler(alarmDatas, currentDateTime);
		List<AlarmData> nextAlarms = scheduler.getNextAlarms();
		assertEquals(1, nextAlarms.size());
		assertEquals(alarmDatas.get(0), nextAlarms.get(0));
		assertEquals(expectedDateTime, scheduler.getMinimalTime());
	}

	void performTest(LocalDateTime currentDateTime, AlarmData.WeekInMonth weekInMonth, boolean[] selectedDays,
			LocalDateTime expectedDateTime) {
		List<AlarmData> alarmDatas = getTestDatasForBeginningWeeks(selectedDays, weekInMonth);
		performTest(currentDateTime, alarmDatas, expectedDateTime);
	}

	List<AlarmData> getTestDatasForDayOnMonth() {

		List<AlarmData> alarmDatas = new ArrayList<AlarmData>();
		boolean[] selectedDays;
		LocalTime time;

		time = LocalTime.of(12, 00);

		selectedDays = new boolean[7];
		alarmDatas.add(
				new AlarmData(true, AlarmData.AlarmType.DAY_OF_MONTH, "Alarm 30", null, selectedDays, 10, null, time));

		return alarmDatas;
	}

}
