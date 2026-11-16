package com.ralf.rat;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;

class Preferences {

	private static final char CHAR1 = 0x18C1; // Unified Canadian Aboriginal Syllabics Extended: CANADIAN SYLLABICS SHAY
	private static final char CHAR2 = 0x1981; // Neu-Tai-Lue: NEW TAI LUE LETTER LOW QA
	private static final char CHAR3 = 0x1382; // Ethiopic Supplement: ETHIOPIC SYLLABLE MWEE

	private static final String SEPARATOR = String.valueOf(new char[] { CHAR1, CHAR2, CHAR3 });

	private static final java.util.prefs.Preferences PREFERENCES_ROOT = java.util.prefs.Preferences.userRoot()
			.node("/com/ralf/rat");

	private static final String ALARM_HEADER = "alarms";
	private static final String NUMBER_ALARMS = "number alarms";
	private static final String SOUND_HEADER = "sounds";
	private static final String NUMBER_SOUNDS = "number sounds";
	private static final String DEFAULT_SOUND_NAME = "default sound name";
	private static final String DEFAULT_SOUND_PATH = "default sound path";
	private static final String DEFAULT_SOUND_IS_RESOURCE = "default sound is resource";
	private static final String SYSTRAY_MODE = "systray mode";

	private static final int ALARM_STRING_PARTS_NUMBER = 20;
	private static final int ALARM_SOUND_STRING_PARTS_NUMBER = 10;

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

	static void setAlarms(final List<AlarmManager.AlarmManagerItem> items) {

		PREFERENCES_ROOT.put(NUMBER_ALARMS, "" + items.size());

		int alarmIndex = 0;
		for (final AlarmManager.AlarmManagerItem item : items) {
			final StringBuilder alarm = new StringBuilder();
			final String[] alarmStrings = new String[ALARM_STRING_PARTS_NUMBER];

			for (int index = 0; index < alarmStrings.length; index++) {
				alarmStrings[index] = "Future Expansion";
			}
			alarmStrings[0] = item.getName();
			alarmStrings[1] = item.getTime();

			final AlarmSounds.AlarmSoundData alarmSoundData = item.getAlarmSoundData();

			if (alarmSoundData != null) {
				alarmStrings[2] = alarmSoundData.getName();
				alarmStrings[3] = alarmSoundData.getPath();
				alarmStrings[4] = alarmSoundData.isResource() ? "1" : "0";
			} else {
				alarmStrings[2] = "";
				alarmStrings[3] = "";
				alarmStrings[4] = "";
			}

			for (int index = 0; index < alarmStrings.length - 1; index++) {
				alarm.append(alarmStrings[index]);
				alarm.append(SEPARATOR);
			}
			alarm.append(alarmStrings[alarmStrings.length - 1]);

			PREFERENCES_ROOT.put(ALARM_HEADER + alarmIndex++, alarm.toString());
		}

		flush();

	}

	static ArrayList<AlarmManager.AlarmManagerItem> getAlarms() {

		final int numberAlarms = Integer.parseInt(PREFERENCES_ROOT.get(NUMBER_ALARMS, "0"));
		final ArrayList<AlarmManager.AlarmManagerItem> items = new ArrayList<>();

		for (int alarmIndex = 0; alarmIndex < numberAlarms; alarmIndex++) {
			final String alarmsString = PREFERENCES_ROOT.get(ALARM_HEADER + alarmIndex, "");
			final String[] alarmStrings = alarmsString.split(SEPARATOR);

			final boolean isResource = alarmStrings[4].equals("1");

			AlarmSounds.AlarmSoundData alarmSoundData;
			if (isResource) {
				alarmSoundData = new AlarmSounds.AlarmSoundData(alarmStrings[2]);
			} else {
				alarmSoundData = new AlarmSounds.AlarmSoundData(alarmStrings[2], alarmStrings[3]);
			}

			AlarmManager.AlarmManagerItem item = new AlarmManager.AlarmManagerItem(alarmStrings[0], alarmStrings[1],
					alarmSoundData);
			items.add(item);
		}

		return items;

	}

