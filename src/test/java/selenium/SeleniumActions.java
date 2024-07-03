package selenium;

import static org.junit.Assert.assertNotNull;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class SeleniumActions {

	public static WebElement findElement(WebDriver driver, By by) {
		return findElement(driver, by);
	}

	public static WebElement findElement(SearchContext container, By by) {
		boolean wait = true;
		WebElement e = wait ? waitElement(container, by) : container
				.findElement(by);
		assertNotNull(e);
		return e;
	}

	public static WebElement waitElement(SearchContext container, By by) {
		WebElement we = null;

		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// no way
		}

		long end = System.currentTimeMillis() + 5000;
		while (System.currentTimeMillis() < end) {
			we = container.findElement(by);
			if (we != null) {
				break;
			}
		}
		assertNotNull(we);
		return we;
	}

	public static void clickButtonByText(WebDriver driver, String text) {
		
	}

	public static void clickButtonByText(SearchContext container, String text) {
		WebElement element = findElement(container,
				By.xpath("//button[contains(., '" + text + "')]"));
		element.click();
	}

	public static void clickLinkByText(WebDriver driver, String text) {
		clickLinkByText(driver, text);
	}

	public static void clickLinkByText(SearchContext container, String text) {
		WebElement element = findElement(container, By.partialLinkText(text));
		element.click();
	}

	public static void clickLinkByTitle(WebDriver driver, String string) {
		// http://stackoverflow.com/questions/6554067/xpath-1-0-closest-preceding-and-or-ancestor-node-with-an-attribute-in-a-xml-tree
		WebElement element = findElement(driver,
				By.xpath("//ancestor::*[img[@title='English']]"));
		String tagname = element.getTagName();
		element.click();
		// element.sendKeys(Keys.RETURN);
		// JavascriptExecutor executor = (JavascriptExecutor) driver;
		// executor.executeScript("arguments[0].click();", element);
		// executor.executeScript("PrimeFaces.addSubmitParam('j_idt11',{'j_idt11:j_idt16':'j_idt11:j_idt16'}).submit('j_idt11');return false;");
	}

	public static void clickMenuById(WebDriver driver, String id) {
	    WebElement element = driver.findElement(By.xpath("//*[@id=\""+id+"\"]/a"));
	    if (element != null) {
	        element.click();
	    }
	  
	}
	
	public static WebElement showMainMenu(WebDriver driver, String menu) {
        WebElement mainMenu = driver.findElement(By.xpath("//span[text()='"+menu+"']"));
        Actions builder = new Actions(driver);
        builder.clickAndHold(mainMenu).moveByOffset(5, 5).build().perform();
        return mainMenu;
	}
	
	public static void clickSubmenu(WebDriver driver, String main, String sub) {
        WebElement element = driver.findElement(By.linkText(main));
        Actions action = new Actions(driver);
        action.moveToElement(element).perform();
        WebElement subElement = driver.findElement(By.linkText(sub));
        action.moveToElement(subElement);
        action.click();
        action.perform();
	}
	
	public static void clickMenuById(SearchContext container, String id) {
		WebElement element = findElement(container,
				By.xpath("//*[@id=\"" + id + "\"]/a"));
		element.click();
	}

	public static boolean isElementPresent(WebDriver driver, By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public boolean isAlertPresent(WebDriver driver) {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	public String closeAlertAndGetItsText(WebDriver driver,
			boolean acceptNextAlert) {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			if (acceptNextAlert) {
				alert.accept();
			} else {
				alert.dismiss();
			}
			return alertText;
		} finally {
			acceptNextAlert = true;
		}
	}

	public static void pressTabKey(Robot robot) {
		robot.keyPress(KeyEvent.VK_TAB);
		robot.keyRelease(KeyEvent.VK_TAB);
	}

	public static void typeKeys(Robot robot, String text) {
		for (int index = 0; index < text.length(); index++) {
			int keyCode = KeyEvent.getExtendedKeyCodeForChar(text.charAt(index));
			robot.keyPress(keyCode);
			robot.keyRelease(keyCode);
		}
	}

}
