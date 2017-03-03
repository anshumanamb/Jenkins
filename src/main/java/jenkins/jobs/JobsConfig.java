package jenkins.jobs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Pattern;
import com.offbytwo.jenkins.JenkinsServer;

public class JobsConfig {

	private JenkinsServer jenkins;

	private final String PATTERN_TO_REPLACE = "<assignedNode>.*?</assignedNode>";
	private Pattern pattern;

	public JobsConfig(JenkinsServer jenkins) throws MalformedURLException {
		this.jenkins = jenkins;
	}

	public void updateJobConfigWithDesiredSeleniumAgent(String jobName,
			String seleniumAgent) {
		pattern = Pattern.compile(PATTERN_TO_REPLACE);
		String getXmlOfTheJob = null;
		try {
			getXmlOfTheJob = getJobXml(jobName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		pattern.matcher(getXmlOfTheJob).replaceAll(
				"<assignedNode>" + seleniumAgent + "</assignedNode>");
	}

	public String getJobXml(String jobName) throws IOException {
		System.out.println(jenkins.getJobXml(jobName));
		return jenkins.getJobXml(jobName);
	}
}
