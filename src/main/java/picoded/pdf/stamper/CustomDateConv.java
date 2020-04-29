/**
 * 
 */
package picoded.pdf.stamper;

import java.util.Calendar;
import java.text.DateFormatSymbols;
//import org.apache.commons.lang3.ArrayUtils;

//import com.mysql.jdbc.StringUtils;


/**
 * @author Tai Sheng Sheng
 * @source copy from FA-Portal javacommons-core
 *
 */
public class CustomDateConv {

	///
	/// Convenience class to convert between date types
	/// Month is 1-indexed
	/// Default dateformat is DD-MM-YYYY like the rest of the civilised world uses
	///
		public enum ISODateFormat {
			DDMMYYYY, MMDDYYYY, YYYYMMDD, YYYYDDMM
		}

		public static ISODateFormat toISODateFormat(String format) {
			if (format == null || format.isEmpty()) {
				return ISODateFormat.DDMMYYYY;
			}

			String format_cleaned = CustomRegexUtils.removeAllNonAlphaNumeric(format);

			if (format_cleaned.equalsIgnoreCase("ddmmyyyy")) {
				return ISODateFormat.DDMMYYYY;
			} else if (format_cleaned.equalsIgnoreCase("mmddyyyy")) {
				return ISODateFormat.MMDDYYYY;
			} else if (format_cleaned.equalsIgnoreCase("yyyymmdd")) {
				return ISODateFormat.YYYYMMDD;
			} else if (format_cleaned.equalsIgnoreCase("yyyyddmm")) {
				return ISODateFormat.YYYYDDMM;
			} else {
				return ISODateFormat.DDMMYYYY;
			}
		}

		public static String toISOFormat(long inDate, ISODateFormat dateFormat, String separator) {
			return toISOFormat(inDate, dateFormat, separator, false, "", false);
		}
		
		public static String toISOFormat(long inDate, ISODateFormat dateFormat, String separator, boolean formatMonthAsMMM) {
			return toISOFormat(inDate, dateFormat, separator, false, "", formatMonthAsMMM);
		}

		public static String toISOFormat(long inDate, ISODateFormat dateFormat, String separator, boolean includeTime, String middleText){
			return toISOFormat(inDate, dateFormat, separator, includeTime, middleText, false);
		}
		
		public static String toISOFormat(long inDate, ISODateFormat dateFormat, String separator, boolean includeTime, String middleText, boolean formatMonthAsMMM){
			if (separator == null) {
				separator = "-";
			}
			String finalDate = "";

			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(inDate);
			
			String date = "" + cal.get(Calendar.DATE);
			if (date.length() == 1) {
				date = "0" + date;
			}
			
			int monthZeroIndexed = cal.get(Calendar.MONTH);
			String month = "";
			if(formatMonthAsMMM){
				month = new DateFormatSymbols().getShortMonths()[monthZeroIndexed];
			}else{
				month += (monthZeroIndexed + 1);
				if (month.length() == 1) {
					month = "0" + month;
				}
			}
			
			// zero padding for year so it follows YYYY format
			String year = String.format("%04d", cal.get(Calendar.YEAR));

			finalDate = "" + date + separator + month + separator + year;
			finalDate = changeISODateFormat(finalDate, ISODateFormat.DDMMYYYY, dateFormat, separator);
			
			if(includeTime){
				cal.setTimeInMillis(inDate);

				int hour = cal.get(Calendar.HOUR_OF_DAY);
				int minutes = cal.get(Calendar.MINUTE);

				String timeSection = "" + hour + ":" + (Integer.toString(minutes).length() == 1 ? "0"+minutes : minutes);
				finalDate += middleText + timeSection;
			}

			return finalDate;
		}

		///
		/// I return string to that i can return null if an error happened during conversion
		///
		public static String toMillisecondsFormat(String inDate, ISODateFormat currentDateFormat, String separator) {
			if ( inDate == null || inDate.length() <= 0 ) {
				return null;
			}

			String newDate = changeISODateFormat(inDate, currentDateFormat, ISODateFormat.YYYYMMDD, separator);
			String[] newDateSplit = newDate.split(separator);

			if (newDateSplit == null || newDateSplit.length != 3) {
				return null;
			}

			Calendar cal = Calendar.getInstance();
			cal.set(Integer.parseInt(newDateSplit[0]), (Integer.parseInt(newDateSplit[1]) - 1),
				Integer.parseInt(newDateSplit[2]));
			return "" + cal.getTimeInMillis();

		}

		///
		/// Util functions
		///
		public static boolean isInISOFormat(String inDateString) {
			if (inDateString.indexOf("-") != inDateString.lastIndexOf("-")) {
				return true;
			} else {
				return false;
			}
		}

		public static boolean isInMillisecondsFormat(String inDateString) {
			if (inDateString.startsWith("-") || !inDateString.contains("-")) {
				return true;
			} else {
				return false;
			}
		}

