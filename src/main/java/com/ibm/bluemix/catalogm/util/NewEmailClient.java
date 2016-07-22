package com.ibm.bluemix.catalogm.util;
import java.util.List;

import com.ibm.bluemix.catalogm.community.Discussion;
import com.ibm.bluemix.catalogm.devworks.Article;
import com.ibm.bluemix.catalogm.notifications.json.OneNotification;
import com.sendgrid.*;
import com.sendgrid.smtpapi.SMTPAPI;

public class NewEmailClient {
	
	String messageText = "";// "<html> <head> <meta http-equiv=Content-Type content=\"text/html; charset=windows-1252\"> </head> <body lang=EN-US> <div class=WordSection1> <p class=MsoNormal>Hi.</p> <p class=MsoNormal style='border:none;padding:0in'> Welcome to Catalog Monitor Utility. Please note recent updates to IBM Bluemix Catalog.</p> </div>";
	int numberOfupdates = 0;
	private String catalogType = "Regular";
	private boolean someMajorErrorFlag = false;
	
	

	int numberOfAnnouncements = 0;
	int numberOfDiscussions = 0;
	int numberOfArticles = 0;

	String announcementMessage = "";
	String discussionsMessage = "";
	String articleMessage = "";

	String finalCatalogMessage = "";
	String finalAnnouncementMessage ="";
	String finalDevWorks ="";
	String finalStackOF ="";
	private String emailBody = "Please note that this is automated mail generated by Catalog Monitor Utility. Please contact cvishal@in.ibm.com or spansari@in.ibm.com for subscribe / unsubscribe to this automated emails. Thank You.";
	
	
	private static final String adminemail = "spansari@in.ibm.com";
	
	public NewEmailClient(String typeOfCatlog){
		catalogType = typeOfCatlog;// Regular_US or Experimental_US		
	}
	
	public void appendMessage(String msgLine){
		
		StringBuilder sb = new StringBuilder();
		sb.append("Update: "+(numberOfupdates+1));
//		sb.append("</div>");
//		sb.append("-> CATALOG : "+catalogType);
//		sb.append("</p>");
		sb.append(msgLine);
//		sb.append("</div>");
		sb.append("<p>-------------------------------------------------------------</p>");
//		sb.append("<p>&nbsp;</p>");
		messageText = messageText+sb.toString();
		//System.out.println("------------> Append - Message = "+ messageText);
		numberOfupdates++;
	}



	public boolean hasChangesToNotify() {
		if(numberOfupdates==0){
			System.out.println("***** There are no changes observed. No Email will be sent..............................");
			return false;
		}else{
			System.out.println("***** There are "+numberOfupdates+" changes observed. Email will be sent ..............................");
			return true;
		}
		
	}


	public void sendEmail() {
		System.out.println("Total updates are "+numberOfupdates);
		if(numberOfupdates>20){
			System.out.println("Some Problem in parser.................oops....");
			sendEmailWorkerError();
		} else {
			sendEmailWorkerGood();
		}
		System.out.println("Email sent successfully");
	}



