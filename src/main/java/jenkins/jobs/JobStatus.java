package jenkins.jobs;

import java.io.IOException;

import com.offbytwo.jenkins.model.Job;

public class JobStatus {
	
	public String getResultOfJob(Job job) throws IOException{
		String status = job.details().getLastBuild().details().getResult().toString();
			return status;
	}
}
