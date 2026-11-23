package com.ralf.asac;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;

import com.ralf.asac.AlarmData.AlarmType;
import com.ralf.asac.AlarmData.WeekInMonth;

class Preferences {

	private static final char CHAR1 = 0x18C1; // Unified Canadian Aboriginal Syllabics Extended: CANADIAN SYLLABICS SHAY
	private static final char CHAR2 = 0x1981; // Neu-Tai-Lue: NEW TAI LUE LETTER LOW QA
	private static final char CHAR3 = 0x1382; // Ethiopic Supplement: ETHIOPIC SYLLABLE MWEE

	private static final String SEPARATOR = String.valueOf(new char[] { CHAR1, CHAR2, CHAR3 });

	private static final java.util.prefs.Preferences PREFERENCES_ROOT = java.util.prefs.Preferences.userRoot()
			.node("/com/ralf/asac");

	private static final int ALARM_STRING_PARTS_NUMBER = 30;
	private static final String ALARM_HEADER = "alarm";
	private static final String NUMBER_ALARMS = "number alarm";

	private Preferences() {
	}

	@SuppressWarnings("java:S4507")
	private static void flush() {
		try {
			PREFERENCES_ROOT.flush();
		} catch (BackingStoreException exception) {
			exception.printStackTrace();
		}
	}

	static void setAlarms(final List<AlarmData> alarmDatas) {
		final String futureExpansion = "future Expansion";

		int dataIndex = 0;

		for (final AlarmData alarmData : alarmDatas) {
			final String[] alarmStrings = new String[ALARM_STRING_PARTS_NUMBER];

			for (int index = 0; index < ALARM_STRING_PARTS_NUMBER; index++) {
				alarmStrings[index] = futureExpansion;
			}
			alarmStrings[0] = boolean2string(alarmData.isActive());
			alarmStrings[1] = alarmData.alarmType == null ? null : alarmData.alarmType.toString();
			alarmStrings[2] = alarmData.name;
			alarmStrings[3] = alarmData.weekInMonth.toString();
			alarmStrings[4] = boolean2string(alarmData.selectedDays[0]);
			alarmStrings[5] = boolean2string(alarmData.selectedDays[1]);
			alarmStrings[6] = boolean2string(alarmData.selectedDays[2]);
			alarmStrings[7] = boolean2string(alarmData.selectedDays[3]);
			alarmStrings[8] = boolean2string(alarmData.selectedDays[4]);
			alarmStrings[9] = boolean2string(alarmData.selectedDays[5]);
			alarmStrings[10] = boolean2string(alarmData.selectedDays[6]);
			alarmStrings[11] = "" + alarmData.dayOfMonth;

			if (alarmData.selectedDate != null) {
				alarmStrings[12] = "" + alarmData.selectedDate.getYear();
				alarmStrings[13] = "" + alarmData.selectedDate.getMonth().getValue();
				alarmStrings[14] = "" + alarmData.selectedDate.getDayOfMonth();
			} else {
				alarmStrings[12] = "-1";
				alarmStrings[13] = "-1";
				alarmStrings[14] = "-1";
			}
			alarmStrings[15] = alarmData.time != null ? alarmData.getTimeString() : "";

			final StringBuilder alarms = new StringBuilder();

			for (int index = 0; index < alarmStrings.length - 1; index++) {
				alarms.append(alarmStrings[index]);
				alarms.append(SEPARATOR);
			}
			alarms.append(alarmStrings[alarmStrings.length - 1]);
			PREFERENCES_ROOT.put(ALARM_HEADER + dataIndex++, alarms.toString());
		}

		PREFERENCES_ROOT.put(NUMBER_ALARMS, dataIndex + "");

		flush();
	}

