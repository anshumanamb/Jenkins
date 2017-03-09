package jenkins.jobs;

import java.io.IOException;
import java.util.regex.Pattern;
import com.offbytwo.jenkins.JenkinsServer;

public class JobsConfig extends JenkinsAuth {

	private JenkinsServer jenkins;

	JobsConfig(String Url) {
		this.jenkins = super.initJenkins(Url);
	}

	private final String PATTERN_TO_REPLACE = "<assignedNode>.*?</assignedNode>";
	private Pattern pattern;

	public void updateJobConfigWithDesiredSeleniumAgent(String jobName,
			String seleniumAgent) {
		pattern = Pattern.compile(PATTERN_TO_REPLACE);
		String getXmlOfTheJob = null;
		try {
			getXmlOfTheJob = getJobXml(jobName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String updatedXml = pattern.matcher(getXmlOfTheJob).replaceAll(
				"<assignedNode>" + seleniumAgent + "</assignedNode>");
		try {
			jenkins.updateJob(jobName, updatedXml);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getJobXml(String jobName) throws IOException {
		return jenkins.getJobXml(jobName);
	}
}
