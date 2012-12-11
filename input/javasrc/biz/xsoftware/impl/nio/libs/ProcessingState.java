package biz.xsoftware.impl.nio.libs;

public enum ProcessingState {
	PROCESSING_HEADER,
	PROCESSING_BODY,
	PROCESSING_TAIL,
	RECOVERING;
}
