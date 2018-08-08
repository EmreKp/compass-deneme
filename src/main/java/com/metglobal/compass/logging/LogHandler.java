package com.metglobal.compass.logging;

import com.metglobal.compass.logging.model.LogModel;

public interface LogHandler {
	void handle(LogModel model);
}
