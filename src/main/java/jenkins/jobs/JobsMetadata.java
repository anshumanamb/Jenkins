package jenkins.jobs;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Job;

public class JobsMetadata extends JenkinsAuth {

	private JenkinsServer jenkins;

	JobsMetadata(String Url) {
		this.jenkins = super.initJenkins(Url);
	}

	public Iterator<Job> getListOfJobs() {
		try {
			return jenkins.getJobs().values().iterator();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Queue<Job> getQueueOfJobs() {
		Queue<Job> queue = new LinkedList<Job>();
		Iterator<Job> jobs = getListOfJobs();
		while (jobs.hasNext()) {
			queue.add(jobs.next());
		}
		return queue;
	}

	public Queue<String> getDesiredJobs() {
		Queue<String> queue = new LinkedList<String>();
		queue.add("SMOKE_SBRC_PROD_Suite");
		return queue;
	}

}
