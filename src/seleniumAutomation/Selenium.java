/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seleniumAutomation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

/**
 *
 * @author PMUIR
 */
public class Selenium {

    public EventFiringWebDriver efwd;
    public WebDriver driver;
    public WebElement foundElement;
    public String browser;
    
    
    public void parseTestCase(String testCase) {
        try {
            for (String step : testCase.split(",")) {
                startOfStep();
                if (step.matches("(?i)Initialize.*?")) {
                    InitializeSelenium(step.split("=")[1]);
                } else if (step.matches("(?i)Goto.*?")) {
                    goToURL(step.split("=")[1]);
                } else if (step.matches("(?i)While.*?")) {
                    whileLoop(step.split("=", 2)[1]);
                } else {
                    parseStep(step.split("=")[0].trim(), step.split("=", 2)[1].split("="));
                }
                endOfStep();
            }
        } catch (Exception e) {
            if (UI.Controls.getPlayButton().isSelected()) { 
                UI.Controls.getPlayButton().setSelected(false);
            }
            teardown();
        }
        teardown();
        return;
    }
    
    public void runStep(String step) {
        if (step.matches("(?i)screenshot.*?")) {
            if (step.contains("=")) {
                takeScreenShot(step.split("=")[1]);
            } else {
                takeScreenShot();
            }
        }
        try {
            if (step.contains("=")) {
                startOfStep();
                if (step.matches("(?i)Initialize.*?")) {
                    InitializeSelenium(step.split("=")[1]);
                } else if (step.matches("(?i)Goto.*?")) {
                    goToURL(step.split("=")[1]);
                } else if (step.matches("(?i)While.*?")) {
                    whileLoop(step.split("=", 2)[1]);
                } else {
                    parseStep(step.split("=")[0].trim(), step.split("=", 2)[1].split("="));
                }
                endOfStep();
            }
        } catch (Exception e) {
            if (UI.Controls.getPlayButton().isSelected()) { 
                UI.Controls.getPlayButton().setSelected(false);
            }
        }
        return;
    }
    
    public void startOfStep() {
        
    }
    
    public void endOfStep(){
        
    }
    
    public void teardown() {
        if (this.efwd != null) {
            flowControls.selenium = null;
            this.efwd.close();
            this.efwd.quit();
        }
    }
    
    
    public boolean parseStep(String stepType,String[] step) {
        if (step[0].equalsIgnoreCase("id")) {
            if (stepType.equalsIgnoreCase("click")) {
                clickElementbyID(step[1]);
            } else if (stepType.equalsIgnoreCase("insertText")) {
                insertTextbyID(step[1],step[2]);
            } else if (stepType.equalsIgnoreCase("verify")) {
                return verifyTextbyID(step[1],step[2]);
            } else if (stepType.equalsIgnoreCase("clear")) {
                clearTextbyID(step[1]);
            }
        } else if (step[0].equalsIgnoreCase("linkText")) {
            clickLinkByText(step[1]);
        } 
        return false;
    }
    
