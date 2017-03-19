package jenkins.jobs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import com.offbytwo.jenkins.model.Computer;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.TestReport;

public class RunJenkins extends JenkinsAuth {

	private static String environment;
	private static String radiatorUrl;
	private static String genericUrl;
	public static PropertiesConfiguration envProperties;

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
//		failedJobs = jobStatus.getListOfFailedJobs(metaData);
//		for (Job job : failedJobs){
//			System.out.println("Failed job======"+job.getName());
//			job.details().getLastBuild().getTestReport();
	}
	
	public static void failed() throws IOException{
		ArrayList<Job> failedJobs = new ArrayList<Job>();
		JobsMetadata metaData = new JobsMetadata(radiatorUrl);
		JobStatus jobStatus = new JobStatus(radiatorUrl);
		failedJobs = jobStatus.getListOfFailedJobs(metaData);
		for (Job job : failedJobs){
			System.out.println("Failed job======"+job.getName()+"\n");
//			int status = job.details().getLastBuild().getClient().getStatusLine().getStatusCode();
			try {
				String output = job.getFileFromWorkspace("omni-selenium-regression-tests/target/screenshots");//failsafe-reports/emailable-report.html
				//.details().getDescription().getLastBuild().details().getConsoleOutputHtml();
				System.out.println("URL========"+job.getUrl());
				System.out.println("Test report========"+output);
				ArrayList<String> failedScr = new ArrayList<String>();
				failedScr.add(StringUtils.substringBetween(output,"png\" /></td><td><a href=\"", ".png"));
				System.out.println("FAILED=-=========="+failedScr.get(0));
				
				 File newTextFile = new File("D:/thetextfile.html");
		            FileWriter fw = new FileWriter(newTextFile);
		            fw.write(output);
		            fw.close();
		            break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	
	/*public static void failed() throws IOException {
		ArrayList<Job> failedJobs = new ArrayList<Job>();
		JobsMetadata metaData = new JobsMetadata(radiatorUrl);
		JobStatus jobStatus = new JobStatus(radiatorUrl);
		failedJobs = jobStatus.getListOfFailedJobs(metaData);
		for (Job job : failedJobs) {
			System.out.println("Failed job======" + job.getName() + "\n");
			try {
				String output = job
						.getFileFromWorkspace("omni-selenium-regression-tests/target/screenshots");// failsafe-reports/emailable-report.html
				ArrayList<String> failedScr = new ArrayList<String>();
				String currentUrl = null;
				while (output.contains("png\" /></td><td><a href=\"")) {
					currentUrl = job
							.getUrl()
							.replace("http://jenkins.gale.web:8080/", "")
							.replace("_Suite",
									"_Suite/ws/omni-selenium-regression-tests/target/screenshots");
					failedScr.add(StringUtils.substringBetween(output,
							"png\" /></td><td><a href=\"", ".png"));
					output = StringUtils.substringAfter(output,
							"png\" /></td><td><a href=\"");
				}
				for (String failed : failedScr) {
					String screenshotURL = radiatorUrl + currentUrl + failed;
					System.out.println("New URL================"
							+ screenshotURL + ".png");
				}
				File newTextFile = new File("D:/thetextfile.html");
				FileWriter fw = new FileWriter(newTextFile);
				fw.write(output);
				fw.close();
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
}