	private void sendEmailWorkerError() {
		// TODO Auto-generated method stub
		SendGrid sendgrid = new SendGrid("snehalpansare", "ibmAdm1n");
		System.out.println("Email will send using using vish.ac4@gmail.com");
	    SendGrid.Email email = new SendGrid.Email();
	    System.out.println("## EMail sent to only VISHAL and SNEHAL only........... : ");
	    email.setTo(new String[]{"cvishal@in.ibm.com","spansare@in.ibm.com"});	    
    	email.setSubject("** ERROR ERROR ERROR +++++ Please note Bluemix Catalog and Services updates. **");
	    email.setFrom(adminemail);
	    email.setHtml("Please Check Catalog Monitor application Seems to have errors. There are more than "  +numberOfupdates+ " updates");
	    try {
			SendGrid.Response response = sendgrid.send(email);
		} catch (SendGridException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void sendEmailWorkerGood(){
		//SendGrid sendgrid = new SendGrid("Gp9O0Zbc0B", "ZOpWrLbHaGIk7206");
		//SendGrid sendgrid = new SendGrid("JvZy37Z3S0iGs3Bd5VNY6Q");
		SendGrid sendgrid = new SendGrid("vish.ac4@gmail.com", "passw0rd");
		System.out.println("Email will send using using vish.ac4@gmail.com");
	    SendGrid.Email email = new SendGrid.Email();
	    SendGridHelper sgHelper = new SendGridHelper();
	    
	    //TEMPLATE WORK
	    //email.setTemplateId("309dcebf-29ed-47bf-bf28-34c949a4f89d");
	    //email.setTemplateId("3f8ea2ea-d26f-4e53-b497-14f91563c2ac");
	    email.setTemplateId("c70d9e49-f552-4fe3-9830-6822fada1040");
	    SMTPAPI _smtp = email.getSMTPAPI();
	    
//	    String[] smtp2list = {"cvishal@in.ibm.com","vish.ac@gmail.com"};
	    
    
	    _smtp.addSubstitutions("-catalogupdates-", sgHelper.getListOfCatalogUpdates());
	    _smtp.addSubstitutions("-announcements-", sgHelper.getListOfNotifications());
	    _smtp.addSubstitutions("-developerworks-", sgHelper.getListOfDWArticles());
	    _smtp.addSubstitutions("-stackoverflow-", sgHelper.getListOfStackOF());
	    _smtp.addSection("-sectiondatacatalogupdates-", messageText.length()>1 ? messageText : "No Updates to catalog.");
	    _smtp.addSection("-sectiondatanotifications-", finalAnnouncementMessage.length()>1 ?  finalAnnouncementMessage : "No new announcements.");
	    _smtp.addSection("-sectiondatadevworks-", finalDevWorks);
	    _smtp.addSection("-sectiondatasof-", finalStackOF);
	    
		_smtp.setTos(sgHelper.getEmail2() );
	    
	    System.out.println("## EMail : "+_smtp.getSubstitutions());
    	email.setSubject("** Test Mail - Automated Email: Bluemix Catalog and Services updates. FYI. **");
	    email.setFrom(adminemail);
	    email.setHtml(emailBody );
	    try {
			SendGrid.Response response = sendgrid.send(email);
		} catch (SendGridException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	private String buildFormattedContent() {
//		// TODO Auto-generated method stub
//		EmailTemplate template = new EmailTemplate();
//		template.set_catalogChanges(finalCatalogMessage);
//		template.set_announcements(finalAnnouncementMessage);
//		template.set_devWorkArticles(finalDevWorks);
//		template.set_stackOverflow(finalStackOF);
//		return template.get_email2send();				
//	}

	public void thereIsAnError() {
		// TODO Auto-generated method stub
		someMajorErrorFlag=true;
	}
	
	public void processAnnouncements(String url2Monitor, List<OneNotification> listofAnnouncements) {
		System.out.println("snehal processAnnouncements : " + listofAnnouncements.size());
		if (listofAnnouncements.size() > 0) {
//			announcementMessage = announcementMessage + "<br>";
//			announcementMessage = announcementMessage + "+++++++++++++++++++++++++++++++<br>";
//			announcementMessage = announcementMessage + "Please note the recent announcements to IBM Bluemix <br>";
			
			for (OneNotification notification : listofAnnouncements) {
				StringBuilder sb = new StringBuilder();
				sb.append("Announcement : " + (numberOfAnnouncements + 1));
//				sb.append("</div>");
//				sb.append("</div>");
				sb.append("<p>Announcement :"+notification.getObj().getTitle()+"</p><p>Description : "+notification.getObj().getText()+"</p>");//<p class=MsoNormal>Regions affected : "+notification.getObj().getRegionsAffected()[0]+"</p>");
//				sb.append("</div>");
				sb.append("<p>&nbsp;</p>");
				announcementMessage = announcementMessage + sb.toString();
				//System.out.println("------------> Append announcement - Message = "+ messageText); 
				numberOfAnnouncements++;
			}
//			announcementMessage = announcementMessage + "+++++++++++++++++++++++++++++++++<br>";
			finalAnnouncementMessage = finalAnnouncementMessage + announcementMessage;
		}
		
	}


	public void processTopDiscussions(List<Discussion> topDiscussions) {
		
		System.out.println("snehal processTopDiscussions : " + numberOfDiscussions);
		
		
//			discussionsMessage = discussionsMessage + "<br>";
//			discussionsMessage = discussionsMessage + "-------------------------------------------<br>";
//			discussionsMessage = discussionsMessage + "Please note the recent Discussions on IBM Bluemix from StackOverflow Community : <br>";
			
			for (Discussion disc : topDiscussions) {
				StringBuilder sb = new StringBuilder();
//				sb.append("Discussion : " + (numberOfDiscussions + 1));
//				sb.append("</div>");
//				sb.append("</div>");
				//<p><a href="http://www.w3schools.com/html/">Visit our HTML tutorial</a></p>
				sb.append("<p> <a href="+disc.getLink()+">"+disc.getTitle()+"</a></p>");
				//sb.append("<p >Discussion on  :" + disc.getTitle()+"</p><p class=MsoNormal>URL : "+ disc.getLink() +"</p>");
				
//				sb.append("</div>");
//				sb.append("<p class=MsoNormal>&nbsp;</p>");
				discussionsMessage = discussionsMessage + sb.toString();
				//System.out.println("------------> Append announcement - Message = "+ messageText); 
				numberOfDiscussions++;
			}
			
//			discussionsMessage = discussionsMessage + "-------------------------------------------<br>";
			finalStackOF = finalStackOF + discussionsMessage;
		
		
	}
	
	public void processPublishedArticles(List<Article> topArticles) {
		
		System.out.println("snehal processPublishedArticles : " + numberOfArticles);
		
		
//			articleMessage = articleMessage + "<br>";
//			articleMessage = articleMessage + "-------------------------------------------<br>";
//			articleMessage = articleMessage + "Please note the recent Published Articles on IBM Bluemix : <br>";
			
			for (Article article : topArticles) {
				StringBuilder sb = new StringBuilder();
//				sb.append("Article : " + (numberOfArticles + 1));
//				sb.append("</div>");
//				sb.append("</div>");
				sb.append("<p> <a href="+article.getLink()+">"+article.getTitle()+"</a></p>");
//				sb.append("<p class=MsoNormal>Article on  :" + article.getTitle()+"</p><p class=MsoNormal>URL : "+ article.getLink() +"</p>");
//				
//				sb.append("</div>");
//				sb.append("<p class=MsoNormal>&nbsp;</p>");
				articleMessage = articleMessage + sb.toString();
				//System.out.println("------------> Append announcement - Message = "+ messageText); 
				numberOfArticles++;
			}
			
//			articleMessage = articleMessage + "-------------------------------------------<br>";
			finalDevWorks = finalDevWorks + articleMessage;
		
		
	}

}
