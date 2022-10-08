package sbccunittest;

import static java.lang.System.*;
import static junit.framework.Assert.assertTrue;
import static org.apache.commons.lang3.StringUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static sbcc.Core.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.apache.commons.lang3.*;
import org.junit.*;

import sortcomparison.*;

/**
 * Unit test code to grade the sort comparison assignment. 05/02/2019
 * 
 * @author spstrenn
 */
public class BasicSorterTester {

	Sorter sorter;

	static final int INSERTION_SORT = 1;

	static final int MERGE_SORT = 2;

	static final int QUICK_SORT = 3;

	static final int HEAP_SORT = 4;

	final String newLine = System.getProperty("line.separator");

	static final int RANDOM_DATA = 0;

	static final int SORTED_DATA = 1;

	static final int DUPLICATE_DATA = 2;

	public static int totalScore = 0;

	public static int extraCredit = 0;


	@BeforeClass
	public static void beforeTesting() {
		totalScore = 0;
		extraCredit = 0;
	}


	@AfterClass
	public static void afterTesting() {
		println();
		println("Estimated score (w/o late penalties, etc.) = " + totalScore);
		println("Estimated extra credit (assuming on time submission) = " + extraCredit);

		// If the project follows the naming convention, save the results in a folder on
		// the desktop. (Alex Kohanim)
		try {

			String directory = substringAfterLast(System.getProperty("user.dir"), File.separator);
			if (!directory.equals("SortComparisonRI")) {
				String userName = substringBefore(directory, "_").trim();
				String projectName = substringBetween(directory, "_", "_").trim();
				String home = System.getProperty("user.home");
				Files.createDirectories(
						Paths.get(home + File.separator + "Desktop" + File.separator + projectName.toLowerCase()));

				File f = new File(home + File.separator + "Desktop" + File.separator + projectName.toLowerCase()
						+ File.separator + "out.csv");

				FileWriter fw = new FileWriter(f); // the true will append the new data
				fw.write(userName + "," + totalScore + "," + extraCredit + "\r\n");// appends the string to the file
				fw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Before
	public void setUp() throws Exception {
		sorter = new BasicSorter();
	}


	@After
	public void tearDown() throws Exception {
	}


	@Test(timeout = 15000)
	public void testQuickSort() {
		// int wordLength = 10;
		// int minN = 150000;
		// int maxN = 600000;
		// int stepN = 450000;
		int wordLength = 5;
		int minN = 150000;
		int maxN = 600000;
		int stepN = 450000;
		int numSamplesPerTest = 3;
		int numTests = (maxN - minN) / stepN + 1;
		double[] wordCounts = new double[numTests];
		double[] elapsedTimes = new double[numTests];
		double[] elapsedTimesSortedData = new double[numTests];

		standardSortTest(QUICK_SORT, wordLength, minN, maxN, stepN, numSamplesPerTest,
				RANDOM_DATA, true, 10, true, wordCounts, elapsedTimes);

		totalScore += 5;

		// Now verify that it handles sorted data sets properly.
		standardSortTest(QUICK_SORT, wordLength, minN, maxN, stepN, numSamplesPerTest,
				SORTED_DATA, true, 10, true, wordCounts, elapsedTimesSortedData);

		double sortedFactor = 2.0;
		if (elapsedTimesSortedData[elapsedTimesSortedData.length - 1] > sortedFactor
				* elapsedTimes[elapsedTimes.length - 1])
			fail("Sorted-data time limit exceeded.  Expected:  <= " + String.format("%.3f", sortedFactor
					* elapsedTimes[elapsedTimes.length - 1]) + " sec, but was "
					+ String.format("%.3f", elapsedTimesSortedData[elapsedTimesSortedData.length - 1])
					+ " sec.  The time required to QuickSort " + wordCounts[wordCounts.length - 1]
					+ " already-sorted words must be less than " + sortedFactor
					+ " times the time to required to QuickSort " + wordCounts[wordCounts.length - 1]
					+ " words that are in random order.");

		totalScore += 5;
	}


	/**
	 * This is just a speed test for quicksort.
	 */
	// @Test(timeout = 30000)
	public void testQuickSortLarger() {
		int wordLength = 10;
		int minN = 1000000;
		int maxN = 1000000;
		int stepN = 1000000;
		int numSamplesPerTest = 10;
		int numTests = (maxN - minN) / stepN + 1;
		double[] wordCounts = new double[numTests];
		double[] elapsedTimes = new double[numTests];

		standardSortTest(QUICK_SORT, wordLength, minN, maxN, stepN, numSamplesPerTest, RANDOM_DATA, true, 10, true,
				wordCounts, elapsedTimes);

	}


	// @Test(timeout = 15000)
	public void testQuickSortDuplicates() {
		int wordLength = 10;
		int minN = 150000;
		int maxN = 600000;
		int stepN = 450000;
		int numSamplesPerTest = 3;
		int numTests = (maxN - minN) / stepN + 1;
		double[] wordCounts = new double[numTests];
		double[] elapsedTimes = new double[numTests];

		standardSortTest(QUICK_SORT, wordLength, minN, maxN, stepN,
				numSamplesPerTest, DUPLICATE_DATA, true, 10, true,
				wordCounts, elapsedTimes);

	}


	/**
	 * This is just a speed test for merge sort.
	 * 
	 * 
	 */
	// @Test(timeout = 30000)
	public void testMergeSortLarger() {
		int wordLength = 10;
		int minN = 1000000;
		int maxN = 1000000;
		int stepN = 1000000;
		int numSamplesPerTest = 10;
		int numTests = (maxN - minN) / stepN + 1;
		double[] wordCounts = new double[numTests];
		double[] elapsedTimes = new double[numTests];

		standardSortTest(MERGE_SORT, wordLength, minN, maxN, stepN, numSamplesPerTest,
				RANDOM_DATA, true, 10, true, wordCounts, elapsedTimes);
	}


	@Test(timeout = 15000)
	public void testPartition() {
		var words = createRandomStrings(1024, 5); // 10);
		var expectedWords = words.clone();
		Arrays.sort(expectedWords);

		int startNdx = 512;
		int segmentLength = 256;
		int indexOfPivot = sorter.partition(words, startNdx, segmentLength);

		// Verify that all values before indexOfPivot are <= words[indexOfPivot]
		for (int ndx = startNdx; ndx < indexOfPivot; ndx++)
			assertTrue(words[ndx].compareTo(words[indexOfPivot]) <= 0);

		// Verify that all values after indexOfPivot are >= words[indexOfPivot]
		for (int ndx = indexOfPivot + 1; ndx < (startNdx + segmentLength - 1); ndx++)
			assertTrue(words[ndx].compareTo(words[indexOfPivot]) >= 0);

		// Verify that a single partition does not result in a sorted array.
		words = createRandomStrings(100000, 5); // 10);
		sorter.partition(words, 0, 100000);
		int badNdx = verifySortOrder(expectedWords, words);
		assertFalse("The sorted data is out of order at index " + badNdx, badNdx == -1);

		totalScore += 10;
	}


	@Test(timeout = 15000)
	public void testMergeSort() {
		// int wordLength = 10;
		// int minN = 150000;
		// int maxN = 600000;
		// int stepN = 450000;
		int wordLength = 5;
		int minN = 150000;
		int maxN = 600000;
		int stepN = 450000;
		int numSamplesPerTest = 3;
		int numTests = (maxN - minN) / stepN + 1;
		double[] wordCounts = new double[numTests];
		double[] elapsedTimes = new double[numTests];
		double[] elapsedTimesSortedData = new double[numTests];

		standardSortTest(MERGE_SORT, wordLength, minN, maxN, stepN, numSamplesPerTest,
				RANDOM_DATA, true, 10, true, wordCounts, elapsedTimes);

		totalScore += 5;

		standardSortTest(MERGE_SORT, wordLength, minN, maxN, stepN, numSamplesPerTest,
				SORTED_DATA, true, 10, true, wordCounts, elapsedTimesSortedData);

		double sortedFactor = 0.95;
		if (elapsedTimesSortedData[elapsedTimesSortedData.length - 1] > sortedFactor
				* elapsedTimes[elapsedTimes.length - 1])
			fail("The elapsed time required to MergeSort " + wordCounts[wordCounts.length - 1]
					+ " already-sorted words must be less than " + sortedFactor
					+ " times the time to MergeSort " + wordCounts[wordCounts.length - 1]
					+ " words that are in random order.");
		totalScore += 5;
	}


	@Test(timeout = 30000)
	public void testMerge() {
		var originalWords = new String[] { "M", "B", "Z", "A", "F", "D", "C", "P", "Q", "E", "V", "X" };

		var words = new String[] { "M", "B", "Z", "A", "F", "D", "C", "P", "Q", "E", "V", "X" };

		var expectedWords = ArrayUtils.subarray(words, 6, 12);
		Arrays.sort(expectedWords);

		sorter.merge(words, 6, 3, 3);

		for (int ndx = 0; ndx < 6; ndx++)
			assertEquals(originalWords[ndx], words[ndx]);

		int badNdx = verifySortOrder(expectedWords, ArrayUtils.subarray(words, 6, 12));
		assertTrue("The merged data is out of order at index " + badNdx, badNdx == -1);

		totalScore += 5;
	}


	@Test(timeout = 10000)
	public void testHeapSort() {
		// int wordLength = 10;
		// int minN = 150000;
		// int maxN = 600000;
		// int stepN = 450000;
		int wordLength = 5;
		int minN = 150000;
		int maxN = 600000;
		int stepN = 450000;
		int numSamplesPerTest = 3;
		int numTests = (maxN - minN) / stepN + 1;
		double[] wordCounts = new double[numTests];
		double[] elapsedTimes = new double[numTests];

		standardSortTest(HEAP_SORT, wordLength, minN, maxN, stepN, numSamplesPerTest,
				RANDOM_DATA, true, 10, true,
				wordCounts, elapsedTimes);

		extraCredit++;
	}


	@Test(timeout = 30000)
	public void testHeapify() {
		var words = createRandomStrings(100000, 5); // 10);
		sorter.heapify(words);
		verifyIsHeap(words, 0);

		extraCredit++;
	}


	@Test(timeout = 30000)
	public void testInsertionSort() {
		// int wordLength = 10;
		// int minN = 25000;
		// int maxN = 100000;
		// int stepN = 75000;
		int wordLength = 5;
		int minN = 10000;
		int maxN = 40000;
		int stepN = 30000;
		int numSamplesPerTest = 2;
		int numTests = (maxN - minN) / stepN + 1;
		double[] wordCounts = new double[numTests];
		double[] elapsedTimes = new double[numTests];

		standardSortTest(INSERTION_SORT, wordLength, minN, maxN, stepN, numSamplesPerTest,
				RANDOM_DATA, false, 8.5, true, wordCounts, elapsedTimes);

		totalScore += 5;
	}


	void standardSortTest(int sortId, int wordLength, int minN, int maxN, int stepN,
			int numSamplesPerTest, int dataOrder, boolean ensureTimeRatioBelowThreshold,
			double lastFirstTimeRatioThreshold, boolean printResults, double[] wordCounts,
			double[] elapsedTimes) {

		String[] words;
		double sumTime;
		int testNdx = 0;
		long startTime;
		double elapsedTime;

		if (printResults) {
			switch (sortId) {
			case INSERTION_SORT:
				out.print("\nInsertion Sort");
				break;

			case QUICK_SORT:
				out.print("\nQuick Sort");
				break;

			case MERGE_SORT:
				out.print("\nMerge Sort");
				break;

			case HEAP_SORT:
				out.print("\nHeap Sort");
				break;
			}
		}

		switch (dataOrder) {
		case SORTED_DATA:
			println(" sorted data");
			break;
		case RANDOM_DATA:
			println(" random data");
			break;
		case DUPLICATE_DATA:
			println(" duplicated data");
			break;
		}

		for (int count = minN; count <= maxN; count += stepN) {
			sumTime = 0;
			for (int ndx = 0; ndx < numSamplesPerTest; ndx++) {
				String[] expectedWords = null;
				if (dataOrder == DUPLICATE_DATA) {
					var word = RandomStringUtils.randomAlphabetic(wordLength)
							.toUpperCase();
					words = new String[count];
					Arrays.fill(words, word);
					expectedWords = words.clone();
				} else {
					words = createRandomStrings(count, wordLength); // 10000 -> 0.3 s, 40000 -> 4.3 s
					expectedWords = words.clone();
					Arrays.sort(expectedWords);
					if (dataOrder == SORTED_DATA)
						Arrays.sort(words);
				}

				startTime = nanoTime();
				switch (sortId) {
				case INSERTION_SORT:
					sorter.insertionSort(words, 0, words.length);
					break;

				case QUICK_SORT:
					sorter.quickSort(words, 0, words.length);
					break;

				case MERGE_SORT:
					sorter.mergeSort(words, 0, words.length);
					break;

				case HEAP_SORT:
					sorter.heapSort(words);
					break;
				}
				elapsedTime = (nanoTime() - startTime) / 1.0e9;
				sumTime += elapsedTime;
				int badNdx = verifySortOrder(expectedWords, words);
				assertTrue("The sorted data is out of order at index " + badNdx, badNdx == -1);
			}
			wordCounts[testNdx] = count;
			elapsedTimes[testNdx] = sumTime / numSamplesPerTest;
			// println("N = " + count + "\tTime: " + elapsedTimes[testNdx]);
			testNdx++;
		}

		double countRatio = wordCounts[elapsedTimes.length - 1] / wordCounts[0];
		double timeRatio = elapsedTimes[elapsedTimes.length - 1] / elapsedTimes[0];

		if (printResults) {
			println("\tN = " + wordCounts[0] + ", time = " + String.format("%.3f sec", elapsedTimes[0]));
			println("\tN = " + wordCounts[elapsedTimes.length - 1] + ", time = "
					+ String.format("%.3f sec", elapsedTimes[elapsedTimes.length - 1]));
			println("\tCount:  " + countRatio + "x,  Time: " + String.format("%.1f", timeRatio) + "x");
		}

		boolean failedTimeRatioRequirement = false;
		String failureMessage = "";

		if (ensureTimeRatioBelowThreshold) {
			if (timeRatio > lastFirstTimeRatioThreshold) {
				failedTimeRatioRequirement = true;
				failureMessage = "Expected the ratio of the time for " + wordCounts[wordCounts.length - 1]
						+ " data elements to the time for " + wordCounts[0] + " data elements to be <= "
						+ lastFirstTimeRatioThreshold + ", but it was measured as " + timeRatio + ".";
			}
		} else {
			if (timeRatio < lastFirstTimeRatioThreshold) {
				failedTimeRatioRequirement = true;
				failureMessage = "Expected the ratio of the time for " + wordCounts[wordCounts.length - 1]
						+ " data elements to the time for " + wordCounts[0] + " data elements to be >= "
						+ lastFirstTimeRatioThreshold + ", but it was measured as " + timeRatio + ".";
			}
		}

		if (failedTimeRatioRequirement) {
			StringBuilder sb = new StringBuilder();

			switch (sortId) {
			case INSERTION_SORT:
				sb.append("Insertion Sort")
						.append(newLine);
				break;

			case QUICK_SORT:
				sb.append("Quick Sort")
						.append(newLine);
				break;

			case MERGE_SORT:
				sb.append("Merge Sort")
						.append(newLine);
				break;

			case HEAP_SORT:
				sb.append("Heap Sort")
						.append(newLine);
				break;
			}

			for (int ndx = 0; ndx < elapsedTimes.length; ndx++) {
				sb.append("" + wordCounts[ndx] + "\t" + elapsedTimes[ndx])
						.append(newLine);
			}

			sb.append(failureMessage);
			fail(failureMessage);
		}

	}


	private void verifyIsHeap(String[] words, int i) {
		if ((2 * i + 1) > words.length)
			return;
		if (words[i].compareTo(words[2 * i + 1]) < 0)
			fail("heapify() failed.  The parent (" + words[i] + ") is less than its left child ("
					+ words[2 * i + 1] + ").");

		if ((2 * i + 2) < words.length)
			if (words[i].compareTo(words[2 * i + 2]) < 0)
				fail("heapify() failed.  The parent (" + words[i] + ") is less than its right child ("
						+ words[2 * i + 2] + ").");

		verifyIsHeap(words, 2 * i + 1);
		if ((2 * i + 2) < words.length)
			verifyIsHeap(words, 2 * i + 2);
	}


	public String[] createRandomStrings(int n, int wordLength) {
		var words = new String[n];
		for (int ndx = 0; ndx < n; ndx++)
			words[ndx] = RandomStringUtils.randomAlphabetic(wordLength)
					.toUpperCase();
		return words;
	}


	public int verifySortOrder(String[] expectedWords, String[] words) {
		int badNdx = -1;
		for (int ndx = 0; ndx < words.length; ndx++) {
			if (words[ndx].compareTo(expectedWords[ndx]) != 0) {
				badNdx = ndx;
				break;
			}
		}

		return badNdx;
	}


	@Test
	public void testPmd() {
		try {
			execPmd("src" + File.separator + "sortcomparison", "cs106.ruleset");
		} catch (Exception ex) {
			fail(ex.getMessage());
		}

		totalScore += 5;

	}


	private static void execPmd(String srcFolder, String rulePath) throws Exception {

		File srcDir = new File(srcFolder);
		File ruleFile = new File(rulePath);

		verifySrcAndRulesExist(srcDir, ruleFile);

		ProcessBuilder pb;
		if (getProperty("os.name").toLowerCase()
				.indexOf("win") >= 0) {
			String pmdBatPath = ".\\pmd_min\\bin\\pmd.bat";
			String curPath = Paths.get(".")
					.toAbsolutePath()
					.toString();

			// Handle CS lab situation where the current dir is a UNC path
			if (curPath.startsWith("\\\\NEBULA\\cloud$")) {
				curPath = "N:\\" + substringAfter(curPath, "cloud$\\");
				pmdBatPath = curPath + pmdBatPath.substring(1);
			}
			pb = new ProcessBuilder(
					pmdBatPath,
					"-f", "text",
					"-d", srcDir.getAbsolutePath(),
					"-R", ruleFile.getAbsolutePath());
		} else {
			pb = new ProcessBuilder(
					"./pmd_min/bin/run.sh", "pmd",
					"-d", srcDir.getAbsolutePath(),
					"-R", ruleFile.getAbsolutePath());
		}
		Process process = pb.start();
		int errCode = process.waitFor();

		switch (errCode) {

		case 1:
			println("PMD Check: -5 pts");
			String errorOutput = getOutput(process.getErrorStream());
			fail("Command Error:  " + errorOutput);
			break;

		case 4:
			println("PMD Check: -5 pts");
			String output = getOutput(process.getInputStream());
			fail(trimFullClassPaths(output));
			break;

		}

	}


	private static String trimFullClassPaths(String output) {
		// Shorten output to just the short class name, line, and error.
		String[] lines = output.split(getProperty("line.separator"));
		StringBuilder sb = new StringBuilder();
		for (String line : lines)
			sb.append(substringAfterLast(line, File.separator))
					.append(lineSeparator());

		String trimmedOutput = sb.toString();
		return trimmedOutput;
	}


	private static void verifySrcAndRulesExist(File fileFolderToCheck, File ruleFile) throws Exception {
		if (!fileFolderToCheck.exists())
			throw new FileNotFoundException(
					"The folder to check '" + fileFolderToCheck.getAbsolutePath() + "' does not exist.");

		if (!fileFolderToCheck.isDirectory())
			throw new FileNotFoundException(
					"The folder to check '" + fileFolderToCheck.getAbsolutePath() + "' is not a directory.");

		if (!ruleFile.exists())
			throw new FileNotFoundException(
					"The rule set file '" + ruleFile.getAbsolutePath() + "' could not be found.");
	}


	private static String getOutput(InputStream inputStream) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + getProperty("line.separator"));
			}
		} finally {
			br.close();
		}
		return sb.toString();

	}
}
