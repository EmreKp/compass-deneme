package com.github.metglobalcompass.logging;

import com.github.metglobalcompass.logging.model.LogModel;

public interface LogHandler {
	void handle(LogModel model);
}
