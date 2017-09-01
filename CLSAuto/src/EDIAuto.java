import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

public class EDIAuto extends Helper {
	
	static String nightlyupdatefolder = "C:\\Sachin\\C2Dev\\PostJmt_Placement_toCLS\\NightlyUpdates\\";
	
	static String noncourt = "CLS_NonCourtOpt_OvernightUpdate_AutoSys_DT_DATE_Plx.txt";
	static String noncourt2 = "CLS_NonCourtOpt_OvernightUpdate2_AutoSys_DT_DATE_Plx.txt";
	static String trigger = "CLS_OvernightTriggers_AutoSys_DT_DATE_Plx.txt";
	static String courtopt = "CLS_CourtOpt_OvernightUpdate_AutoSys_DT_DATE_Plx.txt";
	static String courtopt2 = "CLS_CourtOpt_OvernightUpdate2_AutoSys_DT_DATE_Plx.txt";
	
	
	static String inifilepath ="F:\\CLSInc\\CUSTOM\\temp.INI";
	
	
	
	static boolean sendemail=false;
	static void EDIAuto1(){
		
		waitforfile();
		BasicAuto.startRequest("CLS Overnight "+folderdate, folderdate); 
		log("START EDI AUTO 1");
		 
		String path = nightlyupdatefolder+noncourt.replace("DATE", date);
		
		File file = new File(path);
		if(file.exists()){
			addstamp(summaryFile, "EDI import started at "+getTime2());
			 
			
			log("Creating INI: "+path);
			createINI(path);
			moveINI();
			 if(startcall){
				 call();
			 }
			 if(sendemail){
				 SendMail("START EDI AUTO 1 : "+currentTime());
				 
			 }
			runbatch();
			 
			 
			 if(sendemail){
				 SendMail("END EDI AUTO 1 : "+currentTime());
			 }
		}else{
			log("File Not Found: "+path);
		}
		log("END EDI AUTO 1");
	}
	
	static void EDIAuto2(){
		
		waitforfile();
		log("START EDI AUTO 2");
		String path = nightlyupdatefolder+noncourt2.replace("DATE", date);
		File file = new File(path);
		if(file.exists()){
			log("Creating INI: "+path);
			createINI2(path);
			moveINI();
			 if(sendemail){
				 SendMail("START EDI AUTO 2 : "+currentTime());
			 }
			runbatch();
			  
			 if(sendemail){
				 SendMail("END EDI AUTO 2 : "+currentTime()); 
			 }
			if(finalcall){
				call();
			}
		}else{
			log("File Not Found: "+path);
		}
		log("END EDI AUTO 2");
	
	}
	 
	static void moveINI(){
		try{
			log("Moving INI file");
			 File tempfile =new File("temp.INI");
			 File inifile = new File(inifilepath);
			 if(tempfile.exists()){
				 if(inifile.exists()){
					 log("Old INI file found");
					 inifile.delete();
					 log("Old INI file deleted");
				 }
				 if(tempfile.renameTo(inifile)){
					 log("New INI file successfully copied.");
				 }
			 }
			
		}catch(Exception e){
			log(e.toString());
		}
	}
	
	 
	
	static void runbatch(){
		
//		if(!auto){
//			Scanner reader = new Scanner(System.in);  // Reading from System.in
//			System.out.println("Press y to execute cmd");
//			reader.nextLine();
//		}
		Process p = null; 
		try{
			log("Executing cmd");
			Runtime run = Runtime.getRuntime();  
			 
	        String cmd = "test.cmd";   
	        p = run.exec(cmd); 

			log("Waiting for process to finish.");
	        p.waitFor();
	        BufferedReader reader=new BufferedReader(
                    new InputStreamReader(p.getInputStream())
                ); 
                String line; 
                while((line = reader.readLine()) != null) 
                { 
                    System.out.println(line);
                } 
	        log("RUN.COMPLETED.SUCCESSFULLY"); 
	       
	        
	       
		}catch(Exception e){
			log(e.toString());
		}finally{
			p.destroy();
		}
		 System.out.println(p.exitValue());
	}
	
	static void createINI(String path){
		try{
			File fout = new File("temp.INI");
			if(fout.exists()){
				fout.delete();
			}
			FileOutputStream fos = new FileOutputStream(fout);
		 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		   
			bw.write("||MENUPATH:4-2-1-1");
			bw.newLine();
			bw.write("EDI NUMBER=326");
			bw.newLine();
			bw.write("FILE NUMBER=GO000001");
			bw.newLine();
			bw.write("SOURCE FILE=@::"+path);
			bw.newLine();
			bw.write("UNASSIGNED FORW=");  
			bw.close();
		}catch(Exception e){
			log(e.toString());
		}
	}
	
	static void createINI2(String path){
		try{
			File fout = new File("temp.INI");
			if(fout.exists()){
				fout.delete();
			}
			FileOutputStream fos = new FileOutputStream(fout);
		 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		   
			bw.write("||MENUPATH:4-2-1-1");
			bw.newLine();
			bw.write("EDI NUMBER=326");
			bw.newLine();
			bw.write("FILE NUMBER=AUTO");
			bw.newLine();
			bw.write("SOURCE FILE=@::"+path);
			bw.newLine();
			bw.write("UNASSIGNED FORW=");  
			bw.close();
		}catch(Exception e){
			log(e.toString());
		}
	}
	
