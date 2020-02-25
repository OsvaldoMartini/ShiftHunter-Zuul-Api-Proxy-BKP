package com.shifthunter.zuul;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Joiner;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import static org.junit.Assert.*;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicStringProperty;
import com.netflix.config.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tests_General_Zuul_UnitTest {

	public static final int SIZE_DIM1 = 5;
	public static final int SIZE_DIM2 = 5;
	public static final int SIZE_DIM3 = 3;
	public static final int SIZE_DIM4 = 3;
	private static final int INCREMENT = 2;
	
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;
	public static String TEST_XML_STRING = "<?xml version=\"1.0\" ?><test attrib=\"moretest\">Turn this to JSON</test>";

	private static final Logger logger = LoggerFactory.getLogger(Tests_General_Zuul_UnitTest.class);
	// private static final Logger logger = LoggerFactory.getLogger(Settings.class);

	public static final boolean SORT_BY_REGION = false;
	private static final String DATE_FORMAT = "dd-M-yyyy hh:mm:ss a";

	@Ignore
	@Test
	public void wheInstanteDateisUsed() {

//		System.out.println(LocalDateTime.parse( "2014-02-14T06:04:00:00" )    // Parse a string lacking any indicator of time zone or offset-from-UTC. *Not* a specific point on the timeline.
//        .atOffset( ZoneOffset.UTC )           // Apply UTC as we are certain that offset-from-UTC of zero was intended by the supplier of that input string. Returns a `OffsetDateTime` object.
//        .atZoneSameInstant(                   // Adjust into another time zone. The `sameInstant` part means the same moment, different wall-clock time. 
//            ZoneId.of( "Europe/Zurich" )      // Specify the particular zone of interest to you.
//        ).toString());                                     // Returns a `ZonedDateTime` object.
//		

		String dateInString = "22-1-2015 10:15:55 AM";
		LocalDateTime ldt = LocalDateTime.parse(dateInString, DateTimeFormatter.ofPattern(DATE_FORMAT));

		ZoneId singaporeZoneId = ZoneId.of("Asia/Singapore");
		System.out.println("TimeZone : " + singaporeZoneId);

		// LocalDateTime + ZoneId = ZonedDateTime
		ZonedDateTime asiaZonedDateTime = ldt.atZone(singaporeZoneId);
		System.out.println("Date (Singapore) : " + asiaZonedDateTime);

		ZoneId newYokZoneId = ZoneId.of("America/New_York");
		System.out.println("TimeZone : " + newYokZoneId);

		ZonedDateTime nyDateTime = asiaZonedDateTime.withZoneSameInstant(newYokZoneId);
		System.out.println("Date (New York) : " + nyDateTime);

		ZoneId zurichZoneTime = ZoneId.of("Europe/Zurich");
		System.out.println("TimeZone : " + zurichZoneTime);

		ZonedDateTime zurichDateTime = asiaZonedDateTime.withZoneSameInstant(zurichZoneTime);
		System.out.println("Date (New York) : " + zurichDateTime);

		// Convert a java.util.Date into an Instant
//		Instant instantLocal = Instant.ofEpochMilli(new Date().getTime());
//		System.out.println(LocalDateTime.parse( instantLocal.toString() )    // Parse a string lacking any indicator of time zone or offset-from-UTC. *Not* a specific point on the timeline.
//		        .atOffset( ZoneOffset.UTC )           // Apply UTC as we are certain that offset-from-UTC of zero was intended by the supplier of that input string. Returns a `OffsetDateTime` object.
//		        .atZoneSameInstant(                   // Adjust into another time zone. The `sameInstant` part means the same moment, different wall-clock time. 
//		            ZoneId.of( "Europe/Zurich" )      // Specify the particular zone of interest to you.
//		        ));                                     // Returns a `ZonedDateTime` object.

		DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_FORMAT);
		System.out.println("\n---DateTimeFormatter---");
		System.out.println("Date (Singapore) : " + format.format(asiaZonedDateTime));
		System.out.println("Date (New York) : " + format.format(nyDateTime));
		System.out.println("Date (Zurich) : " + format.format(zurichDateTime));

		for (

				int x = 0; x < 5; x++) {
			int timeStr = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC).getHour();

			System.out.println(timeStr);

			// Convert a java.util.Date into an Instant
			Instant instant = Instant.ofEpochMilli(new Date().getTime());
			System.out.println(instant.toString());

			// Create from a String
			instant = Instant.parse("1995-10-23T10:12:35Z");

			System.out.println(instant.toString());
		}

		// Get All Time zones Available
		Map<String, String> sortedMap = new LinkedHashMap<>();
		Map<String, String> allZoneIdsAndItsOffSet = getAllZoneIdsAndItsOffSet();

		// sort map by key
		if (SORT_BY_REGION) {
			allZoneIdsAndItsOffSet.entrySet().stream().sorted(Map.Entry.comparingByKey())
					.forEachOrdered(e -> sortedMap.put(e.getKey(), e.getValue()));
		} else {
			// sort by value, descending order
			allZoneIdsAndItsOffSet.entrySet().stream().sorted(Map.Entry.<String, String>comparingByValue().reversed())
					.forEachOrdered(e -> sortedMap.put(e.getKey(), e.getValue()));
		}

		// print map
		sortedMap.forEach((k, v) -> {
			String out = String.format("%35s (UTC%s) %n", k, v);
			System.out.printf(out);
		});

		System.out.println("\nTotal Zone IDs " + sortedMap.size());

	}

	private static Map<String, String> getAllZoneIdsAndItsOffSet() {

		Map<String, String> result = new HashMap<>();

		LocalDateTime localDateTime = LocalDateTime.now();

		for (String zoneId : ZoneId.getAvailableZoneIds()) {

			ZoneId id = ZoneId.of(zoneId);

			// LocalDateTime -> ZonedDateTime
			ZonedDateTime zonedDateTime = localDateTime.atZone(id);

			// ZonedDateTime -> ZoneOffset
			ZoneOffset zoneOffset = zonedDateTime.getOffset();

			// replace Z to +00:00
			String offset = zoneOffset.getId().replaceAll("Z", "+00:00");

			result.put(id.toString(), offset);

		}

		return result;

	}

	@Ignore
	@Test
	public void whenBuildPersonWithBuilder_thenObjectHasPropertyValues() {

		Integer[] lstStepDecision = { 2, 1, 1, 4, 6 };
		int[] lstStepInit = new int[5];

		String cols = "";
		for (int i = 0; i < lstStepDecision.length; i++) {
			cols += Integer.toString(lstStepDecision[i]) + " ";
		}
		logger.info(cols);

		int[][] wS = new int[16][5];
		int xCont = 0;

		//TreeMap<String, List<int[]>> lstBinario = new TreeMap<>();
		int[][] lstBinario = new int[1][];
		int[] init = new int[5];
//		List<int[]> list = new ArrayList<int[]>();
//		list.add(init);
		String[] arrOut = {};
//		int[] zeroPos = list.get(0);
		for (int y = 0; y < lstStepDecision.length; y++) {
			arrOut = Arrays.copyOf(arrOut, arrOut.length + 1);
			arrOut[arrOut.length - 1] = Integer.toString(lstStepDecision[y]);
		}
		String wayOut = String.join("-", arrOut);
		//lstBinario.put(cols, list);
		lstBinario[0] = init;
		Set<int[]> hashset = new HashSet<>();
		Set<String> hashsetCols = new HashSet<>();
		
		// lstBinario.push()
		//for (int[] w4 : wS) {
			//for (int xAttention = 0; xAttention < w4.length; xAttention++) {
				//logger.info(Integer.toString(lstStepDecision[xAttention]));
				cols = "";
				//for (int xRoll = 0; xAttention < lstStepDecision[xRoll]; xRoll++) {
				int xRoll = 0;
				while(!cols.equalsIgnoreCase(wayOut)){
					
					String keyName=  "";
					//for (Map.Entry<String, List<int[]>> entry : lstBinario.entrySet()) {
					int[][] localNew = {};

					for (int[] entry : lstBinario) {
						//keyName = entry.getKey();
						//List<int[]> local = entry.getValue();

						//for (int[] unit : local) {
							System.arraycopy(entry, 0, lstStepInit, 0, entry.length);
							
							while (lstStepInit[xRoll] <= lstStepDecision[xRoll]) {
								cols = "";
								String[] arr = new String[0];
								int[] arrInt = new int[0];

								for (int y = 0; y < lstStepInit.length; y++) {
									// System.arraycopy(strCoord, 0, allCoord, xCont, strCoord.length);
									arr = Arrays.copyOf(arr, arr.length + 1);
									arr[arr.length - 1] = Integer.toString(lstStepInit[y]);
									// cols += Integer.toString(lstStepInit[y]) + "-";
									// String allValues = Arrays.asList(keyAttributes).toString();
									arrInt = Arrays.copyOf(arrInt, arrInt.length + 1);
									arrInt[arrInt.length-1] = lstStepInit[y];
								}
								//localNew.add(arrInt);
								localNew = Arrays.copyOf(localNew, localNew.length + 1);
								localNew[localNew.length-1] = arrInt;

								//hashset.add(arrInt);
								cols = String.join("-", arr);
								// cols = Integer.toString(lstStepInit[y]);
								// cols = Joiner.on("-").skipNulls().join("", Integer.toString(lstStepInit[y]));

								logger.info(cols);
								hashsetCols.add(cols);
								lstStepInit[xRoll] += 1;
							}
							lstStepInit[xRoll] = 0;
						//}
						//lstBinario.put(keyName, localNew);
					}
					//logger.info(String.format("hashSet size %s ",hashset.size()));
					logger.info(String.format("hashSetCols size %s ",hashsetCols.size()));
					logger.info(String.format("localNew size %s ",localNew.length));
					
					lstBinario = localNew;
					xRoll++;
				}

				//w4[xAttention] = xCont;
				//xCont++;
			//}
		//}

		for(String val: hashsetCols) {
			String[]  arr = val.split("-");
			displayArray(arr);
		}
		seqFill(wS);

		display2D(wS);
		
		Integer[] lstZeroInit = new Integer[9];
		Arrays.fill(lstZeroInit, 0);
		int xCurrent = 0;
		lstZeroInit[xCurrent] = 0;
		// Looking for the next to Loop
		int LookNext = xCurrent;

		while ((LookNext < lstZeroInit.length) && (lstZeroInit[LookNext] <= lstStepDecision[LookNext])) {

			LookNext++;
			lstZeroInit[LookNext] += 1;
			if (lstZeroInit[LookNext] == lstStepDecision[LookNext]) {
				lstZeroInit[LookNext] = 0;
			} else {
				break;
			}

		}

		int stepCount = 0;
		int xCurrent_2 = 0;
		Integer[] lstStepDecision_2 = { 5, 6, 5, 4, 3, 6, 7, 8, 9 };
		Integer[] lstZeroInit_2 = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		try {
			while (xCurrent_2 < lstStepDecision_2.length) {

				int xLimit = lstStepDecision_2[xCurrent_2];
				for (int xRow = 0; xRow < xLimit; xRow++) {
					lstZeroInit_2[xCurrent] += 1;
					stepCount++;
				}
				lstZeroInit_2[xCurrent_2] = 0;
				xCurrent++;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// Practical Distribution
		String[][] strCoordDim1 = {
				{ "1,2,15", "1,3,15", "2,4,15", "2,6,15", "3,5,15", "3,6,15", "3,7,15", "3,8,15" } };
		String[][] strCoordDim2 = {
				{ "5,9,15", "5,7,15", "5,4,15", "6,4,15", "8,4,15", "9,7,15", "7,4,15", "7,3,15" } };
		String[][] strCoordDim3 = { { "3,8,15", "4,8,15" } };

		String[] strCoordDim4 = { "3,8,15", "4,8,15" };

		String[][] lstMatrizDados = { {} };
		System.out.println(String.format("size %d %s", lstMatrizDados.length, lstMatrizDados[0]));
		lstMatrizDados[0] = new String[5];
		String[][] newVar = new String[lstMatrizDados.length + 1][strCoordDim3[0].length];
		newVar[0] = strCoordDim3[0];

		System.out.println(String.format("size %d %s", lstMatrizDados.length, lstMatrizDados[0]));

		for (int i = 0; i < lstMatrizDados.length; i++) {
			System.arraycopy(newVar[0], 0, lstMatrizDados[0], 0, newVar[0].length);
		}

		lstMatrizDados = Arrays.copyOf(lstMatrizDados, lstMatrizDados.length + 1); // Reduce Eliminates the ".enum.0..."
		lstMatrizDados[lstMatrizDados.length - 1] = new String[strCoordDim4.length];

		for (int i = 0; i < lstMatrizDados.length; i++) {
			System.arraycopy(strCoordDim4, 0, lstMatrizDados[1], 0, strCoordDim4.length);
		}

		// Just Increase the Size as Copy
		lstMatrizDados[0] = Arrays.copyOf(lstMatrizDados[0], lstMatrizDados[0].length + 1); // Reduce Eliminates the
																							// ".enum.0..."

		lstMatrizDados = Arrays.copyOf(lstMatrizDados, lstMatrizDados.length + 1); // Reduce Eliminates the ".enum.0..."

		lstMatrizDados[0] = Arrays.copyOf(lstMatrizDados[0], lstMatrizDados[0].length + 1); // Reduce Eliminates the
																							// ".enum.0..."

		System.out.println(String.format("size %d %s", lstMatrizDados.length, lstMatrizDados[0]));

		// lstMatrizDados = new String[lstMatrizDados[0].length+1];

		String[][] allCoordDim = {
				new String[strCoordDim1[0].length + strCoordDim2[0].length + strCoordDim3[0].length] };

		for (int i = 0; i < allCoordDim.length; i++) {
			System.arraycopy(strCoordDim1[0], 0, allCoordDim[i], 0, strCoordDim1[0].length);
		}

		String[] strCoord1 = { "1,2,15", "1,3,15", "2,4,15", "2,6,15", "3,5,15", "3,6,15", "3,7,15", "3,8,15" };
		String[] strCoord2 = { "5,9,15", "5,7,15", "5,4,15", "6,4,15", "8,4,15", "9,7,15", "7,4,15", "7,3,15" };
		String[] strCoord3 = { "3,8,15", "4,8,15" };

		String[] allCoord = new String[strCoord1.length + strCoord2.length + strCoord3.length];

		System.arraycopy(strCoord1, 0, allCoord, 0, strCoord1.length);
		System.arraycopy(strCoord2, 0, allCoord, strCoord1.length, strCoord2.length);
		System.arraycopy(strCoord3, 0, allCoord, strCoord1.length + strCoord2.length, strCoord3.length);

		int[][][][] w = new int[SIZE_DIM1][SIZE_DIM2][SIZE_DIM3][SIZE_DIM4];

		randomFill(w);

		display(w);

		int[][][][] wNew = new int[SIZE_DIM1 + INCREMENT][SIZE_DIM2 + INCREMENT][SIZE_DIM3][SIZE_DIM4];

		for (int i = 0; i < w.length; i++) {
			for (int j = 0; j < w[i].length; j++) {
				System.arraycopy(w[i][j], 0, wNew[i][j], 0, w[i][j].length);
			}
		}

		display(wNew);

		w = wNew;
		try {
			w[0][3][2][2] = 1;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

//        Person person = new PersonBuilder().setAge(25).setName("John").build();
//        
//        assertEquals(25, person.getAge());
//        assertEquals("John", person.getName());

		String fieldName_1 = "address";
		String fieldName_2 = "address.required";
		String columnLine = null;
		String[] keyAttributes = { ".maxLength", ".minLength", ".required", ".enum" };

		for (int x = 0; x < 5; x++) {
			// columnLine += String.join(",", (columnLine + "," + fieldName_1).split(","));
			fieldName_1 = "address" + "_" + Integer.toString(x);
			columnLine = Joiner.on(",").skipNulls().join(columnLine, fieldName_1);
			// columnLine = Joiner.on(" ").skipNulls().join("I", null, "love", null, "u");
		}

//		arrEnum = Arrays.copyOf(arrEnum, arrEnum.length - 2); // Reduce Eliminates the ".enum.0..."

		// Must fail first
		// assertEquals("address", columnLine);

		// https://stackoverflow.com/questions/1181969/java-get-last-element-after-split
//		String lastone = fieldName.split(Pattern.quote("."))[..];
//		lastone = fieldName(one.split("-"));
//		String lastOne1 = fieldName_2.substring(fieldName_2.lastIndexOf("." + 1));
//		String lastOne = StringUtils.substringAfterLast(fieldName, ".");

		int lastIndexOf = fieldName_2.lastIndexOf(".");
		String lastOne = fieldName_2.substring(lastIndexOf);
		String allValues = Arrays.asList(keyAttributes).toString();
		allValues = String.join(",", keyAttributes);

		/*
		 * Pattern p = Pattern.compile(".+?\\..+?\\.(\\w)"); Matcher m =
		 * p.matcher(allValues);
		 * 
		 * if (m.find()) { System.out.println(m.group(1)); }
		 */

		if (allValues.indexOf(lastOne) > -1) {
			System.out.println("Contains. " + allValues);
			assert (true);
		} else {
			System.out.println("NOT Contains. " + allValues);
			assert (true);
		}

	}

	public static void randomFill(int[][][][] w) {
		Random random = new Random();
		for (int[][][] w2 : w) {
			for (int[][] w3 : w2) {
				for (int[] w4 : w3) {
					for (int i = 0; i < w4.length; i++) {
						w4[i] = random.nextInt();
					}
				}
			}
		}
	}

	public static void seqFill(int[][] w) {
		int xCont = 0;
		for (int[] w4 : w) {
			for (int i = 0; i < w4.length; i++) {
				w4[i] = xCont;
				xCont++;
			}
		}
	}


	public static void displayArray(String[] w) {
		System.out.println("Printing---------------------------------------------------------------------------------");

			System.out.print("\t\t\t[");
			for (String element : w) {
				System.out.print(element + " ");
			}
			System.out.print("]\n");
	}

	
	public static void displayArray(int[] w) {
		System.out.println("Printing---------------------------------------------------------------------------------");

			System.out.print("\t\t\t[");
			for (int element : w) {
				System.out.print(element + " ");
			}
			System.out.print("]\n");
	}
	
	public static void display2D(int[][] w) {
		System.out.println("Printing---------------------------------------------------------------------------------");

		System.out.print("\t\t[\n");
		for (int[] w4 : w) {
			System.out.print("\t\t\t[");
			for (int element : w4) {
				System.out.print(element + " ");
			}
			System.out.print("]\n");
		}

	}

	public static void display(int[][][][] w) {
		System.out.println("Printing---------------------------------------------------------------------------------");

		System.out.print("[\n");
		for (int[][][] w2 : w) {
			System.out.print("\t[\n");
			for (int[][] w3 : w2) {
				System.out.print("\t\t[\n");
				for (int[] w4 : w3) {
					System.out.print("\t\t\t[");
					for (int element : w4) {
						System.out.print(element + " ");
					}
					System.out.print("]\n");
				}
				System.out.print("\t\t]\n");
			}
			System.out.print("\t]\n");
		}
		System.out.print("]\n");

	}

	@Test
	public void testFibponacciRecursive() throws Exception {
		// Definition
		// n = Fn-1 + Fn-2
		int n = 4;
		long expected = 3;
		// Action
		long actual = FibonacciRecursive(n);

		// Assertions
		assertEquals(expected, actual);
	}

	@Test
	public void testFibponacciLoop() throws Exception {
		// Definition
		// n = Fn-1 + Fn-2
		int n = 4;
		long expected = 3;
		// Action
		long actual = FibonacciLoop(n);

		// Assertions
		assertEquals(expected, actual);
	}

	@Test
	public void testFatorial() throws Exception {
		// Definition
		// n! = Fn-1
		int n = 3;
		long expected = 6;
		// Action
		long actual = Fatorial(n);

		// Assertions
		assertEquals(expected, actual);
		System.out.println(String.format("Fatorial: %d Ret: %s", n, Long.toString(actual)));

	}

	public long FibonacciRecursive(int position) throws Exception {
		if (position < 0) {
			throw new Exception("Fibonacci not define negtive nuber!");
		}

		long ret = 0;

		switch (position) {
		case 0:
			return 0;
		case 1:
			return 1;
		case 2:
			return 1;
		default:
			int pos1 = position - 1;
			int pos2 = position - 2;
			ret = FibonacciRecursive(pos1) + FibonacciRecursive(pos2);
			System.out.println(String.format("Pos: %d - %d Ret: %s", pos1, pos2, Long.toString(ret)));
			return ret;
		}
	}

	public long Fatorial(int value) {
		long f = 1;
//		while (value > 1){ 
//            f *= (value-1);
//            value--;
//		}

		for (; value > 0; value--) {
			f *= value;

		}
		return f;
	}

	public int FibonacciLoop(int position) throws Exception {
		if (position < 0) {
			throw new Exception("Fibonacci not define negtive nuber!");
		}
		int i, pos1, pos2, ret = 0;

		switch (position) {
		case 0:
			return 0;
		case 1:
			return 1;
		case 2:
			return 1;
		default:
			pos1 = 0;
			pos2 = 1;
			for (i = 2; i <= position; i++) {
				ret = pos1 + pos2;
				pos1 = pos2;
				pos2 = ret;
				System.out.println(String.format("Pos: %d - %d Ret: %d", pos1, pos2, ret));
			}
		}

		return ret;
	}

	@Test
	public void testValidation() {
		DynamicStringProperty prop = new DynamicStringProperty("abc", "default") {
			public void validate(String newValue) {
				throw new ValidationException("failed");
			}
		};
		try {
			ConfigurationManager.getConfigInstance().setProperty("abc", "new");
			fail("ValidationException expected");
		} catch (ValidationException e) {
			assertNotNull(e);
		}
		assertEquals("default", prop.get());
		assertNull(ConfigurationManager.getConfigInstance().getProperty("abc"));

		try {
			ConfigurationManager.getConfigInstance().addProperty("abc", "new");
			fail("ValidationException expected");
		} catch (ValidationException e) {
			assertNotNull(e);
		}
		assertEquals("default", prop.get());
		assertNull(ConfigurationManager.getConfigInstance().getProperty("abc"));
	}


//	@Test
//	public void convertXMLToJson() {
//		try {
//			String soapmessageString = "<xml>yourStringURLorFILE</xml>";
//			JSONObject soapDatainJsonObject = XML.toJSONObject(soapmessageString);
//			System.out.println(soapDatainJsonObject);
//
//			JSONObject xmlJSONObj = XML.toJSONObject(TEST_XML_STRING);
//			String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
//			System.out.println(jsonPrettyPrintString);
//		} catch (JSONException je) {
//			System.out.println(je.toString());
//		}
//	}

}
