package jenkins.jobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import net.sf.json.JSONException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import com.offbytwo.jenkins.model.Computer;
import com.offbytwo.jenkins.model.Job;

public class RunJenkins extends JenkinsAuth {

	private static String environment;
	private static String radiatorUrl;
	private static String genericUrl;
	public static PropertiesConfiguration envProperties;
	private static StringBuilder buf = new StringBuilder();
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
			InterruptedException, JSONException, org.json.JSONException {
		setUpEnvProperties();
		JobsMetadata metaData = new JobsMetadata(radiatorUrl);
		JobStatus jobStatus = new JobStatus(radiatorUrl);
//		Queue<Job> jobs = new LinkedList<Job>();
		Queue<String> jobs = metaData.getSortedQueueOfJobs();
//		jobs = metaData.getQueueOfJobs();
		runTheJobs(jobs);
		Thread.sleep(900000);
		jobs = jobStatus.getSortedQueueOfFailedJobs(metaData);
		runTheJobs(jobs);
	}

	public static void runTheJobs(Queue<String> jobs) throws IOException, InterruptedException{
		RunJobs runJobs = new RunJobs(genericUrl);
		JobsConfig config = new JobsConfig(radiatorUrl);
	ArrayList<Computer> agents = runJobs.getListOfOmniSeleniumAgents();
	System.out.println("Size of jobs: " + jobs.size());
	try{
	for (; jobs.size() != 0;) {
		System.out.println("This is inside for");
		for (Computer agent : agents) {
			Computer computer = agent;
			System.out.println("Is the selenium agent idle above if: "
					+ agent.getDisplayName() + "   "
					+ runJobs.isTheSeleniumAgentIdle(computer));
			if (runJobs.isTheSeleniumAgentIdle(agent)) {
				String jobName = jobs.poll();
				System.out.println("Size of queue:" + jobs.size());
				config.updateJobConfigWithDesiredSeleniumAgent(jobName,
						agent.getDisplayName());
				System.out.println("Running job: " + jobName
						+ " on Agent: |" + agent.getDisplayName() + "|");
				Thread.sleep(5000);
				runJobs.runJob(jobName);
			}
			Thread.sleep(5000);
		}
	}
	}
	catch(NullPointerException e){
//	failed();
	}
	}

	public static void failed() throws IOException, InterruptedException {
		ArrayList<Job> failedJobs = new ArrayList<Job>();
		JobsMetadata metaData = new JobsMetadata(radiatorUrl);
		JobStatus jobStatus = new JobStatus(radiatorUrl);
		failedJobs = jobStatus.getListOfFailedJobs(metaData);
		Map<String, List<String>> failuresScreenshotUrls = new HashMap<String, List<String>>();
		Integer skippedCount;
		Integer failureCount;
		String html = "";
		generateHtmlTable();
//		for (Job job : failedJobs) {
			for (int i = 0; i<failedJobs.size();i++){
				Job job = new Job();
				job = failedJobs.get(i+1);
			System.out.println("*******************This is the next Job******************");
			String emailableReport;
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			System.out.println("&&&&&&&&&&&&The Next Job&&&&&&&&&&&"+job.getName());
//			failedJobs.get(i) = job;
			try{
			System.out.println("&&&&&&&&&&&&The Next Job&&&&&&&&&&&"+job.getUrl());}
			catch (Exception e){
				e.printStackTrace();
			}
			System.out.println("---------Report-----------"+job
					.getFileFromWorkspace("omni-selenium-regression-tests/target/screenshots"));
//			emailableReport = job1.details().getBuilds().get(0).details().getConsoleOutputHtml();
//			System.out.println("------------This is the emailable reprot-----------");
//			skippedCount = Integer.valueOf(StringUtils.substringBetween(
//					emailableReport, "Skipped: ", ","));
//			failureCount = Integer.valueOf(StringUtils.substringBetween(
//					emailableReport, "Failures: ", ","));
//			System.out.println("Failure Count====" + failureCount);
//			System.out.println("Skipped Count====" + skippedCount);
//
//			if (failureCount.intValue() == 0) {
//				continue;
//			}

			String screenshotPageSource = job
					.getFileFromWorkspace("omni-selenium-regression-tests/target/screenshots");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ArrayList<String> failedTestName = new ArrayList<String>();
			failedTestName = getFailedTestName(screenshotPageSource, job);
			Thread.sleep(5000);
			failuresScreenshotUrls = getFailedJobAndScreenshotUrl(job,
					failedTestName);
			System.out.println("This is to check the value::: "+failuresScreenshotUrls.entrySet().iterator().next().getValue());
			for (Map.Entry<String, List<String>> entry : failuresScreenshotUrls
					.entrySet()) {
				System.out.println("This is the third time::::");
				String key = entry.getKey();
				List<String> values = entry.getValue();
				System.out.println("Value::::::"+values.get(0));
				for (String value : entry.getValue()) {
					System.out.println("Job name======" + key + "     "
							+ "\nScreenshot URL====" + value);
//					buf.append("<tr><td>")
//							.append(key)
//							.append("</td><td>")
//							.append(StringUtils.substringBetween(value,
//									"Test_", ".png")).append("</td><td>")
//							.append("<a href=" + value + ">Screenshot</a>")
//							.append("</td></tr>");
				}

			}

		}
		buf.append("</table>" + "</body>" + "</html>");
		html = buf.toString();
		generateHtmlReport(html);
	}

	private static StringBuilder generateHtmlTable() {
		return buf.append("<html>" + "<body>" + "<table border = '1'>" + "<tr>"
				+ "<th>Job Name</th>" + "<th>Failure</th>"
				+ "<th>Screenshot Link</th>" + "</tr>");
	}

	private static ArrayList<String> getFailedTestName(String pageSource,
			Job job) {
		ArrayList<String> failedTestName = new ArrayList<String>();
		while (pageSource.contains(".png</a></td><td class=\"fileSize\">")) {
			failedTestName.add(StringUtils.substringBetween(pageSource,
					".png\">", ".png</a></td><td class=\"fileSize\">"));
			pageSource = StringUtils.substringAfter(pageSource,
					".png</a></td><td class=\"fileSize\">");
		}
		return failedTestName;
	}

	private static void generateHtmlReport(String html) throws IOException {
		String file = "D:/TheHtmlReport.html";
		File newTextFile = new File(file);
		OutputStream fos = new FileOutputStream(newTextFile.getAbsoluteFile(),
				true);
		Writer writer = new OutputStreamWriter(fos);
		PrintWriter pw = new PrintWriter(newTextFile);
		pw.print("");
		pw.close();
		writer.write(html);
		writer.close();
	}

	private static Map<String, List<String>> getFailedJobAndScreenshotUrl(
			Job job, ArrayList<String> failedScr) throws IOException {
		String jobName;
		String currentUrl = job.getUrl().replace(
				"http://jenkins-as01.gale.web:8080/", radiatorUrl);
		System.out.println("Vaule of job------" + job);
		jobName = job.getName();
		System.out.println("Job Name========" + jobName);
		System.out.println("currentUrl========" + currentUrl);
		List<String> failedScrUrl = new ArrayList<String>();
		Map<String, List<String>> failures = new HashMap<String, List<String>>();
		for (String failed : failedScr) {
			String screenshotURL = currentUrl + screenshotPath + failed
					+ ".png";
			failedScrUrl.add(screenshotURL);
		}
		failures.put(jobName, failedScrUrl);
		return failures;
	}
}
