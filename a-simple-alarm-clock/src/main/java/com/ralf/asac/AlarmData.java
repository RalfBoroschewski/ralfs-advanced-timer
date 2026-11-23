package com.ralf.asac;

import java.time.LocalDate;
import java.time.LocalTime;

public class AlarmData {

	public enum AlarmType {
		DAYS_OF_WEEKS(0), DAY_OF_MONTH(1), DATE(2);

		private final int value;

		private AlarmType(int value) {
			this.value = value;
		}

		int getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "" + value;
		}
	}

	public enum WeekInMonth {
		EVERY_WEEK(0), FIRST_WEEK(1), SECOND_WEEK(2), THIRD_WEEK(3), FOURTH_WEEK(4), LAST_WEEK(5), WEEK_BEFORE_LAST(6);

		private final int value;

		private WeekInMonth(final int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "" + value;
		}
	}

	private boolean isActive;
	final AlarmType alarmType;
	final String name;
	final WeekInMonth weekInMonth;
	final boolean[] selectedDays;
	final int dayOfMonth;
	final LocalDate selectedDate;
	final LocalTime time;

	@SuppressWarnings("java:S107")
	AlarmData(final boolean isActive, final AlarmType alarmType, final String name, final WeekInMonth weekInMonth,
			final boolean[] selectedDays, final int dayOfMonth, final LocalDate selectedDate, final LocalTime time) {
		this.isActive = isActive;
		this.alarmType = alarmType;
		this.name = name;
		this.weekInMonth = weekInMonth;
		this.selectedDays = selectedDays;
		this.dayOfMonth = dayOfMonth;
		this.selectedDate = selectedDate;
		this.time = time;
	}

	AlarmData() {
		this(true, null, "", WeekInMonth.EVERY_WEEK, new boolean[7], 1, null, null);
	}

	public static AlarmData createForTest(String name) {
		return new AlarmData(true, null, name, null, null, -1, null, null);
	}

	boolean isActive() {
		return isActive;
	}

	void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	String getTimeString() {
		return String.format("%02d:%02d", time.getHour(), time.getMinute());
	}

	@Override
	public String toString() {
		return "isActive: " + isActive + "\nalarmType: " + alarmType + "\nname: \"" + name + "\"\nweekInMonth: "
				+ weekInMonth + "\nselectedDays[0]: " + selectedDays[0] + "\nselectedDays[1]: " + selectedDays[1]
				+ "\nselectedDays[2]: " + selectedDays[2] + "\nselectedDays[3]: " + selectedDays[3]
				+ "\nselectedDays[4]: " + selectedDays[4] + "\nselectedDays[5]: " + selectedDays[5]
				+ "\nselectedDays[6]: " + selectedDays[6] + "\ndayOfMonth: " + dayOfMonth + "\nselectedDate: "
				+ selectedDate + "\ntime: " + time + "\n";
	}

}
