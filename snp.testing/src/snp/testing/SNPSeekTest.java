/*
Program Description:
A program that auto-tests the SNP-Seek User Interface and checks query results. The covered parts in this test are the following: Variety page, Genotype page (Table, Haplotype View, Pairwise), Gene Loci (Sequence page) 

Author's Note:
Things that needs to be hard coded:
- Directory where file will be unzipped (dirUnzipped)
- Directory where the orginal file to be compared to is located (dirOriginal)
- Directory where the file goes after downloading (directory)
- Type of Haplotype Download
- directory of json file
- File path of the downloaded file and the expected file (expectedpath and downloadedpath) for haplotype
 */


package snp.testing;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.jetty.websocket.common.io.http.HttpResponseHeaderParser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.AWTException;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class SNPSeekTest {
	//attributes, input the value to the proper textbox then click search
	static WebDriver driver;
	static FirefoxProfile fp;
	static int testcount = 0;
	static Boolean flag = true;

	public static void main(String[] args) throws AWTException, NoSuchAlgorithmException, InterruptedException, InvocationTargetException {
		//path of the json file
		String filename = "/home/lbgo/Desktop/WorkspaceN/snp.testing/snptest.json";
		
		try{
			//read the JSON file
			JsonParser parser = new JsonParser();
			
			Object obj = parser.parse(new FileReader(filename));
			JsonArray jsonArray = (JsonArray) obj;

			for (JsonElement element : jsonArray) {
			    JsonObject jsonObject = element.getAsJsonObject();

			    //get test type
			    String testType = jsonObject.get("test").getAsString();
			    
			    //testing pairwise table
			    if(testType.equals("snppairwise")){			    	
			    	//get the parameters of snppairwise, check first if empty
			    	String expected = "", header = "", variety1 = "", variety2 = "", chromosome = "", start = "", end = "", header2 = "";
			    	Boolean mismatchonly = false;
			    	
			    	if (jsonObject.has("expected")) {
			    		expected = jsonObject.get("expected").getAsString();
			    	}
			    	if (jsonObject.has("header")) {
			    		header = jsonObject.get("header").getAsString();
			    	}
			    	if (jsonObject.has("variety1")) {
			    		variety1 = jsonObject.get("variety1").getAsString();
			    	}
			    	if (jsonObject.has("variety2")) {
			    		variety2 = jsonObject.get("variety2").getAsString();
			    	}
			    	if (jsonObject.has("chromosome")) {
			    		chromosome = jsonObject.get("chromosome").getAsString();
			    	}
			    	if (jsonObject.has("start")) {
			    		start = jsonObject.get("start").getAsString();
			    	}
			    	if (jsonObject.has("end")) {
			    		end = jsonObject.get("end").getAsString();
			    	}
			    	if (jsonObject.has("mismatchonly")) {
			    		mismatchonly = jsonObject.get("mismatchonly").getAsBoolean();
			    	}
			    	if (jsonObject.has("header2")) {
			    		header2 = jsonObject.get("header2").getAsString();
			    	}
			    	
			    	startAutomation("http://snp-seek.irri.org/genotype.zul");
			    	getColumnWithHeader(testType, expected, header, variety1, variety2, chromosome, start, end, mismatchonly,header2);
			    	
			    	driver.close();
			    	
			    } else if (testType.equals("snpmatrix")){
			    	//get the parameters of snpmatrix, check first if empty
			    	String expected = "", subpopulation = "", chromosome = "", start = "", end = "", downloadType = "";
			    	Boolean mismatchonly = false, testHaplotype = false;
			    	
			    	if (jsonObject.has("expected")) {
			    		 expected = jsonObject.get("expected").getAsString();
			    	}
			    	if (jsonObject.has("subpopulation")) {
			    		subpopulation = jsonObject.get("subpopulation").getAsString();
			    	}
			    	if (jsonObject.has("chromosome")) {
			    		chromosome = jsonObject.get("chromosome").getAsString();
			    	}
			    	if (jsonObject.has("start")) {
			    		start= jsonObject.get("start").getAsString();
			    	}
			    	if (jsonObject.has("start")) {
			    		end = jsonObject.get("end").getAsString();
			    	}
			    	if (jsonObject.has("mismatchonly")) {
			    		mismatchonly = jsonObject.get("mismatchonly").getAsBoolean();
			    	}
			    	if (jsonObject.has("testHaplotype")) {
			    		testHaplotype = jsonObject.get("testHaplotype").getAsBoolean();
			    	}
			    	if (jsonObject.has("downloadType")) {
			    		downloadType = jsonObject.get("downloadType").getAsString();
			    	}

			    	startAutomation("http://snp-seek.irri.org/genotype.zul");
			    	genotypeTest(testType, expected, subpopulation, chromosome, start, end, mismatchonly, testHaplotype, downloadType);
			    	
			    	driver.close();
			    	
			    } else if (testType.equals("variety")){
			    	//get parameters of variety, check first if empty
			    	String dataset = "", country = "", designation = "", accession = "", subpopulation = "", irisID = "", phenotype = "", phenotype_comp = "", phenotype_comp_value = "";
			    	int expected = 0;
			    	
			    	if (jsonObject.has("dataset")) {
			    		dataset = jsonObject.get("dataset").getAsString();
			    	}
			    	if (jsonObject.has("expected")) {
			    		expected = jsonObject.get("expected").getAsInt();
			    	}
			    	if (jsonObject.has("subpopulation")) {
			    		subpopulation = jsonObject.get("subpopulation").getAsString();
			    	}
			    	if (jsonObject.has("designation")) {
			    		designation = jsonObject.get("designation").getAsString();
			    	}
			    	if (jsonObject.has("accession")) {
			    		accession = jsonObject.get("accession").getAsString();
			    	}
			    	if (jsonObject.has("irisID")) {
			    		irisID = jsonObject.get("irisID").getAsString();
			    	}
			    	if (jsonObject.has("phenotype")) {
			    		phenotype = jsonObject.get("phenotype").getAsString();
			    	}
			    	if (jsonObject.has("country")) {
			    		country = jsonObject.get("country").getAsString();
			    	}
			    	if (jsonObject.has("phenotype_comp")) {
			    		phenotype_comp = jsonObject.get("phenotype_comp").getAsString();
			    	}
			    	if (jsonObject.has("phenotype_comp_value")) {
			    		phenotype_comp_value = jsonObject.get("phenotype_comp_value").getAsString();
			    	}
			    	
			    	startAutomation("http://snp-seek.irri.org/variety.zul");
			    	varietyTest(testType, dataset, designation, accession, subpopulation, irisID,  country, phenotype, phenotype_comp, phenotype_comp_value,expected);
			    	
			    	driver.close();
			    	
			    } else if (testType.equals("locus")){
			    	//get parameters of locus, check first if empty
			    	String expected = "", querytype = "", databasetype = "", maxevalue = "", sequence = "";
			    	
			    	if (jsonObject.has("expected")) {
			    		expected = jsonObject.get("expected").getAsString();
			    	}
			    	if (jsonObject.has("querytype")) {
			    		querytype = jsonObject.get("querytype").getAsString();
			    	}
			    	if (jsonObject.has("databasetype")) {
			    		databasetype = jsonObject.get("databasetype").getAsString();
			    	}
			    	if (jsonObject.has("maxevalue")) {
			    		maxevalue= jsonObject.get("maxevalue").getAsString();
			    	}
			    	if (jsonObject.has("sequence")) {
			    		sequence = jsonObject.get("sequence").getAsString();
			    	}
			    	
			    	startAutomation("http://snp-seek.irri.org/locus.zul");
			    	testGeneLoci(expected, querytype, maxevalue, databasetype, sequence);
			    	
			    	driver.close();
			    	
			    }
			}			
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
        	e.printStackTrace();
        }
		
	}
	
	//------------------------------------- SNP PAIRWISE TEST ----------------------------------------//
	public static void getColumnWithHeader(String testtype, String expected, String header, String variety1, String variety2, String chromosome, String start, String end, Boolean mismatchonly, String header2){
		int column = 0;
		
		//assign the value to first variety
		if(variety1 != null && !variety1.isEmpty()){
			WebElement input_pairwise1 = driver.findElement(By.cssSelector(".pairwise1.z-combobox > input"));
			input_pairwise1.clear();
			input_pairwise1.sendKeys(variety1);
		}
		
		//assign the value to first variety
		if(variety2 != null && !variety2.isEmpty()){
			WebElement input_pairwise2 = driver.findElement(By.cssSelector(".pairwise2.z-combobox > input"));
			input_pairwise2.clear();
			input_pairwise2.sendKeys(variety2);
		}
		
		//assign values to chromosome
		if(variety2 != null && !variety2.isEmpty()){
			WebElement input_chromosome = driver.findElement(By.cssSelector(".z-chromosome-input.z-combobox > input"));
			input_chromosome.clear();
			input_chromosome.sendKeys(chromosome);
		}
		
		//assign start values
		if(start!= null && !start.isEmpty()){
			WebElement input_start = driver.findElement(By.cssSelector(".z-start-input.z-intbox"));
			input_start.clear();
			input_start.sendKeys(start);
		}
			
		//assign end values
		if(end != null && !end.isEmpty()){
			WebElement input_end = driver.findElement(By.cssSelector(".z-end-input.z-intbox"));
			input_end.clear();
			input_end.sendKeys(end);
		}
		
		//click the mismatch button
		if(mismatchonly == true){
			if (!driver.findElement(By.cssSelector(".z-mismatch-option.z-checkbox > input")).isSelected()){
			     driver.findElement(By.cssSelector(".z-mismatch-option.z-checkbox > input")).click();
			}
		}
		
		//click the search button
		WebElement element = driver.findElement(By.cssSelector(".z-search-button.z-button"));
		element.click();	
		
		//check if pop up message occured and catch
		if(isAlertPresent(".z-messagebox-button.z-button")){ } 
		else {
			//wait for the pairwise table to appear
			try{
				WebDriverWait wait = new WebDriverWait(driver, 20);
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".pairwise-table.z-listbox")));	
			} catch (Exception e) {}
			
			WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".haplotype-table.z-listhead")));
			
			//CODE FOR COUNTING ROWS
			WebElement eleParentx = driver.findElement(By.cssSelector(".pairwise-table.z-listbox"));
			List <WebElement> pairwiseTable = eleParentx.findElements(By.cssSelector("div:nth-child(4) > table > tbody > tr"));
			int rowcount = pairwiseTable.size() - 1;
			checkRows(rowcount,Integer.parseInt(expected), testtype);
			
			//IF MISMATCH IS TRUE CHECK IF THERE IS MATCH ON RESULT
			if(mismatchonly == true){
				//get the variety names
				String[] pair1 = getData(header);
				String[] pair2 = getData(header2);
				
				if(pair1.equals(pair2)) justAppend("ERROR ON MISMATCH: Found Match \n");
				else justAppend("NO ERROR ON MISMATCH: No match found \n");
			}
		}
	}
	
	public static String[] getData(String header){
		int column = 0;
		
		//get the column header
		//access the headers
		if(driver.findElement(By.cssSelector(".haplotype-table.z-listhead")).isDisplayed()){
			WebElement eleParent = driver.findElement(By.cssSelector(".haplotype-table.z-listhead"));
			List <WebElement> headers = eleParent.findElements(By.cssSelector("th"));
			//get the column number of the header
			for(int i = 0; i < headers.size(); i++){
				if((headers.get(i).getText()).equals(header)){
					column = i;
				}
			}
		}
		
		//get the data
		return getColumnData(column);
		
	}

	public static String[] getColumnData(int columnno){
		
		//get all data in this column
		//traverse the rows and get nth element
		WebElement eleParent = driver.findElement(By.cssSelector(".pairwise-table.z-listbox"));
		List <WebElement> pairwisetable = eleParent.findElements(By.cssSelector("div:nth-child(4) > table > tbody > tr"));
		int rowcount = pairwisetable.size()-1;
		String[] columndata = new String[rowcount];
		
		for(int i = 0; i < rowcount; i++){
			//access td of tr
			WebElement current = pairwisetable.get(i);
			WebElement thiselement = current.findElement(By.cssSelector("td:nth-child(" + columnno + ")"));
			columndata[i] = thiselement.getText();
		}
		return columndata;
	}

	
	//--------------------------------------- SNP MATRIX TEST ----------------------------------------//
	public static void genotypeTest(String testtype, String expected, String subpopulation, String chromosome, String start, String end, boolean mismatchonly, Boolean testHaplotype, String downloadType) throws InterruptedException, NoSuchAlgorithmException, IOException{
	
		//assign subpopulation
		if(subpopulation != null && !subpopulation.isEmpty()){
			Select input_subpopulation = new Select(driver.findElement(By.cssSelector(".z-subpopulation-select.z-select")));
			input_subpopulation.selectByVisibleText(subpopulation);
		}
		
		//assign value to chromosome
		if(chromosome != null && !chromosome.isEmpty()){
			WebElement input_chromosome = driver.findElement(By.cssSelector(".z-chromosome-input.z-combobox > input"));
			input_chromosome.clear();
			input_chromosome.sendKeys(chromosome);
		}
		
		//assign start values
		if(start != null && !start.isEmpty()){
			WebElement input_start = driver.findElement(By.cssSelector(".z-start-input.z-intbox"));
			input_start.clear();
			input_start.sendKeys(start);
		}
		
		//assign end values
		if(end != null && !end.isEmpty()){
			WebElement input_end = driver.findElement(By.cssSelector(".z-end-input.z-intbox"));
			input_end.clear();
			input_end.sendKeys(end);
		}
		
		//click the mismatch button
		if(mismatchonly == true){
			if (!driver.findElement(By.cssSelector(".z-mismatch-option.z-checkbox > input")).isSelected()){
			     driver.findElement(By.cssSelector(".z-mismatch-option.z-checkbox > input")).click();
			}
		}
	
		//click the search button
		WebElement element = driver.findElement(By.cssSelector(".z-search-button.z-button"));
		element.click();
		
		//check and catch pop up message
		if(isAlertPresent(".z-messagebox-button.z-button")){ } 
		else {
			//check if test haplotype is true
			if(testHaplotype == true){
				testHaplotype("Image");
			}
			else {
				//parse the expected value
				String[] value = expected.split("x");
				int row = Integer.parseInt(value[0]);
				int col = Integer.parseInt(value[1]);
				//count the rows and columns
				countRowsColumn(expected, row, col, downloadType, testtype);
			}
		}
	}
	
	public static void testHaplotype(String choice) throws NoSuchAlgorithmException, IOException, InterruptedException{
		String dirOriginal = "/home/lbgo/snp3kgroups-LOC_OS01G01010-4528853037671596217.txt"; //path of the original file to be compared to
		
		//wait for the results to appear
		try{
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".biglistbox-table.z-hbox")));	
		} catch (Exception ex) {}
		
		//click haplotype
		WebElement eleParent = driver.findElement(By.cssSelector(".table-navigation-bar.z-tabbox.z-tabbox-top"));
		WebElement clickHaplotype = eleParent.findElement(By.cssSelector("div:nth-child(1) > ul > li:nth-child(5)"));
		clickHaplotype.click();
		
		//wait until pairwise table appears
		//either log or download group alleles
		try{
			WebDriverWait wait = new WebDriverWait(driver, 15);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".z-log-button.z-button")));
		} catch (Exception e) {}
		
		Thread.sleep(10000);
		
		if(driver.findElement(By.cssSelector(".z-log-button.z-button")).isDisplayed()){
			specialResult("snpmatrix - haplotype", "Result is log");
		} else {
			try{
				WebDriverWait wait = new WebDriverWait(driver, 150);
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".z-download-alleles.z-button")));
				
				if(choice.equals("Variety order")){
					WebElement varietybtn = driver.findElement(By.className("z-variety-order"));
					varietybtn.click();
				}else if(choice.equals("Image")){
					WebElement imagebtn = driver.findElement(By.className("z-image"));
					imagebtn.click();
				}else if(choice.equals("Download group alleles")){
					WebElement downloadbutton = driver.findElement(By.cssSelector(".z-download-alleles.z-button"));
					downloadbutton.click();
				}	
			} catch (Exception e) {}
		}	
		
		//hardcode the filename of the downloaded file and the expected file //can still be improved in the future
		String expectedpath = "/home/lbgo/snp3kvars-LOC_OS01G01010-309058736011932701.haplo.zip";
		String downloadedpath = "/home/lbgo/snp3kvars-chr01-2903-10817-2575152409096755698.haplo.zip";
		
		//check if file exists by comparing using checksum	
		String expected = checkSum(expectedpath);
		String result = checkSum(downloadedpath);
		
		if(expected.equals(result)) specialResult("snpmatrix - haplotype","No Error");
		else specialResult("snpmatrix - haplotype","Error: Files not equal");
	}
	
	public static void countRowsColumn(String expected, int totalrows, int totalcols, String downloadType, String testtype) throws InterruptedException, NoSuchAlgorithmException, IOException{
		Boolean isDownloadable = true;
		
		//check if result is table or downloadable file
		try{
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".download-only-message.z-vbox")));		
		} catch (Exception e) {}
		
		if(!driver.findElement(By.cssSelector(".download-only-message.z-vbox")).isDisplayed()){
			try{
				WebDriverWait wait = new WebDriverWait(driver, 30);
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".biglistbox-table.z-hbox")));	
			} catch (Exception ex) {}
			isDownloadable = false;
		}
		
		//results show
		if(isDownloadable == false){
			//click the end button to reach the end > detect the last element > go back up > scroll until last element is found
			int cols = autoscrollcolumns();
			JavascriptExecutor jse = (JavascriptExecutor)driver;
			jse.executeScript("window.scrollBy(0,-250)", "");
			int rows = autoscrollrows();
			
			//check results
			checkRowsCols(testtype, rows, totalrows, cols, totalcols);
		} 
		//if downloadable result
		else {
			performDownload(expected, testtype, downloadType);
		}
	}
	
	public static int autoscrollrows() throws InterruptedException{
		//try class name, click end, get style top value, click home, then count row
		int count = 0, page_size, i = 0;
		Boolean flag = false;
		
		//access the scroll bar	
		WebElement down = driver.findElement(By.className("z-biglistbox-wscroll-down"));
		WebElement eleParent=driver.findElement(By.cssSelector(".biglistbox-table.z-hbox"));
		List <WebElement> pagination= eleParent.findElements(By.cssSelector("tbody.z-rows > tr > td:nth-child(1)"));
		Actions actions = new Actions(driver);
		
		//if scroll bar not available
		WebElement drag = driver.findElement(By.className("z-biglistbox-wscroll-drag"));
		if(!drag.isDisplayed()){
			i = pagination.size();
			while(i > 0){
				count++;
				i--;
			}
		} else {
			page_size = pagination.size();
			while(flag != true){
				WebDriverWait wait = new WebDriverWait(driver, 10);
				eleParent=driver.findElement(By.cssSelector(".biglistbox-table.z-hbox"));
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".biglistbox-table.z-hbox")));
				try{
					//check if last set of rows
					if(pagination.size() >=  page_size){
						//click the down arrow
						Point item =driver.findElement(By.className("z-biglistbox-wscroll-down")).getLocation();
						((JavascriptExecutor)driver).executeScript("window.scrollBy("+(item.getY())+",0);");
						actions.moveToElement(down).click().build().perform();	
							
						Thread.sleep(100);
						eleParent=driver.findElement(By.cssSelector(".biglistbox-table.z-hbox"));
						pagination= eleParent.findElements(By.cssSelector("tbody.z-rows > tr > td:nth-child(1)"));
						
						count++;
					} else {
						//reference: https://stackoverflow.com/questions/26498299/how-to-loop-through-listwebelement
						//iterate on all elements of pagination
						i = pagination.size();
						while(i > 0){
							count++;
							i--;
						}
						flag = true;					
					}
					
				}  catch (StaleElementReferenceException elementHasDisappeared){
					//exception for when the element disappeared
				}
			}	
		}
		return count;
		
	}
	
	public static int autoscrollcolumns() throws InterruptedException{
		//try class name, click end, get style top value, click home, then count row
		int count = 0, toAdd = 0;
		Boolean flag = false;
		String strcurrlast;
		
		WebElement eleParent=driver.findElement(By.cssSelector(".z-biglistbox-one.z-biglistbox"));
		List<WebElement> pagination = eleParent.findElements(By.cssSelector("tbody > tr:nth-child(1) > th"));
		List<WebElement> rightbuttons = driver.findElements(By.className("z-biglistbox-wscroll-down"));
		List<WebElement> endbuttons = driver.findElements(By.className("z-biglistbox-wscroll-end"));
		List<WebElement> homebuttons = driver.findElements(By.className("z-biglistbox-wscroll-home"));
		List<WebElement> dragbuttons = driver.findElements(By.className("z-biglistbox-wscroll-drag"));
				
		//access the scroll bar	;
		WebElement right = rightbuttons.get(1);
		WebElement end = endbuttons.get(1);
		WebElement home = homebuttons.get(1);
		WebElement drag = dragbuttons.get(1);
		Actions actions = new Actions(driver);		

		WebElement element = Iterables.getFirst(pagination, eleParent);		
		Thread.sleep(2000);		//DON'T REMOVE
	
		//click the end
		Point item = endbuttons.get(1).getLocation();
		((JavascriptExecutor)driver).executeScript("window.scrollBy(0,"+(item.getY())+");");
		actions.moveToElement(end).click().build().perform();	
	
		Thread.sleep(2000);		//DON'T REMOVE
		
		//get last element
		eleParent=driver.findElement(By.cssSelector(".z-biglistbox-one.z-biglistbox"));
		pagination= eleParent.findElements(By.cssSelector("tbody > tr:nth-child(1) > th"));
		WebElement last = Iterables.getLast(pagination, eleParent);
		String strlast = last.getText();
			
		//go home
		item = homebuttons.get(1).getLocation();
		((JavascriptExecutor)driver).executeScript("window.scrollBy(0,"+(item.getY())+");");
		actions.moveToElement(home).click().build().perform();	
			
		while(flag != true){
				
			try{
				eleParent=driver.findElement(By.cssSelector(".z-biglistbox-one.z-biglistbox"));
				pagination = eleParent.findElements(By.cssSelector("tbody > tr:nth-child(1) > th"));
				element = Iterables.getFirst(pagination, eleParent);
				WebElement lastelement = Iterables.getLast(pagination, eleParent);
				strcurrlast = lastelement.getText();
				
				java.util.Iterator<WebElement> i = pagination.iterator();
				
				while(i.hasNext()) {
				    WebElement row = i.next();
				    Thread.sleep(10);
					}
					
					if(strcurrlast.equals(strlast)){
						eleParent=driver.findElement(By.cssSelector(".z-biglistbox-one.z-biglistbox"));
						pagination = eleParent.findElements(By.cssSelector("tbody > tr:nth-child(1) > th"));
						toAdd = pagination.size();
						flag = true;
					}
					
					
					item = rightbuttons.get(1).getLocation();
					((JavascriptExecutor)driver).executeScript("window.scrollBy(0,"+(item.getY())+");");
					actions.moveToElement(right).click().build().perform();
	
					 count++;
				} catch (StaleElementReferenceException elementHasDisappeared){	}	
			}
			return (count - 7 + toAdd);	
	}
	
	public static void performDownload(String expected, String testtype, String type) throws InterruptedException, NoSuchAlgorithmException, IOException{
		String varxpath = "/html/body/div/div[3]/div/div[2]/div[2]/div[1]/table/tbody/tr/td/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[5]/td/table/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr/td[5]/a";
		String buttonpath = "/html/body/div/div/div[2]/div/div/div/div/div/div/div/table[2]/tbody/tr/td/table/tbody/tr/td[5]/button";
		int columns = 0, rowsx = 0;
		
		String dirUnzipped = "/home/lbgo/Testing"; //directory of where file will be unzipped
		String dirOriginal = "/home/lbgo/snp3kvars-chr01-500-100700-1730873677796278111.csv.summary.txt"; //directory of the orginal file which will be compared to the downloaded file
		String directory = "/home/lbgo/"; //where the file is downloaded
		
		//access the type
		if(type.equals("CSV")){
			driver.findElement(By.cssSelector(".z-download-csv.z-button")).click();
		} else if (type.equals("Tab")){
			driver.findElement(By.cssSelector(".z-download-tab.z-button")).click();
		} else if (type.equals("Plink")){
			driver.findElement(By.cssSelector(".z-download-plink.z-button")).click();
		} else if (type.equals("Flapjack")){
			driver.findElement(By.cssSelector(".z-download-flapjack.z-button")).click();
		}
		
		WebDriverWait waiting = new WebDriverWait(driver, 5);
		waiting.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(varxpath)));
		
		//reference: https://stackoverflow.com/questions/9588827/how-to-switch-to-the-new-browser-window-which-opens-after-click-on-the-button
		//reload page
		driver.findElement((By.xpath(varxpath))).click();
		for(String winHandle : driver.getWindowHandles()){
		    driver.switchTo().window(winHandle);
		}
		
		//wait until an element shows
		WebDriverWait waitelement = new WebDriverWait(driver, 10);
		waitelement.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".z-window.z-window-noborder.z-window-noheader.z-window-embedded")));
		
		WebElement eleParent=driver.findElement(By.cssSelector(".z-window.z-window-noborder.z-window-noheader.z-window-embedded"));
		List <WebElement> btnpath = eleParent.findElements(By.cssSelector("div > div > span"));
		WebElement btn = Iterables.getLast(btnpath, driver.findElement(By.cssSelector("div > div > span")));
		WebElement label = driver.findElement(By.cssSelector(".z-label-message.z-label"));
		String[] splitedx = label.getText().split("\\s+");
		String split = splitedx[3].substring(0, splitedx[3].length() - 1);
		
		StringBuilder sb = new StringBuilder(split);
		sb.append(".zip");
		String filename = sb.toString();
		
		//wait for download button to appear
		//reload page
		WebElement downloadbutton;
		while(true){
			eleParent=driver.findElement(By.cssSelector(".z-window.z-window-noborder.z-window-noheader.z-window-embedded"));
			waitelement.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".z-window.z-window-noborder.z-window-noheader.z-window-embedded")));
			//check if button exists
			
			downloadbutton = driver.findElement(By.xpath(buttonpath));
			if(downloadbutton.isDisplayed())break;
			else {
				Thread.sleep(15000);
				driver.navigate().refresh();
			}
		}

		downloadbutton.click();	//author's note: firefox must enable download automatically
		Thread.sleep(5000);
		
		//extract downloaded file to specific directory
		unzipFile(directory + filename, dirUnzipped);
		
		//get the summary file
		sb = new StringBuilder(split);
		sb.append(".summary.txt");
		String txtfile = sb.toString();
		
		//get checksum of file
		String originalfile = checkSum(dirOriginal);	
		String result = checkSum(dirUnzipped + "/" + txtfile);
		
		//compare checkSum() result and the file
		if(originalfile.equals(result)){
			//count rows and columns
			StringBuilder string = readFile(dirOriginal);
			String[] splited = string.toString().split("\\s+");
			columns = Integer.parseInt(splited[0]);		//counted columns
			rowsx = Integer.parseInt(splited[1]);		//counted rows
			
			//split the expected
			String[] value = expected.split("x");
			int totalrow = Integer.parseInt(value[0]);
			int totalcol = Integer.parseInt(value[1]);
			
			checkRowsCols(testtype, rowsx, totalrow, columns, totalcol);
			
		} else {
			specialResult("snpmatrix - download","ERROR");
		}
	}
	
	//function for reading the file and counting the rows
	public static StringBuilder readFile(String filename){
		int linecount = 0;
		StringBuilder stringBuilder = new StringBuilder();
		
		try {
			File file = new File(filename);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line, line1, line2;
			line1 = bufferedReader.readLine();
			line2 = bufferedReader.readLine();
			
			//count columns
			String[] splited = line2.split("\\s+");
			stringBuilder.append(splited[splited.length-1]);
			
			while ((line = bufferedReader.readLine()) != null) {
				if(line.isEmpty()){}
				else{
					linecount++;
				}
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		linecount++;
		stringBuilder.append(" " + Integer.toString(linecount));
		return stringBuilder;
		
	}
	
	//function to compare files
	public static String checkSum(String filepath) throws NoSuchAlgorithmException, IOException{

	    String datafile = filepath;

	    MessageDigest md = MessageDigest.getInstance("SHA1");
	    FileInputStream fis = new FileInputStream(datafile);
	    byte[] dataBytes = new byte[1024];

	    int nread = 0;

	    while ((nread = fis.read(dataBytes)) != -1) {
	      md.update(dataBytes, 0, nread);
	    };

	    byte[] mdbytes = md.digest();

	    //convert the byte to hex format
	    StringBuffer sb = new StringBuffer("");
	    for (int i = 0; i < mdbytes.length; i++) {
	    	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    
	    return sb.toString();
	  }

	public static void unzipFile(String zipFilePath, String destDirectory) throws IOException{
		File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
	}
	
	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[1024];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
		
	}
	
	//------------------------------------------ VARIETY TEST ----------------------------------------//
	public static void varietyTest(String testtype, String dataset, String designation, String accession, String subpopulation, String irisID,  String country, String phenotype, String phenotype_comp, String phenotype_comp_value, int expected){
		int count = 0;
		
		//check if there is value and assign dataset 
		if(dataset != null && !dataset.isEmpty()){
			Select input_dataset = new Select(driver.findElement(By.cssSelector(".z-dataset-dropdown.z-select")));
			input_dataset.selectByVisibleText(dataset);
		}
		
		//check if there is value and assign designation 
		if(designation != null && !designation.isEmpty()){
			WebElement input_designation = driver.findElement(By.cssSelector(".z-combobox-designation.z-combobox > input"));
			input_designation.clear();
			input_designation.sendKeys(designation);
		}
		//check if there is value and assign accession 
		if(accession != null && !accession.isEmpty()){
			WebElement input_accession= driver.findElement(By.cssSelector(".z-combobox-accession.z-combobox > input"));
			input_accession.clear();
			input_accession.sendKeys(accession);
		}
		
		//check if there is value and assign subpopulation 
		if(subpopulation != null && !subpopulation.isEmpty()){
			Select input_subpopulation = new Select(driver.findElement(By.cssSelector(".z-subpopulation-dropdown.z-select")));
			input_subpopulation.selectByVisibleText(subpopulation);
		}
		
		//check if there is value and assign irisID 
		if(irisID != null && !irisID.isEmpty()){
			WebElement input_irisID= driver.findElement(By.cssSelector(".z-combobox-irisid.z-combobox > input"));
			input_irisID.clear();
			input_irisID.sendKeys(irisID);
		}
		
		//check if there is value and assign iriscountry 
		if(country != null && !country.isEmpty()){
			WebElement input_country= driver.findElement(By.cssSelector(".z-combobox-country.z-combobox > input"));
			input_country.clear();
			input_country.sendKeys(country);
		}
		
		//check if there is value and assign phenotype details 
		if(phenotype != null && !phenotype.isEmpty()){
			Select input_phenotype = new Select(driver.findElement(By.cssSelector(".z-phenotype-ontology.z-select")));
			input_phenotype.selectByVisibleText(phenotype);
			
			/* insert code for checking id the values of the drop down are correct */
			WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.numberOfElementsToBeLessThan(By.cssSelector(".z-phenotype-dropdown.z-select>option"), 3));
			
			Select iselect = new Select(driver.findElement(By.cssSelector(".z-phenotype-dropdown.z-select")));
			String value = iselect.getFirstSelectedOption().getText();

			//choose equality option
			Select equality_dropdown = new Select(driver.findElement(By.cssSelector(".z-phenotype-check.z-select")));
			equality_dropdown.selectByVisibleText(phenotype_comp);
			
			//choose sub-option option
			Select option_dropdown = new Select(driver.findElement(By.cssSelector(".z-phenotype-empty.z-select")));
			option_dropdown.selectByVisibleText(phenotype_comp_value);
		}
		
		//click the search button
		WebElement element = driver.findElement(By.cssSelector(".z-selectbutton.z-button"));
		element.click();	
	
		//check if pop up occurred, check the first instance
		isAlertPresent(".z-messagebox-button.z-button");
		
		//grid table appeared, 1 row
		if(driver.findElement(By.cssSelector(".gridview-visible.z-grid")).isDisplayed()){
			count = 1;
		} 			
		//if list table appeared, multiple rows
		if(driver.findElement(By.cssSelector(".listview-visible.z-listbox")).isDisplayed()){
			
			//check if still one output
			if(driver.findElement(By.cssSelector(".designation-content.z-textbox.z-textbox-readonly")).isDisplayed()){
				count = 1;
			} else {
				//wait for the table to appear
				WebDriverWait waits = new WebDriverWait(driver, 10);
				waits.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".listview-visible.z-listbox")));
				
				count = totalRows();
			}
		} 
		checkRows(count, expected, testtype);	
	}
	
	//function for counting the number of rows if multiple results
	public static int totalRows(){
		int rows = 0, count = 0;
		
		//get number of pages
		int pages = checkPagination();
		
		//if pages == 0 : single page web site else otherwise
		if(pages == 0){
			//count the total rows 
			rows = StartRowCount();
		} else if (pages > 0){
			//click the first button
			WebElement eleParent=driver.findElement(By.cssSelector(".listview-visible.z-listbox"));
			List <WebElement> pagination= eleParent.findElements(By.cssSelector("div:nth-child(1) > div > ul > li"));
			WebElement firstElement = Iterables.getFirst(pagination, driver.findElement(By.cssSelector("div:nth-child(1) > div > ul > li")));
			firstElement.click();
			
			while(count < pages){
				try{		
					
					//wait for table to appear before counting the rows
					WebDriverWait waitx = new WebDriverWait(driver, 10);
					waitx.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".listview-visible .z-listbox-body tbody")));
					
					//update navigation bar information
					eleParent=driver.findElement(By.cssSelector(".listview-visible.z-listbox"));
					List <WebElement> element = eleParent.findElements(By.cssSelector("div:nth-child(1) > div > ul > li"));
					String next= element.get(element.size()-1).getText();
					
					//count rows for current page
					rows = rows + StartRowCount();
					
					if (pages <= 10){
						if(next.equals("Next")){
							element.get(element.size()-1).click();
						}
					}
					
					else if(pages > 10){
						//locate the next element
						if(next.equals("Last")){
							element.get(element.size()-2).click();
						} else if(next.equals("Next")){
							element.get(element.size()-1).click();
						}
					}
					
				} catch (StaleElementReferenceException elementHasDisappeared){
					//exception for when the element disappeared
				}
				count++;
			}
		}	
			return rows;
		}	
	
	//function for checking if pagination exists
	public static int checkPagination(){
		int pagecount = 0;

		//go to last page and get last element
		WebElement eleParent=driver.findElement(By.cssSelector(".listview-visible.z-listbox"));
		List <WebElement> pagination= eleParent.findElements(By.cssSelector("div:nth-child(1) > div > ul > li"));
		WebElement lastElement = Iterables.getLast(pagination, driver.findElement(By.cssSelector("div:nth-child(1) > div > ul > li")));
		
		if(lastElement.getText().equals("Next")){
			pagecount = Integer.parseInt(pagination.get(pagination.size()-2).getText());
		} else if (lastElement.getText().equals("Last")){
			lastElement.click();
			eleParent=driver.findElement(By.cssSelector(".listview-visible.z-listbox"));
			List <WebElement> pagination2 = eleParent.findElements(By.cssSelector("div:nth-child(1) > div > ul > li"));
			pagecount = Integer.parseInt(pagination2.get(pagination2.size()-1).getText());
		}		
		
		return pagecount;
	}
	
	//function for counting rows
	public static int StartRowCount(){
		
		WebElement mytable = driver.findElement(By.cssSelector(".listview-visible .z-listbox-body tbody"));
		List<WebElement> rows_table = mytable.findElements(By.tagName("tr"));
		
		int rows_count = rows_table.size();		
		return rows_count;
	}
	
	//-------------------------------------------- LOCUS TEST ----------------------------------------//
	public static void testGeneLoci(String expected, String query, String maxevalue, String database, String sequence) throws InterruptedException{
		int inrow;
		
		//select sequence
		Select insequence = new Select(driver.findElement(By.cssSelector(".search-by.z-select")));
		insequence.selectByVisibleText("Sequence");
		
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".blast-input-field.z-textbox")));
		
		//assign the value to first variety
		if(sequence != null && !sequence.isEmpty()){
			WebElement blastinput = driver.findElement(By.cssSelector(".blast-input-field.z-textbox"));
			blastinput.clear();
			blastinput.sendKeys(sequence);
		}
		
		//place max evalue inputs
		if(maxevalue != null && !maxevalue.isEmpty()){
			WebElement evalueinput = driver.findElement(By.cssSelector(".z-max-evalue.z-label"));
			evalueinput.clear();
			evalueinput.sendKeys(maxevalue);		
		}
		
		//choose query dropdown option
		if(query != null && !query.isEmpty()){
			Select query_type = new Select(driver.findElement(By.cssSelector(".blast-dropdown.z-select")));
			query_type.selectByVisibleText(query);
		}
		
		//choose database dropdown option
		if(database!= null && !database.isEmpty()){
			Select db_type = new Select(driver.findElement(By.cssSelector(".database-type-select.z-select")));
			db_type.selectByVisibleText(database);
		}
		
		//click the search button
		driver.findElement(By.cssSelector(".blast-sequence-button.z-button")).click();
		
		//wait until results show
		wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".blast-table.z-listbox")));
		
		//click E VALUE to sort elements
		WebElement blasttable = driver.findElement(By.cssSelector(".blast-table.z-listbox"));
		List <WebElement> blastheader = blasttable.findElements(By.cssSelector("div:nth-child(1) > table > tbody > tr > th"));
		int size = blastheader.size();
		blastheader.get(size-2).click();
		
		Thread.sleep(1000);
		
		//get firt row
		blasttable = driver.findElement(By.cssSelector(".blast-table.z-listbox"));
		List <WebElement> result = blasttable.findElements(By.cssSelector("div:nth-child(3) > table > tbody > tr"));
		WebElement first = Iterables.getFirst(result, driver.findElement(By.cssSelector("div:nth-child(3) > table > tbody > tr")));
		WebElement ans = first.findElement(By.cssSelector("td:nth-child(4)"));
		
		//parse answer, disregard decimal point
		String s2 = ans.getText().split("\\.", 2)[0];
		
		//check if answer is top notch, compare topnotch from expected 
		if(s2.equals(expected)) specialResult("locus", expected + " is in row 1");
		else {
			//get the nth row where the expected answer is
			int totalsize = result.size()-1;
			for(int i = 0; i < totalsize; i++){
				WebElement current = result.get(i);
				String subject = current.findElement(By.cssSelector("td:nth-child(4)")).getText();
				 s2 = subject.split("\\.", 2)[0];
				 
				 //check if subejct on current row is same as expected
				 if(s2.equals(expected)){
					 //return row
					 inrow = i+1;
					 specialResult("locus", expected + " is in row "+ Integer.toString(inrow));
					 break;
				 }
			}
		}
	}
	
	//------------------------------------------ GLOBAL FUNCTIONS --------------------------------------//
	public static void startAutomation(String URL) throws AWTException{
		//connect to the browser and web site
		System.setProperty("webdriver.gecko.driver","/home/lbgo/Downloads/geckodriver");		
		FirefoxOptions options = new FirefoxOptions();
		fp = new FirefoxProfile();
		options.setBinary("/home/lbgo/Desktop/firefox/firefox"); 
		driver = new FirefoxDriver(options);
		
		driver.manage().window().maximize();
		driver.get(URL);		

		//check if pop up occurred, check the first instance
		isAlertPresent(".z-messagebox-button.z-button");
	}
	
	//check if popup exists and catch it if it does
	public static Boolean isAlertPresent(String path){ 
		try{
			WebDriverWait wait = new WebDriverWait(driver, 10);
			String parentWindowHandler = driver.getWindowHandle(); // Store your parent window
			String subWindowHandler = null;
			Set<String> handles = driver.getWindowHandles(); // get all window handles
			Iterator<String> iterator = handles.iterator();
			while (iterator.hasNext()){
			    subWindowHandler = iterator.next();
			}
			driver.switchTo().window(subWindowHandler); // switch to popup window
			// Now you are in the popup window, perform necessary actions here
			driver.findElement(By.cssSelector(path)).click();
			driver.switchTo().window(parentWindowHandler);  // switch back to parent window
			return true;
		} catch (Exception e){ }
		return false;
	} 
	
	//Result for checking rows and columns
	public static void checkRows(int result, int totalrows, String testtype){
		testcount++;
		writeOutput("\nTest " + testcount);
		writeOutput("Test Type: " + testtype);
		
		if(totalrows == result){
			writeOutput("NO ERROR");
		} else {
			writeOutput("ERROR");
			writeOutput("Expected rows: " + totalrows);
			writeOutput("Resulted rows: " + result + "");
		}
		
	}

	//Result for checking rows
	public static void checkRowsCols(String testtype, int resrow, int totalrows, int rescol, int totalcols){
		testcount++;
		writeOutput("\nTest " + testcount);
		writeOutput("Test Type: " + testtype);
		
		if(totalrows == resrow && totalcols == rescol){
			writeOutput("NO ERROR");
		} else {
			writeOutput("ERROR");
			writeOutput("Expected rows: " + totalrows + " >> Resulted rows: " + resrow);
			writeOutput("Expected columns: " + totalcols + " >> Resulted columns: " + rescol + "\n");
		}

	}
	
	//special case for results
	public static void specialResult(String testtype, String result){
		testcount++;
		writeOutput("Test " + testcount);
		writeOutput("Test Type: " + testtype);
		
		writeOutput(result);
	}
	
	//special case for results
	public static void justAppend(String result){
		writeOutput(result);
	}
	
	//write results to output file
	public static void writeOutput(String writethis){
		 BufferedWriter writer = null;
		 
		//create file, write to file
		if(flag == true){
			File f = new File("SNPTestResult.txt");
			//overwrite file if it exists
			if(f.exists()){
				try(FileWriter fw = new FileWriter("SNPTestResult.txt", false);
					    BufferedWriter bw = new BufferedWriter(fw);
					    PrintWriter out = new PrintWriter(bw))
					{
					out.println("------- RESULTS -------");
					out.println(writethis);
					} catch (IOException e) {
					    //exception handling left as an exercise for the reader
					}
			}
			//else create new file
			else {
				try {
				    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("SNPTestResult.txt"), "utf-8"));
				    writer.write("------- RESULTS -------");
				    writer.write(writethis);
				    writer.newLine();
				} catch (IOException ex) {
				  // report
				} finally {
				   try {writer.close();} catch (Exception ex) {/*ignore*/}
				}
			}
			
			flag = false;
		}
		//append to file
		else{
			try(FileWriter fw = new FileWriter("SNPTestResult.txt", true);
				    BufferedWriter bw = new BufferedWriter(fw);
				    PrintWriter out = new PrintWriter(bw))
				{
				out.println(writethis);
				} catch (IOException e) {
				    //exception handling left as an exercise for the reader
				}
		}
		
	}

}
