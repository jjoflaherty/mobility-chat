package be.kpoint.pictochat.util;

import android.os.Build;

public final class ReportBuilder
{
	private static final String LINE_SEPARATOR = "\r\n";

	public static String build() {
		return build(null, null);
	}
	public static String build(String exception, String stackTrace) {
		StringBuilder report = new StringBuilder();

		if (exception != null || stackTrace != null)
			report.append("************ CAUSE OF ERROR ************\n");

		if (exception != null) {
			report.append(exception);
		    report.append(LINE_SEPARATOR);
	    }
		if (stackTrace != null) {
			report.append(stackTrace);
			report.append(LINE_SEPARATOR);
		}

		report.append("\n************ DEVICE INFORMATION ***********\n");
		report.append("Brand: ");
		report.append(Build.BRAND);
		report.append(LINE_SEPARATOR);
        report.append("Device: ");
        report.append(Build.DEVICE);
        report.append(LINE_SEPARATOR);
        report.append("Model: ");
        report.append(Build.MODEL);
        report.append(LINE_SEPARATOR);
        report.append("Id: ");
        report.append(Build.ID);
        report.append(LINE_SEPARATOR);
        report.append("Product: ");
        report.append(Build.PRODUCT);
        report.append(LINE_SEPARATOR);

        report.append("\n************ FIRMWARE ************\n");
        report.append("SDK: ");
        report.append(Build.VERSION.SDK);
        report.append(LINE_SEPARATOR);
        report.append("Release: ");
        report.append(Build.VERSION.RELEASE);
        report.append(LINE_SEPARATOR);
        report.append("Incremental: ");
        report.append(Build.VERSION.INCREMENTAL);
        report.append(LINE_SEPARATOR);

        return report.toString();
	}
}