    private void takeScreenShot(String file) {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(new File("").getCanonicalPath().toString() + "/ScreenShots/"+file));
        } catch (IOException ex) {
            Logger.getLogger(Selenium.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void takeScreenShot() {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(new File("").getCanonicalPath().toString() + "/ScreenShots/"+new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date()) +efwd.getTitle()+".png"));
        } catch (IOException ex) {
            Logger.getLogger(Selenium.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean whileLoop(String conditions) {
        int i = 0;
        while(parseStep(conditions.split("=")[0].trim(),conditions.split("=",2)[1].split("="))) {
            if (i > 1000) { return false; }
        }
        return true;
    }

    public void InitializeSelenium(String browser) {
        if (efwd != null) {
            efwd.close();
            efwd.quit();
        }
        if (browser.matches("(?i)I.*")) {
            InitializeIE();
        } else if (browser.matches("(?i)F.*")) {
            InitializeFirefox();
        } else if (browser.matches("(?i)C.*")) {
            InitializeChrome();
        } else if (browser.matches("(?i)H.*")) {
            InitializeHUD();
        }
    }

    public void InitializeIE() {
        File file = null;
        try {
            file = new File(new File("").getCanonicalPath().toString() + "/Data/iedriverserver.exe");
        } catch (IOException ex) {
            Logger.getLogger(Selenium.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
        this.driver = null;
        try {
            this.driver = new InternetExplorerDriver();
        } catch (NoClassDefFoundError ex) {
            System.err.println("error: " + ex.getStackTrace());
        }
        this.driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
        this.efwd = new EventFiringWebDriver(driver);
    }

    public void InitializeChrome() {
        File file = null;
        try {
            file = new File(new File("").getCanonicalPath().toString() + "/Data/chromedriver.exe");
        } catch (IOException ex) {
            Logger.getLogger(Selenium.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
        this.driver = null;
        try {
            this.driver = new ChromeDriver();
        } catch (NoClassDefFoundError ex) {
            System.err.println("error: " + ex.getStackTrace());
        }
        this.driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
        this.efwd = new EventFiringWebDriver(driver);
    }

    public void InitializeFirefox() {
        this.driver = null;
        try {
            this.driver = new FirefoxDriver();
        } catch (NoClassDefFoundError ex) {
            System.err.println("error: " + ex.getStackTrace());
        }
        this.driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
        this.efwd = new EventFiringWebDriver(driver);
    }
    
    public void InitializeHUD() {
        this.driver = null;
        try {
            this.driver = new HtmlUnitDriver();
        } catch (NoClassDefFoundError ex) {
            System.err.println("error: " + ex.getStackTrace());
        }
        this.driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
        this.efwd = new EventFiringWebDriver(driver);
    }

    public void goToURL(String URL) {
        if (null == this.efwd || this.efwd.getWindowHandles().isEmpty()) {
            if (this.efwd != null) {
                this.efwd.quit();
            }
            InitializeSelenium("Firefox");
        }
        this.efwd.get(URL);
    }

   

    public void expandAll() {
        for (WebElement el : this.efwd.findElements(By.tagName("legend"))) {
            if (el.isDisplayed()) {
                if (!this.efwd.findElement(By.id(el.getText().replaceAll("\\s", ""))).isDisplayed()) {
                    el.click();
                }
            }
        }
    }




    public void insertTextbyID(String id, String Text) {
        expandAll();
        this.foundElement = null;
        List<WebElement> elements = this.efwd.findElements(By.id(id));
        if (!elements.isEmpty()) {
            this.foundElement = elements.get(0);
            this.foundElement.clear();
            this.foundElement.sendKeys(Text);
        }
        if (this.foundElement == null) {
            List<WebElement> frames = this.efwd.findElements(By.tagName("iframe"));
            String mainFrame = this.efwd.getWindowHandle();
            for (WebElement frame : frames) {
                this.efwd.switchTo().frame(frame);
                elements = this.efwd.findElements(By.id(id));
                if (!elements.isEmpty()) {
                    this.foundElement = elements.get(0);
                }
                if (this.foundElement != null) {
                    this.foundElement.clear();
                    this.foundElement.sendKeys(Text);
                }
            }
            this.efwd.switchTo().window(mainFrame);
        }
    }
    
    public void clickLinkByText(String text) {
        List<WebElement> frames = this.efwd.findElements(By.tagName("iframe"));
        String mainFrame = this.efwd.getWindowHandle();
        List<WebElement> elements = this.efwd.findElements(By.linkText(text));
        boolean notFound = true;
        if (!elements.isEmpty()) {
            notFound = false;
            elements.get(0).click();
        }
        if (notFound) {
            for (WebElement frame : frames) {
                this.efwd.switchTo().frame(frame);
                elements = this.efwd.findElements(By.linkText(text));
                if (!elements.isEmpty()) {
                    elements.get(0).click();
                }
            }
            this.efwd.switchTo().window(mainFrame);
        }
    }
    
    public void clearTextbyID(String id) {
        expandAll();
        this.foundElement = null;
        List<WebElement> elements = this.efwd.findElements(By.id(id));
            if (!elements.isEmpty()) {
                this.foundElement = elements.get(0);
            }
            if (this.foundElement != null) {
                this.foundElement.clear();
        }
        if (this.foundElement == null) {
            List<WebElement> frames = this.efwd.findElements(By.tagName("iframe"));
            String mainFrame = this.efwd.getWindowHandle();
            for (WebElement frame : frames) {
                this.efwd.switchTo().frame(frame);
                elements = this.efwd.findElements(By.id(id));
                if (!elements.isEmpty()) {
                    this.foundElement = elements.get(0);
                }
                if (this.foundElement != null) {
                    this.foundElement.clear();
                }
            }
            this.efwd.switchTo().window(mainFrame);
        }
    }
    
    public boolean verifyTextbyID(String id,String Text) {
        expandAll();
        this.foundElement = null;
        List<WebElement> elements = this.efwd.findElements(By.id(id));
            if (!elements.isEmpty()) {
                this.foundElement = elements.get(0);
            }
            if (this.foundElement != null) {
                return this.foundElement.getText().matches(Text);
            }
        if (this.foundElement == null) {
            List<WebElement> frames = this.efwd.findElements(By.tagName("iframe"));
            String mainFrame = this.efwd.getWindowHandle();
            for (WebElement frame : frames) {
                this.efwd.switchTo().frame(frame);
                elements = this.efwd.findElements(By.id(id));
                if (!elements.isEmpty()) {
                    this.foundElement = elements.get(0);
                }
                if (this.foundElement != null) {
                    return this.foundElement.getText().matches(Text);
                }
            }
            this.efwd.switchTo().window(mainFrame);
        }
        return false;
    }

    public void clickElementbyID(String id) {
        expandAll();
        List<WebElement> frames = this.efwd.findElements(By.tagName("iframe"));
        String mainFrame = this.efwd.getWindowHandle();
        List<WebElement> elements = this.efwd.findElements(By.id(id));
        boolean notFound = true;
        if (!elements.isEmpty()) {
            notFound = false;
            elements.get(0).click();
        }
        if (notFound) {
            for (WebElement frame : frames) {
                this.efwd.switchTo().frame(frame);
                elements = this.efwd.findElements(By.id(id));
                if (!elements.isEmpty()) {
                    elements.get(0).click();
                }
            }
            this.efwd.switchTo().window(mainFrame);
        }
    }
    
    
}
