package com.ralf.asac;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class Scheduler {

	private final List<AlarmData> nextAlarms = new ArrayList<>();
	private LocalDateTime minimalDateTime;
	private final LocalDateTime currentDateTime;
	private final List<AlarmData> alarmsToBeDiscard = new ArrayList<AlarmData>();

	Scheduler(final List<AlarmData> alarmDatas, final LocalDateTime currentTime) {
		this.currentDateTime = currentTime;
		for (final AlarmData alarmData : alarmDatas) {
			if (alarmData.isActive() && alarmData.alarmType != null) {
				switch (alarmData.alarmType) {
				case DAYS_OF_WEEKS:
					addDaysOfWeeks(alarmData);
					break;
				case DAY_OF_MONTH:
					nextDayOfMonths(alarmData);
					break;
				case DATE:
					final LocalDateTime dateTime = alarmData.selectedDate.atTime(alarmData.time);
					setMinimalAlarmDate(dateTime, alarmData);
					break;
				default:
				}
			}
		}
	}

	private void addDaysOfWeeks(final AlarmData alarmData) {
		switch (alarmData.weekInMonth) {
		case EVERY_WEEK:
			setMinimalAlarmOfEveryWeekData(alarmData, currentDateTime);
			return;
		case FIRST_WEEK:
			setDayAtBeginningMonth(alarmData, 0);
			break;
		case SECOND_WEEK:
			setDayAtBeginningMonth(alarmData, 1);
			break;
		case THIRD_WEEK:
			setDayAtBeginningMonth(alarmData, 2);
			break;
		case FOURTH_WEEK:
			setDayAtBeginningMonth(alarmData, 3);
			break;
		case LAST_WEEK:
			setDayAtEndingMonth(alarmData, 0);
			break;
		case WEEK_BEFORE_LAST:
			setDayAtEndingMonth(alarmData, 1);
			break;
		default:
		}
	}

	private void nextDayOfMonths(final AlarmData alarmData) {
		final LocalDateTime dayOfMonth = currentDateTime.minusDays(currentDateTime.getDayOfMonth() - 1l)
				.plusDays(alarmData.dayOfMonth - 1l);
		if (!dayOfMonth.isBefore(currentDateTime)) {
			setMinimalAlarmDate(dayOfMonth, alarmData);
		} else {
			setMinimalAlarmDate(dayOfMonth.plusMonths(1), alarmData);
		}
	}

	private void setMinimalAlarmOfEveryWeekData(final AlarmData alarmData, LocalDateTime startOfWeek) {
		int startDayOfWeek = startOfWeek.getDayOfWeek().getValue() - 1;
		for (int day = 0; day < 14; day++) {
			int currentDayOfWeek = (startDayOfWeek + day) % 7;

			if (alarmData.selectedDays[currentDayOfWeek % 7]) {
				setMinimalAlarmDate(startOfWeek, alarmData);
			}
			startOfWeek = startOfWeek.plusDays(1);
		}
	}

	private void setDayAtBeginningMonth(final AlarmData alarmData, final int weekNumber) {
		LocalDateTime startDateTime = currentDateTime.minusDays(currentDateTime.getDayOfMonth() - 1l);
		setDayAtBeginningMonth(alarmData, weekNumber, startDateTime);

		if (minimalDateTime == null) {
			startDateTime = startDateTime.plusMonths(1);
			setDayAtBeginningMonth(alarmData, weekNumber, startDateTime);
		}
	}

	@SuppressWarnings({ "java:S3776", "java:S135" })
	private void setDayAtBeginningMonth(final AlarmData alarmData, final int weekNumber,
			final LocalDateTime startDateTime) {

		for (int day = 0; day < 7; day++) {
			if (alarmData.selectedDays[day]) {
				LocalDateTime dateTime = startDateTime;
				while (true) {
					if (dateTime.getDayOfWeek().getValue() - 1 == day) {
						final LocalDateTime tmp = dateTime.plusWeeks(weekNumber);
						if (tmp.isBefore(currentDateTime)) {
							dateTime = currentDateTime.plusMonths(1).minusDays(currentDateTime.getDayOfMonth());
						} else if (tmp.equals(currentDateTime)) {
							setMinimalAlarmDate(currentDateTime, alarmData);
							break;
						} else {
							dateTime = dateTime.plusWeeks(weekNumber);
							setMinimalAlarmDate(dateTime, alarmData);
							break;
						}
					} else {
						dateTime = dateTime.plusDays(1);
					}
				}
			}
		}
	}

	private void setDayAtEndingMonth(final AlarmData alarmData, final int weekNumber) {
		LocalDateTime startDateTime = currentDateTime.minusDays(currentDateTime.getDayOfMonth() - 1l).plusMonths(1)
				.minusWeeks(weekNumber + 1l);
		setDayAtEndingMonth(alarmData, weekNumber, startDateTime);

		if (minimalDateTime == null) {
			startDateTime = startDateTime.plusMonths(1);
			setDayAtEndingMonth(alarmData, weekNumber, startDateTime);
		}
	}

	@SuppressWarnings({ "java:S3776", "java:S135" })
	private void setDayAtEndingMonth(final AlarmData alarmData, final int weekNumber,
			final LocalDateTime startDateTime) {
		for (int day = 0; day < 7; day++) {
			if (alarmData.selectedDays[day]) {
				LocalDateTime dateTime = startDateTime;

				while (true) {
					if (dateTime.getDayOfWeek().getValue() - 1 == day) {
						if (dateTime.isBefore(currentDateTime)) {
							dateTime = currentDateTime.plusMonths(1).minusWeeks(weekNumber);
						} else if (dateTime.equals(currentDateTime)) {
							setMinimalAlarmDate(currentDateTime, alarmData);
							break;
						} else {
							setMinimalAlarmDate(dateTime, alarmData);
							break;
						}
					} else {
						dateTime = dateTime.plusDays(1);
					}
				}
			}
		}
	}

	private void setMinimalAlarmDate(final LocalDateTime dateTime, final AlarmData alarmData) {

		final LocalDateTime alertDateTime = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(),
				dateTime.getDayOfMonth(), alarmData.time.getHour(), alarmData.time.getMinute());

		if ((minimalDateTime == null || alertDateTime.isBefore(minimalDateTime))
				&& (currentDateTime.isBefore(alertDateTime) || currentDateTime.isEqual(alertDateTime))) {
			minimalDateTime = alertDateTime;
			nextAlarms.clear();
		}

		if (alertDateTime.equals(minimalDateTime)) {
			nextAlarms.add(alarmData);
		}

		if (alertDateTime.isBefore(currentDateTime)) {
			alarmsToBeDiscard.add(alarmData);
		}
	}

	List<AlarmData> getNextAlarms() {
		return nextAlarms;
	}

	List<AlarmData> geteAlarmsToBeDiscard() {
		return alarmsToBeDiscard;
	}

	LocalDateTime getMinimalTime() {
		return minimalDateTime;
	}

}
