package jenkins.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Computer;

public class RunJobs extends JenkinsAuth {

	private JenkinsServer jenkins;

	RunJobs(String Url) {
		this.jenkins = super.initJenkins(Url);
	}

	public void runJob(String jobName) {
		try {
			jenkins.getJob(jobName).build();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Iterator<Computer> getListOfAllAgents() {
		try {
			return jenkins.getComputers().values().iterator();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<Computer> getListOfOmniSeleniumAgents() {
		String computerName;
		ArrayList<Computer> computers = new ArrayList<Computer>();
		Iterator<Computer> totalComputers = getListOfAllAgents();
		while (totalComputers.hasNext()) {
			Computer currentComputer = totalComputers.next();
			computerName = currentComputer.getDisplayName();
			if (computerName.equals("SeleniumAgent-Omni1")
					|| computerName.equals("SeleniumAgent-Omni2")
					|| computerName.equals("SeleniumAgent-Omni3")
					|| computerName.equals("SeleniumAgent-Omni4")
					|| computerName.equals("SeleniumAgent-Omni5")) {
				computers.add(currentComputer);
			}
		}
		return computers;
	}

	public boolean isTheSeleniumAgentIdle(Computer computer) {
		try {
			if (computer.details().getIdle())
				return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
