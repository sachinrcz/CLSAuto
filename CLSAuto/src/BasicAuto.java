import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
 
public class BasicAuto extends Helper {

	static String newClaimFolder = "S:\\Sachin\\C2\\NewClaimReports\\";
	static String overnightsummary = "S:\\Sachin\\C2\\OvernightSummary\\";
	static String overnighterrors = "S:\\Sachin\\C2\\Overnight Errors\\";
	static String c2outputfolder="F:\\CLSInc\\OUTPUT\\Users\\SSingh2\\2017\\";
	static String overnightlog = "S:\\Sachin\\C2\\OvernightLogs\\";
	static String summaryFolder = "S:\\Sachin\\C2\\OvernightSummary\\";
	static String screenfolder = "S:\\Sachin\\C2\\OvernightScreenshots\\";
	
	static String getFolderDateOLD(){
		String date="";
		Calendar cal = Calendar.getInstance();
//		int d= Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int w = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		//Set to 0 for old setting
		int d = 0;
		if(!auto){
			Scanner reader = new Scanner(System.in);  // Reading from System.in
			System.out.println("Enter Day to Add for Folder Date");
			
			d =reader.nextInt();
			cal.add(Calendar.DATE,d);
		}else{
			if(w == Calendar.FRIDAY){ 
				 cal.add(Calendar.DATE,d+3);
			}
			else{
				if(w == Calendar.SATURDAY){ 
					 cal.add(Calendar.DATE,d+2);
				}else{
					 cal.add(Calendar.DATE,d+1);
				}
				
			}
		}
		
		date =cal.get(Calendar.DAY_OF_MONTH)+" "+new SimpleDateFormat("MMMM").format(cal.getTime());
		
		return date;
	}
	
	static String getFolderDate(){
		String date="";
		Calendar cal = Calendar.getInstance();
//		int d= Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int w = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		//Set to 0 for old setting
		int d = -1;
		if(!auto){
			Scanner reader = new Scanner(System.in);  // Reading from System.in
			System.out.println("Enter Day to Add for Folder Date");
			
			d =reader.nextInt();
			cal.add(Calendar.DATE,d);
		}else{ 
				if(w == Calendar.SATURDAY){ 
					 cal.add(Calendar.DATE,d+3);
				}else{
					 cal.add(Calendar.DATE,d+1);
				}
				
			
		}
		
		date =cal.get(Calendar.DAY_OF_MONTH)+" "+new SimpleDateFormat("MMMM").format(cal.getTime());
		
		return date;
	}
	
	
	
	static String getFileDate(){
		String date="";
		int d= Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int w = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
//		if(w == Calendar.FRIDAY){ d = d+3;}
//		else{
			if(w == Calendar.SATURDAY){ 
				d = d+2;
			}
			else{
				if(w==Calendar.SUNDAY){
					d++;
				}
//				else{
//					if(!isPastMidNight()){d++;}
//				}  
			}
//		}
		int y= Calendar.getInstance().get(Calendar.YEAR);
		int m= Calendar.getInstance().get(Calendar.MONTH);
		date ="_"+new Integer(y).toString()+"_"+maketwodigit(m+1)+"_"+maketwodigit(d);  
		
		return date;
	}
	
