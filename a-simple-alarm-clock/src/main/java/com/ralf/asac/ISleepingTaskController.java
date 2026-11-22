package com.ralf.asac;

import java.time.LocalDateTime;

interface ISleepingTaskController {
	void sleep(final int durationSeconds);

	LocalDateTime getCurrentDateTime();
}