	static void createINIBatchWord(String path){
		try{
			File fout = new File("temp.INI");
			if(fout.exists()){
				fout.delete();
			}
			FileOutputStream fos = new FileOutputStream(fout);
		 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		   
			bw.write("||MENUPATH:1-3-6-5-4");
			bw.newLine(); 
			bw.write("SOURCE FILE=@::"+path);  
			bw.close();
		}catch(Exception e){
			log(e.toString());
		}
	}
	
	static void readProperty(){
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("config.properties"); 
			prop.load(input);
			nightlyupdatefolder = prop.getProperty("nightlyfolder");
			if(new Integer(prop.getProperty("auto"))==1){
				auto = true;
			}
			if(new Integer(prop.getProperty("email"))==1){
				sendemail = true;
			} 
			if(new Integer(prop.getProperty("call"))==1){
				call = true;
			} 
			if(new Integer(prop.getProperty("finalcall"))==1){
				finalcall = true;
			} 


			if(new Integer(prop.getProperty("startcall"))==1){
				startcall = true;
			} 
			if(new Integer(prop.getProperty("newauto"))==1){
				newauto = true;
			} 
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	static void waitforfile(){
		while(true){
			try{
				
				String path = nightlyupdatefolder+noncourt.replace("DATE", date);
				File file = new File(path);
				log("Checking for file: "+path);
				if(file.exists()){
					log("File Found"); 
					Thread.sleep(300000);
					break;
				}else{
					log("File not found");
					Thread.sleep(300000);
				}
			}catch(Exception e){
				break;
			}
		}
	}
	 
	 static void startIndex(){
//		 try{
//			 System.out.println("Check for Users before starting Index 1-9-1");
//			 Scanner reader = new Scanner(System.in);  // Reading from System.in
//			 System.out.println("Press Enter to start index watcher");
//			 reader.nextLine();
//			 log("Executing Index Watcher Script");
//			 Runtime.getRuntime().exec("indexApp.exe");
//			 log("Index Watcher Executed");
//		 }catch(Exception e){}
		 
		 try{
				Scanner in = new Scanner(System.in);
				if(newauto || askChoice("Index Process")){

					 if(newauto){
						 System.out.println("Wait for 3 Minutes before Index Execution");
						 Thread.sleep(180000);
					 }
					executeAutoit("indexApp.exe");
					System.out.println("Press y if index started or press Enter.....");
					String choice=in.nextLine();
					if(choice.toLowerCase().contains("y")){
						addstamp(summaryFile, "Index process started at "+getTime2());
						System.out.println("Press Enter when you finish Index Process....");
						in.nextLine();
						addstamp(summaryFile, "Index Process completed at "+getTime2());
					}else{
						addstamp(summaryFile, "Index Process did not start "+getTime2());
					}
					
				}else{
					addstamp(summaryFile, "Index Process did not start "+getTime2());
				}
				
			 }catch(Exception e){}
	 }

	 public static boolean askChoice(String process){
		 Scanner in = new Scanner(System.in);
		 System.out.println("Press 'y' if wish to run "+process);
			String choice=in.nextLine();
			if(choice.toLowerCase().contains("y")){
				return true;
			}
			return false;
	 }
	 
	 public static void runCourtOptEDI(){ 
		 	String path = nightlyupdatefolder+courtopt.replace("DATE", date);
			File file = new File(path);
			if(file.exists()){ 
				if(newauto || askChoice("CourtOptEDI")){
					try {
						if(newauto){
							System.out.println("Wait for 3 Minutes before CourtOptEDI Execution");
							Thread.sleep(180000);
						}
						 Runtime.getRuntime().exec("Executables\\watcher.exe"+" \""+autoitfile+"\" "+" \""+screendir+"\""+" \""+"CourtOpt EDI I"+"\"");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
					//executeAutoit("watcher.exe");
					addstamp(summaryFile, "CourtOpt EDI I import started at "+getTime2());
					log("Creating INI: "+path);
					createINI(path);
					moveINI();				 
					runbatch();
					addstamp(summaryFile, "CourtOpt EDI I import completed at "+getTime2());
				}else{
					System.out.println("Fine! Run it yourself!!");
					return;
				}
			}else{
				log("CourtOpt EDI I Not Found: "+path);
				return;
			} 
			try{
				log("Three minute sleep before executing any code next");
				Thread.sleep(180000);
			}catch(Exception e){}
			path = nightlyupdatefolder+courtopt2.replace("DATE", date);
			file = new File(path);
			if(file.exists()){
				 
				try {
					 Runtime.getRuntime().exec("Executables\\watcher.exe"+" \""+autoitfile+"\" "+" \""+screendir+"\""+" \""+"CourtOpt EDI II"+"\"");
						} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//executeAutoit("watcher.exe");
					addstamp(summaryFile, "CourtOpt EDI II import started at "+getTime2());
					log("Creating INI: "+path);
					createINI2(path);
					moveINI();				 
					runbatch();	 
					addstamp(summaryFile, "CourtOpt EDI II import completed at "+getTime2());
					BasicAuto.moveEDIAutoError("Court");
			}else{
				log("CourtOpt EDI II Not Found: "+path);
			} 
			if(file.exists()){ 
				addstamp(summaryFile, "CourtOpt EDI import completed at "+getTime2()); 
				
				addstamp(summaryFile, "CourtOpt New claim processing started at "+getTime2());
				
				runAutoCourtOpt();			 
				BasicAuto.moveNewClaim("Court");
				
				addstamp(summaryFile, "CourtOpt New claim processing completed at "+getTime2());
					
			}
		
	 }
	
	 public static void runEDIs(){
 
		 try {
//			 BasicAuto.startRequest("CLS Overnight "+folderdate, folderdate); 
		     Runtime.getRuntime().exec("Executables\\watcher.exe"+" \""+autoitfile+"\" "+" \""+screendir+"\""+" \""+"NonCourtOpt EDI I"+"\"");
			 //executeAutoit("watcher.exe");
			 addstamp(summaryFile, "Automation app started.");
		     EDIAuto1();
			
				log("Starting Sleep for 5 minutes.");
				Thread.sleep(300000);
				log("Sleep Over");
				Runtime.getRuntime().exec("Executables\\watcher.exe"+" \""+autoitfile+"\" "+" \""+screendir+"\""+" \""+"NonCourtOpt EDI II"+"\"");
			} catch (Exception e) { 
				e.printStackTrace();
			}
			//executeAutoit("watcher.exe");
			EDIAuto2();
			
			BasicAuto.moveEDIAutoError("NonCourt");
			addstamp(summaryFile, "EDI import completed at "+getTime2()); 
			
			addstamp(summaryFile, "NonCourtOpt New claim processing started at "+getTime2());
			
			runAutoNonCourtOpt();			 
			BasicAuto.moveNewClaim("NonCourt");
			
			addstamp(summaryFile, "NonCourtOpt New claim processing completed at "+getTime2());
				
			// Add CourtOpt Processing steps here
			runCourtOptEDI();
			
			  
			runAutoBatchWord();
			
			startIndex();
			
			new File(inifilepath).delete();
			
	 }
	 
	 public static void runAutoNonCourtOpt(){
		 try{
			Scanner in = new Scanner(System.in);
			if(newauto || askChoice("Auto NonCourtOpt")){
				if(newauto){
					System.out.println("Wait for 3 Minutes before AutoNonCourtOpt Execution");
					Thread.sleep(180000);
				}
				executeAutoit("autononcourtopt.exe");
			}
			 
			
		 }catch(Exception e){}
	 }
	 public static void runAutoCourtOpt(){
		 try{
			 if(newauto || askChoice("Auto CourtOpt")){
				 if(newauto){
					 System.out.println("Wait for 3 Minutes before AutoCourtOpt Execution");
					 Thread.sleep(180000);
				 }
				executeAutoit("autocourtopt.exe");
			}
		 }catch(Exception e){}
	 }
	 
	 public static void runAutoBatchWord(){
		 try{
			 if(newauto || askChoice("Auto BatchWord")){
				 if(newauto){
					 System.out.println("Wait for 3 Minutes before Batch Word Execution");
					 Thread.sleep(180000);
				 }
				log("Starting Auto Batch Word Process");
				String path = nightlyupdatefolder+trigger.replace("DATE", date);
				File file = new File(path);
				if(file.exists()){
					addstamp(summaryFile, "Batch Word Processing started at "+getTime2());
					log("Creating INI: "+path);
					createINIBatchWord(path);
					moveINI();
					executeAutoit("BatchWordMerge.exe");
					runbatch();
					
				}else{
					log("File Not Found: "+path);
				}
				 
				
			} 
			 Scanner in = new Scanner(System.in);
				System.out.println("Press Enter when you finish batch word processing....");
				in.nextLine();
				addstamp(summaryFile, "Batch Word Processing completed at "+getTime2());
				
			 
		 }catch(Exception e){}
	 }
	 
	 public static File lastFileModified(String dir) {
		    File fl = new File(dir);
		    File[] files = fl.listFiles(new FileFilter() {          
		        public boolean accept(File file) {
		            return file.isFile();
		        }
		    });
		    long lastMod = Long.MIN_VALUE;
		    File choice = null;
		    for (File file : files) {
		        if (file.lastModified() > lastMod) {
		            choice = file;
		            lastMod = file.lastModified();
		        }
		    }
		    return choice;
		}
	 public static void addIndexStopTime(){ 
		 addstamp(summaryFile, "5. Index process completed at "+getTime2() +" & Batch export process started."); 
	 }
	 
	 public static void addBatchStopTime(){ 
		 addstamp(summaryFile, "6. Batch export process completed at "+getTime2() ); 
	 }
	 
	public static void main(String[] args){   
		System.out.println("Update 1 August: Fixed date issue at month end"); 
		readProperty();  
		createDate();
		BasicAuto.createFolders(); 
		runEDIs();
		
	}

}