		public static String getCurrentDateISO(ISODateFormat dateFormat, String separator) {
			if (separator == null) {
				separator = "-";
			}

			Calendar cal = Calendar.getInstance();
			int date = cal.get(Calendar.DATE);
			int month = cal.get(Calendar.MONTH) + 1;
			int year = cal.get(Calendar.YEAR);

			String newDate = "" + date + separator + month + separator + year; //ddmmyyyy
			newDate = changeISODateFormat(newDate, ISODateFormat.DDMMYYYY, dateFormat, separator);

			return newDate;
		}

		public static String getCurrentDateTimeISO(ISODateFormat dateFormat, String separator){
			if (separator == null) {
				separator = "-";
			}

			String newDateTime = getCurrentFullyFormattedDateAndTime(separator);
			String datePortion = newDateTime.split(", ")[0];
			String timePortion = newDateTime.split(", ")[1];

			String date = changeISODateFormat(datePortion, ISODateFormat.DDMMYYYY, dateFormat, separator);
			return date + ", " + timePortion;
		}

		public static String getCurrentTimestampInMillis(){
			Calendar cal = Calendar.getInstance();
			return "" + cal.getTimeInMillis();
		}

		// Always returns as dd-mm-yyyy, XX:XX:XX
		private static String getCurrentFullyFormattedDateAndTime(String separator){
			if (separator == null) {
				separator = "-";
			}

			Calendar cal = Calendar.getInstance();
			int date = cal.get(Calendar.DATE);
			int month = cal.get(Calendar.MONTH) + 1;
			int year = cal.get(Calendar.YEAR);

			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minutes = cal.get(Calendar.MINUTE);
			int seconds = cal.get(Calendar.SECOND);
			int milliseconds = cal.get(Calendar.MILLISECOND);

			String newDateTime = "" + date + separator + month + separator + year; //ddmmyyyy
			newDateTime += ", " + hour + ":" + minutes + ":" + seconds + ":" + milliseconds; //add the time

			return newDateTime;
		}

		///
		/// Convert from one ISO date format to another format
		///
		public static String changeISODateFormat(String inDateISO, ISODateFormat currentDateFormat,
			ISODateFormat newDateFormat, String separator) {
			if (inDateISO == null || (currentDateFormat == null && newDateFormat == null)) {
				return null;
			}

			if (separator == null) {
				separator = "-";
			} else {
				//TODO sanitise separator string?
			}

			String[] dateSplit;
			if(separator.contains("|"))
			{
				String tempSeparator = separator.replace("|", "\\|");
				dateSplit = inDateISO.split(tempSeparator);
			}
			else{
				dateSplit = inDateISO.split(separator);
			}

			if (dateSplit == null || dateSplit.length != 3) {
				return null;
			}
			dateSplit = resortDateArray(dateSplit, currentDateFormat, newDateFormat);

			StringBuilder sb = new StringBuilder();
			for (byte i = 0; i < dateSplit.length; ++i) {
				sb.append(dateSplit[i]);

				if (i < dateSplit.length - 1) {
					sb.append(separator);
				}
			}

			return sb.toString();
		}

		private static String[] resortDateArray(String[] inDateSplit, ISODateFormat currentDateFormat,
			ISODateFormat newDateFormat) {
			String[] dateSplit = new String[3];

			byte[] currentDateSorting = getISODateSorting(currentDateFormat);
			byte[] newDateSorting = getISODateSorting(newDateFormat);

			for (byte i = 0; i < dateSplit.length; ++i) {
				//this requires later Java version (57) , use the following instead for older version
				//dateSplit[i] = inDateSplit[ArrayUtils.indexOf(currentDateSorting, newDateSorting[i])];
				
				//this is custom to search for byte
				int newIndex = byteIndexOf(currentDateSorting, newDateSorting[i]);
				if (newIndex == -404) {
					throw new RuntimeException("Unknown dateformat found: " +currentDateFormat+" to "+newDateFormat);
				} else if (newIndex == -400 ) {
					throw new RuntimeException("Unable to process dateformat: " +currentDateFormat+" to "+newDateFormat);
				} else {
					dateSplit[i] = inDateSplit[byteIndexOf(currentDateSorting, newDateSorting[i])];
				}
			}

			return dateSplit;
		}

		private static byte[] getISODateSorting(ISODateFormat dateFormat) {
			switch (dateFormat) {
			case DDMMYYYY:
				return new byte[] { 0, 1, 2 };
			case MMDDYYYY:
				return new byte[] { 1, 0, 2 };
			case YYYYMMDD:
				return new byte[] { 2, 1, 0 };
			case YYYYDDMM:
				return new byte[] { 2, 0, 1 };
			default:
				return null;
			}
		}
		
		private static int byteIndexOf(byte[] byteArr , byte byteToFind ) {
			if (byteArr==null)  return -400; //bad request
			
	        for (int i = 0; i < byteArr.length; i++) {
	            if ( Byte.compare(byteToFind,byteArr[i]) == 0 ) {
	                return i;
	            }
	        }
	        return -404; //not found
		}
}
