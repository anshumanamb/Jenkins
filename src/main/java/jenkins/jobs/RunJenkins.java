package jenkins.jobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpResponseException;

import com.offbytwo.jenkins.model.Computer;
import com.offbytwo.jenkins.model.Job;

public class RunJenkins extends JenkinsAuth {

	private static String environment;
	private static String radiatorUrl;
	private static String genericUrl;
	public static PropertiesConfiguration envProperties;
	private Job job1;

	private static String screenshotPath = "ws/omni-selenium-regression-tests/target/screenshots/";

	protected static void setUpEnvProperties() {
		try {
			envProperties = new PropertiesConfiguration(
					"environment.properties");
			environment = System.getProperty("env");
			System.out.println("Env======" + environment);
			radiatorUrl = envProperties.getString(environment + ".radiator");
			System.out.println("URL======" + radiatorUrl);
			genericUrl = envProperties.getString("jenkins.generic");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws IOException,
			InterruptedException {
		setUpEnvProperties();
		JobsConfig config = new JobsConfig(radiatorUrl);
		RunJobs runJobs = new RunJobs(genericUrl);
		JobsMetadata metaData = new JobsMetadata(radiatorUrl);
		failed();
		Queue<Job> jobs = metaData.getQueueOfJobs();
		Queue<String> desiredJobs = metaData.getDesiredJobs();
		ArrayList<Computer> agents = runJobs.getListOfOmniSeleniumAgents();
		System.out.println("Size of jobs: " + jobs.size());
		for (; desiredJobs.size() != 0;) {
			System.out.println("This is inside for");
			for (Computer agent : agents) {
				Computer computer = agent;
				System.out.println("Is the selenium agent idle above if: "
						+ agent.getDisplayName() + "   "
						+ runJobs.isTheSeleniumAgentIdle(computer));
				if (runJobs.isTheSeleniumAgentIdle(agent)) {
					String jobName = jobs.poll().details().getDisplayName();
					System.out.println("Size of queue:" + jobs.size());
					config.updateJobConfigWithDesiredSeleniumAgent(jobName,
							agent.getDisplayName());
					System.out.println("Running job: " + jobName
							+ " on Agent: |" + agent.getDisplayName() + "|");
					Thread.sleep(5000);
					runJobs.runJob(jobName);
				}
				Thread.sleep(8000);
			}
		}
	}

	public static void failed() throws IOException {
		ArrayList<Job> failedJobs = new ArrayList<Job>();
		ArrayList<String> skippedJobs = new ArrayList<String>();
		JobsMetadata metaData = new JobsMetadata(radiatorUrl);
		JobStatus jobStatus = new JobStatus(radiatorUrl);
		failedJobs = jobStatus.getListOfFailedJobs(metaData);
		Map<String, List<String>> failures = new HashMap<String, List<String>>();
//		String emailableReport;
		Integer skippedCount;
		Integer failureCount;
		StringBuilder buf = new StringBuilder();
		buf.append("<html>" +
		           "<body>" +
		           "<table border = '1'>" +
		           "<tr>" +
		           "<th>Job Name</th>" +
		           "<th>Failure</th>" +
		           "</tr>");
		for (Job job : failedJobs) {
			String emailableReport;
			System.out.println("This is instance of JOB========="+job.getName());
			System.out.println("Inside for==========");
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//			try{
//			System.out.println("Job name============"+job.getName());
//			}
//			catch (Exception e){
//				e.printStackTrace();
//			}
//			if(job.details().getDisplayName().equals("COMPLETE_GVRL_PROD_Suite"))
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("EMAILABLE REPORT======"+job.details().getBuilds().get(0).details().getConsoleOutputHtml());//getTestReport().getSkipCount());//getFileFromWorkspace("omni-selenium-regression-tests/target/failsafe-reports/emailable-report.html"));
				emailableReport = job.details().getBuilds().get(0).details().getConsoleOutputHtml();
				//emailableReport = job.getFileFromWorkspace("omni-selenium-regression-tests/target/failsafe-reports/emailable-report.html");
			System.out.println("==================================EMAILABLE REPORT=================================\n"+emailableReport);
			skippedCount = Integer.valueOf(StringUtils.substringBetween(emailableReport, "Skipped: ", ","));
			failureCount = Integer.valueOf(StringUtils.substringBetween(emailableReport, "Failures: ", ","));
//			skippedCount = Integer.valueOf(StringUtils.substringBetween(emailableReport, "class=\"numi_attn\">", "</td>"));
			System.out.println("Is it null============"+skippedCount);
			System.out.println("Skipped count========"+ skippedCount);
			
			if(failureCount.intValue() == 0)
				continue;
//			else if((skippedCount.intValue() != 0 && failureCount.intValue()>0) || failureCount.intValue()>0)
//				System.out.println("Job name under IF====="+ job.details().getDisplayName());
			
				
//			System.out.println("Fail count-------"+job.details().getLastBuild().details().getTestReport().getFailCount());
//			System.out.println("Skip count-------"+job.details().getLastBuild().getTestReport().getSkipCount());
//			System.out.println("\nFailed job======" + job.getName() + "\n");
			String html = "";
			try {
				String output = job
						.getFileFromWorkspace("omni-selenium-regression-tests/target/screenshots");// failsafe-reports/emailable-report.html
				ArrayList<String> failedScr = new ArrayList<String>();
				ArrayList<String> failedScrUrl = new ArrayList<String>();
				String currentUrl = null;
				String jobName;
				while (output.contains("png\" /></td><td><a href=\"")) {
//					System.out.println("Current URL: " + job.getUrl());
					// currentUrl = job
					// .getUrl()
					// .replace("http://jenkins.gale.web:8080/", "")
					// .replace("_Suite",
					// "_Suite/ws/omni-selenium-regression-tests/target/screenshots");
					currentUrl = job.getUrl().replace(
							"http://jenkins.gale.web:8080/", radiatorUrl);
					failedScr.add(StringUtils.substringBetween(output,
							"png\" /></td><td><a href=\"", ".png"));
					output = StringUtils.substringAfter(output,
							"png\" /></td><td><a href=\"");
				}
				jobName = job.details().getDisplayName();
				
				for (String failed : failedScr) {
					String screenshotURL = currentUrl + screenshotPath + failed + ".png";
					failedScrUrl.add(screenshotURL);
				}
				failures.put(jobName, failedScrUrl);
				for (Entry<String, List<String>> entry : failures.entrySet()) {
					System.out.println("Size of Map===="+failures.size());
				    String key = entry.getKey();
				    List<String> values = entry.getValue();
				    for(String value : values){
				    System.out.println("Job name======"+key+"     "+"\nScreenshot URL===="+value);
//				    StringBuilder buf = new StringBuilder();
//					buf.append("<html>" +
//					           "<body>" +
//					           "<table border = '1'>" +
//					           "<tr>" +
//					           "<th>Job Name</th>" +
//					           "<th>Failure</th>" +
//					           "</tr>");
					    buf.append("<tr><td>")
					       .append(key)
					       .append("</td><td>")
					       .append("<a href="+value+ ">Screenshot</a>")
//					       .append(value)
//					       .append("</td><td>")
					       .append("</td></tr>");
//					buf.append("</table>" +
//					           "</body>" +
//					           "</html>");
				    }
				    buf.append("</table>" +
					           "</body>" +
					           "</html>");
				    html = buf.toString();
				    // do something with key and/or tab
				}
//				Iterator itr=failures.keySet().iterator();
//				 while (itr.hasNext()) {
//				        String key =  itr.next().toString();
//				        String value=failures.get(key).toString();
//				        System.out.println(key+"="+value);
//				    }	
				String file = "D:/thetextfile.html";
				File newTextFile = new File(file);
//				FileWriter fw = new FileWriter(newTextFile);
				OutputStream fos =new FileOutputStream(newTextFile.getAbsoluteFile(), true) ;
				 Writer writer=new OutputStreamWriter(fos);
				 PrintWriter pw = new PrintWriter(newTextFile);
				 pw.print("");
				 pw.close();
				    writer.write(html);
				    writer.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		}
}
