package jenkins.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import com.offbytwo.jenkins.model.Computer;
import com.offbytwo.jenkins.model.Job;

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
					System.out.println("Size og queue========" + jobs.size());
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
}
