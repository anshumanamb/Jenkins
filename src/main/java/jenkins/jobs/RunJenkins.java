package jenkins.jobs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Computer;
import com.offbytwo.jenkins.model.Job;

public class RunJenkins extends JenkinsAuth{
	
	JobsConfig config;
	
	RunJenkins() {
		super.initJenkins();
		config = instantiatePage(jenkins,JobsConfig.class);
	}
	
	
			
	public static void main(String args[]) throws IOException{
		RunJenkins auth = new RunJenkins();
//		JenkinsServer jenkins = authenticateJenkins(auth.url, auth.userName, auth.passWord);
		
		String getJobXml = auth.config.getJobXml("COMPLETE_AONE_QA3");
		String patternToReplace = "<assignedNode>.*?</assignedNode>";
		Pattern pattern = Pattern.compile(patternToReplace);
		String seleniumAgent = "SeleniumAgent-Omni1";
		String updatedXml = pattern.matcher(getJobXml).replaceAll("<assignedNode>"+seleniumAgent+"</assignedNode>");
//		System.out.println(getJobXml);
		System.out.println(updatedXml);
//		jenkins.updateJob("COMPLETE_AONE_QA3", updatedXml);
//		jenkins.getJob("COMPLETE_AONE_QA3").build();
//		Iterator <Computer> itr = jenkins.getComputers().values().iterator();
//		while(itr.hasNext()){
//		Computer computer = itr.next();
//		computer.details();
//		System.out.println("Computer========"+computer.details().getIdle());
//		}
//		System.out.println("Jobs==="+jenkins.updateJob(jobName, jobXml););//getComputerSet().getDisplayName());//getJobs().size());
//		Iterator <Job> itr = (Iterator<Job>) jenkins.getJobs().values().iterator();
//
//		while(itr.hasNext()){
//			Job job = itr.next();
////			if(job.details().getLastBuild().details().getResult().toString().contains("FAILURE"))
//			String jobName = job.getName();
//			System.out.println(jobName);
//			System.out.println("Description==="+job.details());
//		}
//		System.out.println("----------"+jenkins.getJobs().values().iterator().next().details().getLastBuild().details().getResult());
	}
}
