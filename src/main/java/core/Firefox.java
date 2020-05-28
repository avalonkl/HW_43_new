package core;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Firefox {

	static Properties p = new Properties();
	static Writer report;
	static String ls = System.getProperty("line.separator");
	static WebDriver driver;

	public static boolean isElementPresent(By by) {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return driver.findElements(by).size() == 1;
	}

	public static String getSize(By by) {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return isElementPresent(by) && driver.findElement(by).isDisplayed()
				? driver.findElement(by).getRect().getDimension().toString().replace(", ", "x")
				: "null";
	}

	public static String getLocation(By by) {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return isElementPresent(by) && driver.findElement(by).isDisplayed()
				? driver.findElement(by).getRect().getPoint().toString().replace(", ", "x")
				: "null";
	}

	public static void setValue(By by, String value) {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		if (isElementPresent(by) && driver.findElement(by).isDisplayed())
			driver.findElement(by).sendKeys(value);
	}

	public static String getValue(By by) {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return isElementPresent(by) && driver.findElement(by).isDisplayed() && !driver.findElement(by).getText().matches("") && 
				driver.findElement(by).getText().length() < 100 ? driver.findElement(by).getText().trim() : "null";
	}

	public static void main(String[] args) throws IOException {

		Logger.getLogger("").setLevel(Level.OFF);
		p.load(new FileInputStream("input.properties"));
		report = new FileWriter("./report_firefox.csv", false);
		String driverPath = "";
		if (System.getProperty("os.name").toUpperCase().contains("MAC")
				|| System.getProperty("os.name").toUpperCase().contains("LINUX"))
			driverPath = "/usr/local/bin/geckodriver.sh";
		else if (System.getProperty("os.name").toUpperCase().contains("WINDOWS"))
			driverPath = "c:\\windows\\geckodriver.exe";
		else
			throw new IllegalArgumentException("Browser dosn't exist for this OS");
		System.setProperty("webdriver.gecko.driver", driverPath);
		driver = new FirefoxDriver();

        Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
		String browserName = cap.getBrowserName();
		browserName = browserName.substring(0, 1).toUpperCase() + browserName.substring(1);

		driver.manage().window().maximize();
		driver.get(p.getProperty("url"));

		System.out.println("#,Browser,Page,Field,isPresent,Value,Size,Location");

		report.write("#,Browser,Page,Field,isPresent,Value, Size, Location");
		report.write(ls);
		
		String[] idElement = p.getProperty("id").split(",");
		String[] valueElement = p.getProperty("value").split(",");
		
		for (int i = 0; i < idElement.length; i++) 
			setValue(By.id(idElement[i]), valueElement[i]);
			

		String[] idElementIndex = p.getProperty("id_index").split(",");
		String[] nameElementIndex = p.getProperty("name_index").split(",");

		for (int i = 0; i < idElementIndex.length; i++) {
			report.write((i + 1) + "," + browserName + ",index.php," + idElementIndex[i] + ","
					+ isElementPresent(By.id(idElementIndex[i])) + "," + getValue(By.id(idElementIndex[i])) + "," + getSize(By.id(idElementIndex[i]))
					+ "," + getLocation(By.id(idElementIndex[i])) + "\n");
			System.out.print((i + 1) + "," + browserName + ",index.php," + idElementIndex[i] + ","
					+ isElementPresent(By.id(idElementIndex[i])) + "," + getValue(By.id(idElementIndex[i])) + "," + getSize(By.id(idElementIndex[i]))
					+ "," + getLocation(By.id(idElementIndex[i])) + "\n");
		}

		driver.findElement(By.id(p.getProperty("submit_id"))).submit();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.titleIs("Confirmation"));
		
		String[] idElementConf = p.getProperty("id_conf").split(",");
		String[] nameElementConf = p.getProperty("name_conf").split(",");

		for (int i = 0; i < idElementConf.length; i++) {
			report.write((i + 1 + idElementIndex.length) + "," + browserName + ",confirmation.php," + idElementConf[i] + ","
					+ isElementPresent(By.id(idElementConf[i])) + "," + getValue(By.id(idElementConf[i])) + ","
					+ getSize(By.id(idElementConf[i])) + "," + getLocation(By.id(idElementConf[i])) + "\n");
			System.out.print((i + 1 + idElementIndex.length) + "," + browserName + ",confirmation.php," + idElementConf[i]
					+ "," + isElementPresent(By.id(idElementConf[i])) + "," + getValue(By.id(idElementConf[i])) + ","
					+ getSize(By.id(idElementConf[i])) + "," + getLocation(By.id(idElementConf[i])) + "\n");
		}
		report.flush();
		report.close();
		driver.quit();
	}

}