	static void setSounds(final List<SoundManager.SoundManagerItem> items) {

		PREFERENCES_ROOT.put(NUMBER_SOUNDS, "" + items.size());

		int indexItem = 0;
		for (final SoundManager.SoundManagerItem item : items) {
			final StringBuilder alarms = new StringBuilder();
			final String[] alarmStrings = new String[ALARM_SOUND_STRING_PARTS_NUMBER];

			for (int index = 0; index < alarmStrings.length; index++) {
				alarmStrings[index] = "Future Expansion";
			}
			alarmStrings[0] = item.getName();
			alarmStrings[1] = item.getPath();

			for (int index = 0; index < alarmStrings.length - 1; index++) {
				alarms.append(alarmStrings[index]);
				alarms.append(SEPARATOR);
			}
			alarms.append(alarmStrings[alarmStrings.length - 1]);

			PREFERENCES_ROOT.put(SOUND_HEADER + indexItem++, alarms.toString());
		}

		flush();
	}

	static ArrayList<SoundManager.SoundManagerItem> getSounds() {
		final ArrayList<SoundManager.SoundManagerItem> items = new ArrayList<>();

		final int numberAlarms = Integer.parseInt(PREFERENCES_ROOT.get(NUMBER_SOUNDS, "0"));

		for (int soundIndex = 0; soundIndex < numberAlarms; soundIndex++) {
			final String soundString = PREFERENCES_ROOT.get(SOUND_HEADER + soundIndex, "");
			final String[] alarmStrings = soundString.split(SEPARATOR);
			final SoundManager.SoundManagerItem item = new SoundManager.SoundManagerItem(alarmStrings[0],
					alarmStrings[1]);
			items.add(item);
		}

		return items;

	}

	static void setDefaultSound(final AlarmSounds.AlarmSoundData defaultSound) {
		PREFERENCES_ROOT.put(DEFAULT_SOUND_NAME, defaultSound.getName());
		PREFERENCES_ROOT.put(DEFAULT_SOUND_PATH, defaultSound.getPath());
		PREFERENCES_ROOT.put(DEFAULT_SOUND_IS_RESOURCE, "" + defaultSound.isResource());

		flush();
	}

	static AlarmSounds.AlarmSoundData getDefaultSound() {
		final String name = PREFERENCES_ROOT.get(DEFAULT_SOUND_NAME, null);
		final String path = PREFERENCES_ROOT.get(DEFAULT_SOUND_PATH, null);
		final String isResourceString = PREFERENCES_ROOT.get(DEFAULT_SOUND_NAME, "");

		final boolean isResource = Boolean.parseBoolean(isResourceString);

		final AlarmSounds.AlarmSoundData alarmSoundData;
		if (isResource) {
			alarmSoundData = new AlarmSounds.AlarmSoundData(name);
		} else {
			alarmSoundData = new AlarmSounds.AlarmSoundData(name, path);
		}

		return alarmSoundData;
	}

	enum SystrayMode {
		NOT_IN_SYSTRAY, MINIMIZE_TO_SYSTRAY, ONLY_IN_SYSTRAY
	}

	static void setSystrayMode(final SystrayMode systrayMode) {
		switch (systrayMode) {
		case MINIMIZE_TO_SYSTRAY:
			PREFERENCES_ROOT.put(SYSTRAY_MODE, "1");
			break;
		case ONLY_IN_SYSTRAY:
			PREFERENCES_ROOT.put(SYSTRAY_MODE, "2");
			break;
		case NOT_IN_SYSTRAY:
		default:
			PREFERENCES_ROOT.put(SYSTRAY_MODE, "0");
			break;
		}

		flush();
	}

	static SystrayMode getSystrayMode() {
		final String modeString = PREFERENCES_ROOT.get(SYSTRAY_MODE, "0");
		switch (modeString) {
		case "1":
			return SystrayMode.MINIMIZE_TO_SYSTRAY;
		case "2":
			return SystrayMode.ONLY_IN_SYSTRAY;
		case "0":
		default:
			return SystrayMode.NOT_IN_SYSTRAY;
		}
	}

}