	static List<AlarmData> getAlarms() {
		final List<AlarmData> alarms = new ArrayList<>();
		final String numberAlarmsString = PREFERENCES_ROOT.get(NUMBER_ALARMS, "");
		final int numberAlarms = string2int(numberAlarmsString, 0);

		for (int index = 0; index < numberAlarms; index++) {
			final String alarmsString = PREFERENCES_ROOT.get(ALARM_HEADER + index, "");
			final String[] alarmsStrings = alarmsString.split(SEPARATOR);

			boolean isActive = string2boolean(alarmsStrings[0]);
			final AlarmType alarmType = int2AlarmType(string2int(alarmsStrings[1], -1));
			final String name = alarmsStrings[2];
			final WeekInMonth weekInMonth = int2WeekInMonth(string2int(alarmsStrings[3], 0));
			final boolean[] selectedDays = new boolean[7];
			selectedDays[0] = string2boolean(alarmsStrings[4]);
			selectedDays[1] = string2boolean(alarmsStrings[5]);
			selectedDays[2] = string2boolean(alarmsStrings[6]);
			selectedDays[3] = string2boolean(alarmsStrings[7]);
			selectedDays[4] = string2boolean(alarmsStrings[8]);
			selectedDays[5] = string2boolean(alarmsStrings[9]);
			selectedDays[6] = string2boolean(alarmsStrings[10]);

			final int dayOfMonth = string2int(alarmsStrings[11], 1);

			final int year = string2int(alarmsStrings[12], 1980);
			final int month = string2int(alarmsStrings[13], 1);
			final int day = string2int(alarmsStrings[14], 1);

			final LocalDate selectedDate;

			if (year != -1) {
				selectedDate = LocalDate.of(year, month, day);
			} else {
				selectedDate = null;
			}

			final String timeString = alarmsStrings[15];

			final LocalTime time;
			if (timeString.isEmpty()) {
				time = null;
			} else {
				final int hour = Integer.parseInt(timeString.substring(0, 2));
				final int minute = Integer.parseInt(timeString.substring(3));
				time = LocalTime.of(hour, minute);
			}
			alarms.add(new AlarmData(isActive, alarmType, name, weekInMonth, selectedDays, dayOfMonth, selectedDate,
					time));
		}

		return alarms;

	}

	static int getNumberAlarmsForTestingOnly() {
		final String numberAlarmsString = PREFERENCES_ROOT.get(NUMBER_ALARMS, "");
		return string2int(numberAlarmsString, 0);
	}

	static void setNumberAlarmsForTestingOnly(final int numberAlarms) {
		PREFERENCES_ROOT.put(NUMBER_ALARMS, "" + numberAlarms);
	}

	static List<String> getAlarmsRawForTestingOnly(final int numberAlarms) {
		final List<String> result = new ArrayList<>();
		for (int index = 0; index < numberAlarms; index++) {
			final String alarmsString = PREFERENCES_ROOT.get(ALARM_HEADER + index, null);
			result.add(alarmsString);
		}
		return result;
	}

	static void setAlarmsRawForTestingOnly(final List<String> alarms) {

		int index = 0;
		for (final String alarm : alarms) {
			if (alarm != null) {
				PREFERENCES_ROOT.put(ALARM_HEADER + index, alarm);
			}
			index++;
		}
	}

	@SuppressWarnings("java:S7467")
	static int string2int(final String string, final int defaultValue) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException exception) {
			return defaultValue;
		}
	}

	static String boolean2string(final boolean value) {
		return boolean2integer(value) + "";
	}

	static int boolean2integer(final boolean value) {
		return value ? 1 : 0;
	}

	static boolean int2boolean(final int value) {
		return value == 1;
	}

	static boolean string2boolean(final String string) {
		return string.equals("1");
	}

	static AlarmType int2AlarmType(final int value) {
		switch (value) {
		case 0:
			return AlarmType.DAYS_OF_WEEKS;
		case 1:
			return AlarmType.DAY_OF_MONTH;
		case 2:
			return AlarmType.DATE;
		default:
			return null;
		}
	}

	static WeekInMonth int2WeekInMonth(final int value) {
		switch (value) {
		case 0:
			return WeekInMonth.EVERY_WEEK;
		case 1:
			return WeekInMonth.FIRST_WEEK;
		case 2:
			return WeekInMonth.SECOND_WEEK;
		case 3:
			return WeekInMonth.THIRD_WEEK;
		case 4:
			return WeekInMonth.FOURTH_WEEK;
		case 5:
			return WeekInMonth.LAST_WEEK;
		case 6:
			return WeekInMonth.WEEK_BEFORE_LAST;
		default:
			return WeekInMonth.EVERY_WEEK;
		}
	}

}
