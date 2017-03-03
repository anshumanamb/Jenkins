package jenkins.jobs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import com.offbytwo.jenkins.JenkinsServer;

 class JenkinsAuth {
	protected JenkinsServer jenkins;
	private URI jenkinURI;
	private URL url;
	private PropertiesConfiguration urlProperties;
	private PropertiesConfiguration authProperties;

	String jenkinsUrl;
	String userName;
	String passWord;

	JenkinsAuth() {
		try {
			urlProperties = new PropertiesConfiguration(
					"environment.properties");
			authProperties = new PropertiesConfiguration("auth.properties");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		jenkinsUrl = urlProperties.getString("jenkins.qa");
		userName = authProperties.getString("jenkins.username");
		passWord = authProperties.getString("jenkins.password");
	}

	public void initJenkins() {
		try {
			url = new URL(jenkinsUrl);
			jenkinURI = url.toURI();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException uriException) {
			uriException.printStackTrace();
		}
		jenkins = new JenkinsServer(jenkinURI, userName, passWord);
	}

	protected <T> T instantiatePage(JenkinsServer jenkins,
			Class<T> pageClassToProxy) {
		try {
			try {
				Constructor<T> constructor = pageClassToProxy
						.getConstructor(JenkinsServer.class);
				return constructor.newInstance(jenkins);
			} catch (NoSuchMethodException e) {
				return pageClassToProxy.newInstance();
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}