	static void createFolders(){
		try{
			folderdate=getFolderDate();
			
			logdir = new File(overnightlog+folderdate);
			logfile = logdir+"\\EventLog"+getFileDate()+".log";
			autoitfile =  logdir+"\\AI_Watcher"+getFileDate()+".log";
			makeDir(logdir);
			 summarydir = new File(overnightsummary+folderdate);
			  errordir = new File(overnighterrors+folderdate);
			  newclaimdir = new File(newClaimFolder+folderdate);
			  screendir = new File(screenfolder+folderdate);
			  summaryFile = summarydir+"\\CLS_Overnight_Summary"+getFileDate()+".txt";
			 makeDir(summarydir);
			 makeDir(errordir);
			 makeDir(newclaimdir);
			 makeDir(screendir);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	static void makeDir(File dir){
		if(!dir.exists()){
			if(dir.mkdir()){log("Directory Created: "+dir);}
		}else{
			log("Directory already exist: "+dir);
		}
	}
	
	static void moveEDIAutoError(String court){
		try{
			Format formatter = new SimpleDateFormat("MMM");
		    String s = formatter.format(new Date());
		    String f = c2outputfolder +s+"\\";
			File[] files = new File(f).listFiles();

			Arrays.sort(files, new Comparator<File>(){
			    public int compare(File f1, File f2)
			    {
			        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
			    } });
			if(files.length>0){
				log(files[0]+"");
				File error1 = new File(errordir+"\\CM_"+court+"_Error_Report"+getFileDate()+".xls");
				files[0].renameTo(error1); 
				files[0].delete();
				log(files[0]+" Renamed To "+error1);
				if(files.length>1){
					File error2 = new File(errordir+"\\CM_"+court+"_2_Error_Report"+getFileDate()+".xls");
					log(files[1]+"");
					files[1].renameTo(error2); 
					files[1].delete();
					log(files[1]+" Renamed To "+error2);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	 
	 
	static void moveNewClaim(String court){
		try{
			System.out.println("Lock Users Out");
			Scanner reader = new Scanner(System.in);  // Reading from System.in
			System.out.println("Press Enter to Move New Claim Report");
			reader.nextLine();
			Format formatter = new SimpleDateFormat("MMM");
		    String s = formatter.format(new Date());
		    String f = c2outputfolder +s+"\\";
			File[] files = new File(f).listFiles();
			log("====Moving New Claim Report====");
			 
			if(files.length==1){
				log(files[0]+"");
				File claimreport = new File(newclaimdir+"\\CM_"+court+"_Opt_New_Claim_Report"+getFileDate()+".xls");
				files[0].renameTo(claimreport); 
				files[0].delete();
				log(files[0]+" Renamed To "+claimreport);
				 
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void test(){
		 try {
			 	folderdate=getFolderDate();
				
				logdir = new File(overnightlog+folderdate);
				logfile = logdir+"\\EventLog"+getFileDate()+".log";
				autoitfile =  logdir+"\\AI_Watcher"+getFileDate()+".log";
				screendir = new File(screenfolder+folderdate);
				System.out.println("test.exe \""+logfile+"\" "+"\""+screendir+"\"");
				Runtime.getRuntime().exec("test.exe \""+logfile+"\" "+"\""+screendir+"\"");
				 System.out.println("Executed");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public static void startRequest(String name, String foldername){
		String localurl = "http://192.168.147.44:8000/dashboard-api/";
		String url = "http://eec.mymailing.website/dashboard-api/"; 
		startRequest(localurl,name, foldername);
		startRequest(url,name, foldername);
	}
	public static int startRequest(String url, String name, String foldername){
		try{

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);

			// add header
			post.setHeader("User-Agent", "Mozilla/5.0");

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("name", name));
			urlParameters.add(new BasicNameValuePair("folderdate", foldername));
			urlParameters.add(new BasicNameValuePair("status","r"));
			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = client.execute(post);
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + post.getEntity());
			System.out.println("Response Code : " +
	                                    response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(
	                        new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			System.out.println(result.toString());
			return new Integer(result.toString());

		}catch(Exception e){
			e.printStackTrace();
		}
		return -1;
	}
	
	
	public static void updateDashboard(Map<String,String> data){
		String url = "http://127.0.0.1:5050/dashboard-api/";
		updateDashboard(url, data);
	}
	
	public static int updateDashboard(String url, Map<String,String> data){
		try{
			

			HttpClient client = new DefaultHttpClient();
			 
			HttpPut put = new HttpPut(url);

			// add header
			put.setHeader("User-Agent", "Mozilla/5.0");

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			for(Map.Entry<String, String> entry: data.entrySet()){
				String key = entry.getKey();
				String value = entry.getValue();
				urlParameters.add(new BasicNameValuePair(key, value));
			}
			 

			put.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = client.execute(put);
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + put.getEntity());
			System.out.println("Response Code : " +
	                                    response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(
	                        new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			System.out.println(result.toString());
			return response.getStatusLine().getStatusCode();

		}catch(Exception e){
			e.printStackTrace();
		}
		return -1;
	}
	
	public static String markComplete(String dashboardID){
		 StringBuilder result = new StringBuilder();
		try{
		 
	      URL url = new URL("http://192.168.147.44:8000/dashboard-api/complete/"+dashboardID+"/");
	      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	      conn.setRequestMethod("GET");
	      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	      String line;
	      while ((line = rd.readLine()) != null) {
	         result.append(line);
	      }
	      rd.close();
	      System.out.println(result.toString());
		}catch(Exception e){e.printStackTrace();}
	      return result.toString();
	}
	 
	
	
	public static void main(String[] args){
		//System.out.println(getFileDate());
		//createFolders();
		//moveEDIAutoError();
//		isPastMidNight(); 
//		markComplete("15");
		
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy 'at' HH:mm:ss z");
        System.out.println( sdf.format(cal.getTime()) );
		 
			
	}
	
	
	
}
