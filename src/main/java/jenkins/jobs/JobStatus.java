package jenkins.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Job;

public class JobStatus extends JenkinsAuth{
	
	private JenkinsServer jenkins;
	
	

	JobStatus(String Url) {
		this.jenkins = super.initJenkins(Url);
	}

	public String getResultOfJob(Job job) {
		String status;
		try {
			status = job.details().getLastBuild().details().getResult().toString();
			return status;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Queue<Job> getQueueOfFailedJobs(JobsMetadata metaData) throws InterruptedException {
		Queue<Job> failedJobs = new LinkedList<Job>();
		Iterator<Job> totalJobs = metaData.getListOfJobs();
		Job job;
		while (totalJobs.hasNext()){
			job = totalJobs.next();
			System.out.println("Status====="+getResultOfJob(job));
			if(getResultOfJob(job).equals("FAILURE")){
				Thread.sleep(3000);
				failedJobs.add(job);
			}
		}
		return failedJobs;
	}
	
	public Queue<String> getSortedQueueOfFailedJobs(JobsMetadata metaData) throws InterruptedException {
		Queue<String> failedJobs = new LinkedList<String>();
		ArrayList<String> list = new ArrayList<String>();
		Iterator<Job> totalJobs = metaData.getListOfJobs();
		Job job;
		while (totalJobs.hasNext()){
			job = totalJobs.next();
			System.out.println("Status====="+getResultOfJob(job));
			if(getResultOfJob(job).equals("FAILURE")){
				Thread.sleep(3000);
//				failedJobs.add(job);
				try {
					list.add(job.details().getDisplayName());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		for(String failed : list){
			failedJobs.add(failed);
		}
		return failedJobs;
	}
	
	public ArrayList<Job> getListOfFailedJobs(JobsMetadata metaData) throws InterruptedException {
		ArrayList<Job> failedJobs = new ArrayList<Job>();
		Iterator<Job> totalJobs = metaData.getListOfJobs();
		Job job;
		while (totalJobs.hasNext()){
			job = totalJobs.next();
			System.out.println("Status====="+getResultOfJob(job));
			if(getResultOfJob(job).equals("FAILURE")){
				Thread.sleep(3000);
				failedJobs.add(job);
			}
		}
		return failedJobs;
	}
	
	public void getTestReport(String jobName){
		System.out.println("Jenkins=========="+jenkins);
//			System.out.println("Test report========="+jenkins.getJob(jobName).details().getLastBuild().getTestResult().getFailCount());
	}
}
