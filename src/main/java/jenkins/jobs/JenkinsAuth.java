package jenkins.jobs;

import java.net.URI;
import java.net.URISyntaxException;

import com.offbytwo.jenkins.JenkinsServer;


public class JenkinsAuth
{
	private JenkinsServer jenkins;
	private URI jenkinURL;
	
	public JenkinsServer authenticateJenkins(String URL, String username, String password) {
		try {
			jenkinURL = new URI(URL);
		} catch(URISyntaxException uriException) {
			uriException.printStackTrace();
		}
		
		jenkins = new JenkinsServer(jenkinURL, username, password);
		
		return jenkins;
	}
}
