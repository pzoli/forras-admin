package selenium;

import java.awt.Robot;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

@RunWith(JUnit4.class)
public class CreateClient {
    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        //driver = new FirefoxDriver();
        // https://github.com/SeleniumHQ/selenium/wiki/ChromeDriver
        // java -Dwebdriver.chrome.driver=c:\temp\webdriver\chromedriver.exe -jar selenium-server-standalone-2.53.0.jar
        //driver = new RemoteWebDriver(new URL("http://localhost:9515"), DesiredCapabilities.chrome());
        //System.setProperty("webdriver.firefox.bin","C:\\Users\\AppData\\Local\\Mozilla Firefox\\firefox.exe");
        //System.setProperty("webdriver.chrome.driver","c:\\temp\\webdriver\\chromedriver.exe");
        driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), DesiredCapabilities.chrome());
        //driver = new ChromeDriver();
        baseUrl = "http://localhost:8080";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        //driver.manage().window().maximize();
    }

    public void testLogIn() throws Exception {
        driver.get(baseUrl + "/forras-admin/user/clients.xhtml");
        driver.findElement(By.id("j_username")).clear();
        //driver.findElement(By.id("j_username")).sendKeys("papp.zoltan@infokristaly.hu");
        driver.findElement(By.id("j_username")).sendKeys("papp.zoltan@integrity.hu");
        driver.findElement(By.id("j_password")).clear();
        driver.findElement(By.id("j_password")).sendKeys("q");
        //driver.findElement(By.id("j_password")).sendKeys("123");
        //driver.findElement(By.id("j_password")).sendKeys("qqqq");
        driver.findElement(By.id("submit")).click();
    }

    @Test
    public void testLogInOut() throws Exception {
        testLogIn();
        new WebDriverWait(driver,1500);
        testLogOut();
    }

    @Test
    public void testCreateClient() throws Exception {
        testLogIn();
        driver.findElement(By.id("lazyForm:tableView:addNewClient")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("editForm:clientDetailsView:nyszam")).clear();
        driver.findElement(By.id("editForm:clientDetailsView:nyszam")).sendKeys("2015/12/KK");
        Thread.sleep(1500);
        driver.findElement(By.id("editForm:clientDetailsView:nev")).clear();
        driver.findElement(By.id("editForm:clientDetailsView:nev")).sendKeys("Homár Tibor");
        Thread.sleep(1500);
        WebElement taj = driver.findElement(By.id("editForm:clientDetailsView:taj"));
        taj.click();
        Thread.sleep(1500);
        // TAJ szám
        SeleniumActions.typeKeys(new Robot(), "123");
        Thread.sleep(500);
        SeleniumActions.typeKeys(new Robot(), "123");
        Thread.sleep(500);
        SeleniumActions.typeKeys(new Robot(), "123");
        Thread.sleep(1500);
        // Felvetel datuma
        driver.findElement(By.xpath("(//button[@type='button'])[2]")).click();
        Thread.sleep(500);
        driver.findElement(By.linkText("1")).click();
        Thread.sleep(1500);
        // ClientType
        driver.findElement(By.id("editForm:clientDetailsView:clientTypeSelect_label")).click();
        Thread.sleep(500);
        driver.findElement(By.id("editForm:clientDetailsView:clientTypeSelect_3")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("editForm:btnSave")).click();
        Thread.sleep(1500);
        driver.findElement(By.linkText("Aktív kliensek")).click();
        Thread.sleep(1500);
        driver.findElement(By.xpath("//div[@id='lazyForm:tableView:ctype_active']/div[3]/span")).click();
        Thread.sleep(1500);
        driver.findElement(By.xpath("//div[@id='lazyForm:tableView:ctype_active_panel']/div/div/div[2]/span")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("lazyForm:tableView:refreshFilterOnActive")).click();
        Thread.sleep(1500);
        //driver.findElement(By.id("lazyForm:tableView:activeClients")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("lazyForm:tableView:lazyTable:nyszamOnActive:filter")).clear();
        driver.findElement(By.id("lazyForm:tableView:lazyTable:nyszamOnActive:filter")).sendKeys("2015/12/KK");
        Thread.sleep(1500);
        driver.findElement(By.xpath("//tbody[@id='lazyForm:tableView:lazyTable_data']/tr/td/div/div[2]/span")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("lazyForm:tableView:deleteActiveClientButton")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("confirmDialogForm:deleteConfirmedButton")).click();
        Thread.sleep(1500);
        driver.findElement(By.linkText("Törölt kliensek")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("lazyForm:tableView:lazyTableForPassivated:nyszamOnDeleted:filter")).clear();
        Thread.sleep(1500);
        driver.findElement(By.id("lazyForm:tableView:lazyTableForPassivated:nyszamOnDeleted:filter")).sendKeys("2015/12/KK");
        Thread.sleep(1500);
        driver.findElement(By.xpath("//tbody[@id='lazyForm:tableView:lazyTableForPassivated_data']/tr/td/div/div[2]/span")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("lazyForm:tableView:deleteClientFinally")).click();
        Thread.sleep(1500);
        testLogOut();
    }
    
    @Test
    public void testCreateUser() throws Exception {
        testLogIn();                
        SeleniumActions.clickSubmenu(driver, "Adatok", "Felhasználók");
        driver.findElement(By.id("lazyForm:newUserButton")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("lazyDialogForm:userName")).clear();
        driver.findElement(By.id("lazyDialogForm:userName")).sendKeys("Bánkeszi Katalin");
        Thread.sleep(1500);
        driver.findElement(By.id("lazyDialogForm:userName")).clear();
        driver.findElement(By.id("lazyDialogForm:userName")).sendKeys("bankeszi.katalin@gmail.com");
        Thread.sleep(1500);
        driver.findElement(By.id("lazyDialogForm:password")).clear();
        driver.findElement(By.id("lazyDialogForm:password")).sendKeys("123");
        Thread.sleep(1500);
        driver.findElement(By.xpath("//div[@id='lazyDialogForm:enabledUser']/div[2]/span")).click();
        driver.findElement(By.id("lazyDialogForm:saveUser")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("lazyForm:lazyTable:username:filter")).clear();
        driver.findElement(By.id("lazyForm:lazyTable:username:filter")).sendKeys("Bánkeszi");
        Thread.sleep(1500);
        driver.findElement(By.xpath("//tbody[@id='lazyForm:lazyTable_data']/tr/td/div/div[2]/span")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("lazyForm:deleteUser")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("confirmDeleteDialogForm:deleteUserConfirmed")).click();
        Thread.sleep(1500);
    }

    @Test
    public void testSubject() throws Exception {
        testLogIn();
        SeleniumActions.clickSubmenu(driver, "Adatok", "Foglalkozások");
        driver.findElement(By.id("newSubjForm:newSubjTitle")).clear();
        driver.findElement(By.id("newSubjForm:newSubjTitle")).sendKeys("Mozaik klub");
        Thread.sleep(1500);
        driver.findElement(By.id("newSubjForm:addSubjectBtn")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("subjForm:subjTable:titleCol:filter")).clear();
        driver.findElement(By.id("subjForm:subjTable:titleCol:filter")).sendKeys("Mozaik klub");
        Thread.sleep(1500);
        driver.findElement(By.xpath("//tbody[@id='subjForm:subjTable_data']/tr/td/div/div[2]/span")).click();
        driver.findElement(By.id("subjForm:deleteSubjBtn")).click();
        Thread.sleep(1500);
        testLogOut();
    }

    @Test
    public void testDoctor() throws Exception {
        testLogIn();
        SeleniumActions.clickSubmenu(driver, "Adatok", "Orvosok");
        driver.findElement(By.id("newDoctorForm:doctorName")).clear();
        driver.findElement(By.id("newDoctorForm:doctorName")).sendKeys("Dr. Magyar Iván György");
        driver.findElement(By.id("newDoctorForm:addBtn")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("DoctorForm:DoctorTable:doctorNameCol:filter")).clear();
        driver.findElement(By.id("DoctorForm:DoctorTable:doctorNameCol:filter")).sendKeys("Dr. Magyar Iván György");
        Thread.sleep(1500);
        driver.findElement(By.xpath("//tbody[@id='DoctorForm:DoctorTable_data']/tr/td/div/div[2]/span")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("DoctorForm:deleteDoctorBtn")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("DoctorForm:DoctorTable:doctorNameCol:filter")).clear();        
        Thread.sleep(1500);
        testLogOut();
    }

    @Test
    public void testGroups() throws Exception {
        testLogIn();
        SeleniumActions.clickSubmenu(driver, "Adatok", "Csoportjaim");
        driver.findElement(By.id("lazyForm:newGroupBtn")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("lazyDialogForm:groupName")).clear();
        driver.findElement(By.id("lazyDialogForm:groupName")).sendKeys("Mozaik klub");
        Thread.sleep(1500);
        driver.findElement(By.xpath("//div[@id='lazyDialogForm:pickList']/div[2]/ul/li/table/tbody/tr/td")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("lazyDialogForm:pickList_source_filter")).clear();
        driver.findElement(By.id("lazyDialogForm:pickList_source_filter")).sendKeys("b");
        Thread.sleep(1500);
        driver.findElement(By.xpath("(//button[@type='button'])[5]")).click();
        Thread.sleep(1500);
        driver.findElement(By.xpath("//div[@id='lazyDialogForm:pickList']/div[2]/ul/li/table/tbody/tr/td")).click();
        Thread.sleep(1500);
        driver.findElement(By.xpath("(//button[@type='button'])[5]")).click();
        Thread.sleep(1500);

        driver.findElement(By.id("lazyDialogForm:saveBtn")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("lazyForm:lazyTable:nameCol:filter")).clear();
        driver.findElement(By.id("lazyForm:lazyTable:nameCol:filter")).sendKeys("mozaik");
        Thread.sleep(1500);
        driver.findElement(By.xpath("//tbody[@id='lazyForm:lazyTable_data']/tr/td/div/div[2]/span")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("lazyForm:deleteBtn")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("confirmDialogForm:confirmedDeleteBtn")).click();
        testLogOut();
    }
    
    @Test
    public void testAdminLessons() throws Exception {
        testLogIn();
        SeleniumActions.clickSubmenu(driver, "Adminisztráció", "Foglalkozásaim");
        Thread.sleep(1500);
        driver.findElement(By.xpath("//div[@id='ScheduleForm:schedule_container']/div[2]/div/table/tbody/tr/td/div/div/div[2]/div/table/tbody/tr/td[2]")).click();
        Thread.sleep(1500);
        driver.findElement(By.xpath("//div[@id='timerForm:clientsTabs:subjectSelect']/div[3]")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("timerForm:clientsTabs:subjectSelect_2")).click();
        Thread.sleep(1500);
        driver.findElement(By.xpath("//a[contains(text(),'Kliensek')]")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("timerForm:clientsTabs:groupSelect_label")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("timerForm:clientsTabs:groupSelect_1")).click();
        Thread.sleep(1500);
        driver.findElement(By.xpath("(//button[@type='button'])[6]")).click();
        Thread.sleep(1500);
        driver.findElement(By.xpath("//a[contains(text(),'Foglalkozás alapadatok')]")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("timerForm:j_id_2f")).click();
        Thread.sleep(1500);
        driver.findElement(By.xpath("//div[@id='ScheduleForm:schedule_container']/div[2]/div/table/tbody/tr/td/div/div/div[2]/div[2]/table/tbody/tr/td[2]/a/div")).click();
        Thread.sleep(1500);
        driver.findElement(By.id("timerForm:j_id_2h")).click();
        Thread.sleep(1500);
        testLogOut();
    }
    
    public void testLogOut() throws Exception {
        WebElement element = driver.findElement(By.linkText("Rendszer"));
        Actions action = new Actions(driver);
        action.moveToElement(element).perform();
        Thread.sleep(1500);
        WebElement subElement = driver.findElement(By.linkText("Kilépés"));
        action.moveToElement(subElement);
        action.click();
        action.perform();
        Thread.sleep(1000);
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    private String closeAlertAndGetItsText() {
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
}
