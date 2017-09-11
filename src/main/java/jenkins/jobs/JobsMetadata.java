package jenkins.jobs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimeZone;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import net.sf.json.JSONException;
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

	public Queue<String> getSortedQueueOfJobs() throws JSONException,
			org.json.JSONException {
		ArrayList<String> jobName = new ArrayList<String>();
		Queue<String> queue = new LinkedList<String>();
		Iterator<Job> jobs = getListOfJobs();
		while (jobs.hasNext()) {
			try {
				extractTime();
				jobName.add(jobs.next().details().getDisplayName());
				Job job1 = jobs.next();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"MMM dd yyyy HH:mm:ss z"); // the format of your date
				sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
				String formattedDate = sdf.format(new java.util.Date((job1
						.details().getLastBuild().details().getTimestamp())));
				System.out.println(formattedDate);
				System.out.println("-----------"
						+ job1.details().getDisplayName());
				// System.out.println("-----------"+new
				// java.util.Date((long)(job1.details().getLastBuild().details().getTimestamp())));;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Collections.sort(jobName, (a, b) -> b.compareTo(a));
		for (String job : jobName) {
			queue.add(job);
		}
		return queue;
	}

	public Queue<String> getDesiredJobs() {
		Queue<String> queue = new LinkedList<String>();
		queue.add("SMOKE_SBRC_PROD_Suite");
		return queue;
	}
	
	public static void extractTime() throws MalformedURLException, IOException{
		try{
		Document doc = Jsoup.connect("").get();
		Elements element = doc.select("#buildDate");
			System.out.println(StringUtils.substringAfter(element.text(), " ").replace("EDT", ""));
		}
		catch(Exception e){
		}
	}

}
