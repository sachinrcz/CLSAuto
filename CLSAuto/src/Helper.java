import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.CallFactory;
import com.twilio.sdk.resource.instance.Call;


public class Helper {
	
	static String date= "";
	static boolean newauto = false;
	static boolean auto = false;
	static boolean call = false;
	static boolean startcall = false;
	static boolean finalcall = false;
	 public static final String ACCOUNT_SID = "AC1eceb247ff9808012a01a36a60af172d"; 
	 public static final String AUTH_TOKEN = "38c4c6c52eded12e7d621e223901edad"; 
	 static File errordir=null;
	 static File newclaimdir=null;
	 static File summarydir=null;
	 static File logdir=null;
	 static String folderdate = null;
	 static String logfile = null;
	 static String autoitfile = null;
	 static String summaryFile =null;
	 static File screendir=null; 
	 static void executeAutoit(String exename){
		 try {
//				Thread.sleep(120000);
				log("Executing AutoIT Script: "+exename);
				Runtime.getRuntime().exec("Executables\\"+exename+" \""+autoitfile+"\" "+"\""+screendir+"\"");
				log("Autoit Executed");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }
	 
	 static boolean isPastMidNight(){
			boolean midnight=true;
			java.util.Date date1= new java.util.Date();

			Time Sqldob1 = new Time(date1.getTime());
			System.out.println("User Time: " +Sqldob1);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 23); // Your hour
			cal.set(Calendar.MINUTE, 59); // Your Mintue
			cal.set(Calendar.SECOND, 59); // Your second

			Time sqlTime3 = new Time(cal.getTime().getTime());
			System.out.println("your time: "+sqlTime3);

			if(Sqldob1.before(sqlTime3)){ 
				 System.out.println("Not Past MidNight");
			   return false;
			}
			 System.out.println("Past MidNight");
			
			return midnight;
		}
	 static String maketwodigit(int a){
		 try{
			 String t = new Integer(a).toString();
			 if(t.length()==2){return t;}
			 if(t.length()==1){ return "0"+t;}
			 return t;
		 }catch(Exception e){}
		 return "";
	 }
	static String createDate(){ 
		 Calendar calendar = Calendar.getInstance();
		 calendar.add(Calendar.DAY_OF_YEAR, -1);
		//Remove -1 to set Date as Current Day
		int d= calendar.get(Calendar.DAY_OF_MONTH);
		try{
			if(!auto){
				Scanner reader = new Scanner(System.in);  // Reading from System.in
				System.out.println("Enter Day or Press s to skip");
				d =reader.nextInt();
			}
		}catch(Exception e){}
		int y= calendar.get(Calendar.YEAR);
		int m= calendar.get(Calendar.MONTH);
		date =new Integer(y).toString()+maketwodigit(m+1)+maketwodigit(d);  
		
		 
			return date;
	}
	 
	
	static String currentTime(){
		 Calendar cal = Calendar.getInstance();
	     SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); 
	     return sdf.format(cal.getTime()) ;
	}
	static String getTime(){
		 Calendar cal = Calendar.getInstance();
	     SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a"); 
	     return sdf.format(cal.getTime()) ;
	}
	static String getTime2(){
		 String t = getTime();
		 t = t.substring(0, 4) + "0"+t.substring(5);
		 return t;
	}
	
	
	
	static void log(String text){
		 
		text= currentTime()+"|| "+text+ System.getProperty("line.separator");
		System.out.println(text);;
		File f = new File(logfile);
		if(!f.exists())  {
			try{
				File fout = new File(logfile);
				FileOutputStream fos = new FileOutputStream(fout);
			 
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
				bw.write(text);  
				bw.close();
			}catch(Exception e){}
		}else{
			try {
			    Files.write(Paths.get(logfile), text.getBytes(), StandardOpenOption.APPEND);
			}catch (IOException e) { 
				e.printStackTrace();
			}
		}
		
	}
	
	static void SendMail(String subject){
		
		// Recipient's email ID needs to be mentioned.
	      String to = "sscrackers1@gmail.com"; 

	      // Sender's email ID needs to be mentioned
	      
	      final String username = "auto@eltmanlaw.com";//change accordingly
	      final String password = "G@teway2013";//change accordingly
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username,password);
				}
			});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
		 
			message.setSubject(subject);
			message.setText(" ");

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	 public static void call(){
		 try{
		TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN); 
	 
		// Build the parameters 
		List<NameValuePair> params = new ArrayList<NameValuePair>(); 
		params.add(new BasicNameValuePair("To", "+917696513706")); 
		params.add(new BasicNameValuePair("From", "+917696513706"));   
		params.add(new BasicNameValuePair("Method", "GET"));  
		params.add(new BasicNameValuePair("FallbackMethod", "GET"));  
		params.add(new BasicNameValuePair("StatusCallbackMethod", "GET"));    
		params.add(new BasicNameValuePair("Record", "false")); 
		params.add(new BasicNameValuePair("Url", "http://demo.twilio.com/welcome/voice/"));
		CallFactory callFactory = client.getAccount().getCallFactory(); 
		Call call = callFactory.create(params); 
		System.out.println(call.getSid()); 
		 }catch(Exception e){e.printStackTrace();}
	 } 
	public static void main(String[] args){
		//call();
	}
	
	public static void addstamp(String filename, String line){
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
		    out.println(line);
		    out.println("");
		    out.close();
		    System.out.println(line);
		} catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
	}
	
	 
}
