package battery;

import java.awt.Color;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.renjin.script.RenjinScriptEngineFactory;

import com.opencsv.CSVWriter;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class BatteryReport extends Application {
	private static final String cmdCommand = "cmd /c";
	private static final String batteryCommand = "powercfg/batteryreport";

	// Comouter overview
	private static final String computerName = "COMPUTER NAME";
	private static final String systemModel = "SYSTEM PRODUCT NAME";
	private static final String bios = "BIOS";
	private static final String reportTime = "REPORT TIME";
	private static Map<String, String> overView = new LinkedHashMap<>();

	// Battery Info
	private static String battery;
	private static final String batteryName = "NAME";
	private static final String manufacture = "MANUFACTURE";
	private static final String serialNumber = "SERIAL NUMBER";
	private static final String chemistry = "CHEMISTRY";
	private static final String designCapacity = "DESIGN CAPACITY";
	private static final String fullChargeCapacity = "FULL CHARGE CAPACITY";
	private static final String percentage = "PERCENTAGE";
	private static Map<String, String> batteryInfo = new LinkedHashMap<>();

	// Compare
	private static List<String[]> currentCapac = new LinkedList<>();
	public static final String CSVNAME = "BatteryData.csv";

	// LastList
	private static LinkedList<Double> dateList;
	private static LinkedList<Integer> currentList;

	@Override
	public void start(Stage primaryStage) throws Exception {
		startBox();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static void execute() throws IOException, ScriptException {

		// Setting up to connect to cmd
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(cmdCommand + batteryCommand);
		InputStream input = process.getInputStream();
		// Read the url where the batteryReport.html is saved
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String result = reader.readLine();
		reader.close();
		String path = "path";

		// get the word after the path
		int firstIndex = result.indexOf(path) + path.length() + 1;

		// -1 to trip the . behind
		int lastIndext = result.length() - 1;
		// The url where the batteryReport is saved
		String url = result.substring(firstIndex, lastIndext);
//		File batteryReport = new File("C:\\Users\\LongP\\Downloads\\battery-report.html");

		// Create a File with url
		File batteryReportHTML = new File(url);
		Document batteryHTML = Jsoup.parse(batteryReportHTML, null);
//	            System.out.println(batteryHTML);

		// Select elements with </td> in html file
		Elements elements = batteryHTML.select("td");

		// A new String that will save all of the data take from html
		String trimReport = "";

		// Initialized trimReport with all of the data from BatteryReport.html
		for (Element e : elements) {
			trimReport = trimReport + e.ownText() + "\n";
		}

		// scan and temp to check line by line in the battery Report
		Scanner scan = new Scanner(trimReport);
		String temp;

		// timeStamp to save the time when the battery was generated
		String timeStamp = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd ").format(Calendar.getInstance().getTime());

		// Scan the computer information section until meet REPORT TIME
		// Saved computer info HashMap overView
		while (!(temp = scan.nextLine()).equals("REPORT TIME")) {
			if (temp.equals(computerName)) {
				overView.put(computerName, scan.nextLine());
			}
			if (temp.equals(systemModel)) {
				overView.put(systemModel, scan.nextLine());
			}
			if (temp.equals(bios)) {
				overView.put(bios, scan.nextLine());
			}
		}
		// saved the report generated time into HashMap overView
		overView.put(reportTime, timeStamp);

		// Scan until see the Battery Info
		while (!(scan.nextLine().contains("BATTERY"))) {

		}

		// Put battery info in HashMap batteryInfo
		scan.nextLine();
		batteryInfo.put(batteryName, scan.nextLine());
		scan.nextLine();
		batteryInfo.put(manufacture, scan.nextLine());
		scan.nextLine();
		batteryInfo.put(serialNumber, scan.nextLine());
		scan.nextLine();
		batteryInfo.put(chemistry, scan.nextLine());
		scan.nextLine();
		batteryInfo.put(designCapacity, scan.nextLine());
		scan.nextLine();
		batteryInfo.put(fullChargeCapacity, scan.nextLine());
		batteryInfo.put(percentage, percentage(batteryInfo.get(fullChargeCapacity), batteryInfo.get(designCapacity)));

		// print the batteryInfo
		for (String a : batteryInfo.keySet()) {
			System.out.println(a + ": " + batteryInfo.get(a));
		}

		// Scan for Battery CapacHistory
		while (true) {
			if (scan.nextLine().equals("DESIGN CAPACITY"))
				break;
		}
		
		
		while (true) {
			// create and array of "Date", "currentCapcity", "design Capacity"
			/** The data is saved by
				Data
				currentCapacity
				designCacpacity is take from the batteryInfo above
			**/
			String[] tempList = new String[3];
			String date = scan.nextLine();
			if (date.length() == 0) {
				break;
			}
			tempList[0] = date;
			String current = scan.nextLine();
			tempList[1] = current;
			tempList[2] = batteryInfo.get(designCapacity);
			// Add to a List<String[]> currentCapac
			currentCapac.add(tempList);
			scan.nextLine();
		}

		//write the data from List<String[]> currentCapac into BatteryData.csv
		writeCSV(CSVNAME);
		
		// create a Renjin engine and analyzed the BatteryData.csv with R
		RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();
		ScriptEngine engine = factory.getScriptEngine();
		
		// read the BatteryData.csv
		engine.eval("data <- read.csv(\"BatteryData.csv\")");
		
		//Delete the mWh unit in the data and the "," between the number
		engine.eval("data$Current.Capacity <- gsub(\"mWh\", \"\", data$Current.Capacity)");
		engine.eval("data$Current.Capacity <- gsub(\",\", \"\" ,data$Current.Capacity)");
		engine.eval("data$Design.Capacity <- gsub(\"mWh\", \"\", data$Design.Capacity)");
		engine.eval("data$Design.Capacity <- gsub(\",\", \"\", data$Design.Capacity)");
		
		//convert currentCapacity and design capacity into integer for calculation and plot
		engine.eval("data$Current.Capacity <- as.character(data$Current.Capacity)");
		engine.eval("data$Current.Capacity <- as.integer(data$Current.Capacity)");
		engine.eval("data$Design.Capacity <- as.integer(as.character(data$Design.Capacity))");
		
		//Convert Data to character
		engine.eval("data$Date <- as.character(data$Date)");
		
		/*Get the interval when the time is calculated by weeks and when it calculated by day
		 * variable length is when it is calculated by week, the length of the Date string is long
		 * variable length is when it is calculated by day, and the Date length is 10 
		 */
		engine.eval("length <-length(data[nchar(data$Date) > 10, 1])");
		engine.eval("length2 <- length(data[nchar(data$Date) == 10,1])");
		
		/*If it is week the sequence generate by 1 unit each time 
		 * If it is day it will use the formula below to generate the fraction of a week 
		 */
		engine.eval("data[nchar(data$Date) > 10, 1] <- seq(1,length , 1)");
		engine.eval("data[nchar(data$Date) == 10,1 ]  <- seq(length, length + length2*0.14, 0.14285)");
		engine.eval("data$Date <- as.double(data$Date)");
		
		// Write data back to BatteryData.csv and turn off row.names (1,2,3,4,5,....)
		engine.eval("write.csv(data, file =\"BatteryData.csv\", row.names = F)");

		reader = new BufferedReader(new FileReader("BatteryData.csv"));
		
		// 2 List that read from the BatteryData.csv
		setDateList(new LinkedList<>());
		setCurrentList(new LinkedList<>());
		reader.readLine();
		String read;
		while (true) {
			read = reader.readLine();
			if (read == null) {
				break;
			}
			String[] data = read.split(",");
			getDateList().add(Double.parseDouble(data[0].trim()));
			getCurrentList().add(Integer.parseInt(data[1]));
		}
		batteryReportHTML.delete();
	}

	public static int evaluate(String input) {
		input = input.replace(",", "");
		input = input.replace("mWh", "");
		input = input.trim();
		return Integer.parseInt(input);
	}

	public static String percentage(String string1, String string2) {
		int num1 = evaluate(string1);
		int num2 = evaluate(string2);
		double ratio = num1 * 100 / num2;
		String percentage = (ratio) + "%";
		return percentage;
	}

	public static void writeCSV(String fileName) throws IOException {
		// Recieve the String contains all battery capacity data and write it in a CSV
		// file
		File file = new File(fileName);
		FileWriter outputfile = new FileWriter(file);
		CSVWriter writer = new CSVWriter(outputfile);

		String[] header = { "Date", "Current Capacity", "Design Capacity" };
		// Write the header
		
		writer.writeNext(header);
		
		// currentCapac is String[] with "Date","currentCapac", "designCapac"
		for (String[] a : currentCapac) {
			writer.writeNext(a);
		}
		writer.close();
	}

	public static LinkedList<Double> getDateList() {
		return dateList;
	}

	public static void setDateList(LinkedList<Double> dateList) {
		BatteryReport.dateList = dateList;
	}

	public static LinkedList<Integer> getCurrentList() {
		return currentList;
	}

	public static void setCurrentList(LinkedList<Integer> currentList) {
		BatteryReport.currentList = currentList;
	}

	private void startBox() throws IOException, ScriptException {
		Alert startBox = new Alert(Alert.AlertType.CONFIRMATION);
		startBox.setTitle("Start");
		startBox.setHeaderText("Click Button Scan to scan for battery health");
		ButtonType scanButton = new ButtonType("Scan", ButtonBar.ButtonData.OK_DONE);
		ButtonType exitButton = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);
		startBox.getDialogPane().getButtonTypes().setAll(scanButton, exitButton);
		Optional<ButtonType> result = startBox.showAndWait();
		if (result.get() == scanButton) {
			execute();
			resultBox();
		}
	}

	private static void resultBox() {
		Alert resultBox = new Alert(Alert.AlertType.INFORMATION);
		resultBox.setTitle("Scan Complete");
		resultBox.setHeaderText("Scan complete. Choose short result or choose Details Data");
		resultBox.setContentText(computerName + ": " + overView.get(computerName) + "\n" + systemModel + ": "
				+ overView.get(systemModel) + "\n" + bios + ": " + overView.get(bios) + "\n" + reportTime + ": "
				+ overView.get(reportTime));
		ButtonType result = new ButtonType("short result", ButtonBar.ButtonData.OK_DONE);
		ButtonType data = new ButtonType("Details Data", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancel = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);
		resultBox.getDialogPane().getButtonTypes().setAll(result, data, cancel);
		Optional<ButtonType> choice = resultBox.showAndWait();
		if (choice.get() == result) {
			shortDetailBox();
		} else {
			if (choice.get() == data) {
				detailsBox();
			}
		}
	}

	private static void shortDetailBox() {
		Alert resultBox = new Alert(Alert.AlertType.INFORMATION);
		resultBox.setTitle("Battery Report");
		resultBox.setHeaderText(batteryInfo.get(percentage));
		resultBox.setContentText(batteryName + ": " + batteryInfo.get(batteryName) + "\n" + manufacture + ": "
				+ batteryInfo.get(manufacture) + "\n" + serialNumber + ": " + batteryInfo.get(serialNumber) + "\n"
				+ chemistry + ": " + batteryInfo.get(chemistry) + "\n" + designCapacity + ": "
				+ batteryInfo.get(designCapacity) + "\n" + fullChargeCapacity + ": "
				+ batteryInfo.get(fullChargeCapacity) + "\n" + percentage + ": " + batteryInfo.get(percentage));
		ButtonType data = new ButtonType("Details Data", ButtonBar.ButtonData.OK_DONE);
		ButtonType exit = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);
		resultBox.getDialogPane().getButtonTypes().setAll(exit, data);
		Optional<ButtonType> choice = resultBox.showAndWait();
		if (choice.get() == data) {
			detailsBox();
		}
	}

	private static void detailsBox() {
		chartBox();
	}

	private static void chartBox() {
		// Scatter Plot
		SwingUtilities.invokeLater(() -> {
			Chart chart = new Chart("Battery vs Time");
			chart.setSize(800, 400);
			chart.setLocationRelativeTo(null);
			chart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			chart.setVisible(true);
		});

		Desktop desktop = Desktop.getDesktop();
		File file = new File(CSVNAME);
		try {
			desktop.open(file);
		} catch (IOException e) {

		}
	}

}

class Chart extends JFrame {
	private XYDataset dataSet;
	private JFreeChart chart;
	public Chart(String title) {
		super(title);
		dataSet = createDataSet();
		chart = ChartFactory.createScatterPlot("Battery Capacity vs Weeks", "weeks", "Battery Capacity(mWh)",
				dataSet);
		XYPlot plot =  (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(new Color(255, 228, 196));
		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);
	}

	public XYDataset createDataSet() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries("Plot");
		for (int i = 0; i < BatteryReport.getDateList().size(); i++) {
			series.add(BatteryReport.getDateList().get(i), BatteryReport.getCurrentList().get(i));
		}
		dataset.addSeries(series);
		return dataset;
	}
}
