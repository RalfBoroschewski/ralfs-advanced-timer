package com.ralf.asac;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class TestPreferences {
	List<String> alarmsBeforeTesting;
	int numberAlarmsBeforeTesting;

	@Test
	void test1() {
		numberAlarmsBeforeTesting = Preferences.getNumberAlarmsForTestingOnly();
		alarmsBeforeTesting = Preferences.getAlarmsRawForTestingOnly(numberAlarmsBeforeTesting);

		java.util.Random random = new Random(42);

		ArrayList<AlarmData> alarmsToBeStored = new ArrayList<AlarmData>();

		int numberElements = 100;
		for (int index = 0; index < numberElements; index++) {
			alarmsToBeStored.add(createAlarmDataItem(random));
		}

		Preferences.setAlarms(alarmsToBeStored);

		List<AlarmData> alarmsRead = Preferences.getAlarms();

		assertEquals(alarmsToBeStored.size(), alarmsRead.size(), "Number of elements are not equal");

		for (int index = 0; index < numberElements; index++) {
			AlarmData alarmStored = alarmsToBeStored.get(index);
			AlarmData alarmRead = alarmsRead.get(index);

			assertEquals(alarmStored.isActive(), alarmRead.isActive(), index + ": isActive are not equal");
			assertEquals(alarmStored.alarmType, alarmRead.alarmType, index + ": alarmType are not equal");
			assertEquals(alarmStored.name, alarmRead.name, index + ": name are not equal");
			assertEquals(alarmStored.weekInMonth, alarmRead.weekInMonth, index + ": weekInMonth are not equal");

			assertEquals(alarmStored.selectedDays[0], alarmRead.selectedDays[0],
					index + ": selectedDays[0] are not equal");
			assertEquals(alarmStored.selectedDays[1], alarmRead.selectedDays[1],
					index + ": selectedDays[1] are not equal");
			assertEquals(alarmStored.selectedDays[2], alarmRead.selectedDays[2],
					index + ": selectedDays[2] are not equal");
			assertEquals(alarmStored.selectedDays[3], alarmRead.selectedDays[3],
					index + ": selectedDays[3] are not equal");
			assertEquals(alarmStored.selectedDays[4], alarmRead.selectedDays[4],
					index + ": selectedDays[4] are not equal");
			assertEquals(alarmStored.selectedDays[5], alarmRead.selectedDays[5],
					index + ": selectedDays[5] are not equal");
			assertEquals(alarmStored.selectedDays[6], alarmRead.selectedDays[6],
					index + ": selectedDays[6] are not equal");

			assertEquals(alarmStored.dayOfMonth, alarmRead.dayOfMonth, index + ": dayOfMonth are not equal");
			assertEquals(alarmStored.selectedDate, alarmRead.selectedDate, index + ": selectedDate are not equal");
			assertEquals(alarmStored.time, alarmRead.time, index + ": time are not equal");
		}

	}

	@AfterEach
	void tearDown() {
		Preferences.setNumberAlarmsForTestingOnly(numberAlarmsBeforeTesting);
		Preferences.setAlarmsRawForTestingOnly(alarmsBeforeTesting);
	}

	AlarmData createAlarmDataItem(Random random) {

		final boolean isActive = random.nextInt(2) == 1;
		final AlarmData.AlarmType alarmType = Preferences.int2AlarmType(random.nextInt(3));

		final String[] names = { "aaa", "bbbb", "ccccc" };

		final String name = names[random.nextInt(names.length)];
		final AlarmData.WeekInMonth weekInMonth = Preferences.int2WeekInMonth(random.nextInt(7));

		final boolean[] selectedDays = new boolean[7];

		for (int day = 0; day < 7; day++) {
			selectedDays[day] = random.nextInt(2) == 1;
		}

		final int dayOfMonth = 1 + random.nextInt(28);

		int year = 2026 + random.nextInt(100);
		int month = 1 + random.nextInt(12);
		int day = 1 + random.nextInt(28);
		final LocalDate selectedDate = LocalDate.of(year, month, day);

		int hour = random.nextInt(24);
		int minute = random.nextInt(60);

		LocalTime time = LocalTime.of(hour, minute);

		return new AlarmData(isActive, alarmType, name, weekInMonth, selectedDays, dayOfMonth, selectedDate, time);
	}
}
