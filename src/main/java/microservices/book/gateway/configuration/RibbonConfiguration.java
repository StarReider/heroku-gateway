package microservices.book.gateway.configuration;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PingUrl;
import com.netflix.loadbalancer.Server;

public class RibbonConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(RibbonConfiguration.class);

	@Bean
	public IPing ribbonPing(final IClientConfig config) {
		return new PingUrl(false,"/actuator/health") {
			@Override
			public boolean isAlive(Server server) {

				String urlStr   = "";
				if (false){
					urlStr = "https://";
				}else{
					urlStr = "http://";
				}
				urlStr += server.getId();
				urlStr += getPingAppendString();

				boolean isAlive = false;

				HttpClient httpClient = new DefaultHttpClient();
				HttpUriRequest getRequest = new HttpGet(urlStr);
				System.out.println("[TEST]Request to " + urlStr);
				String content=null;
				try {
					HttpResponse response = httpClient.execute(getRequest);
					content = EntityUtils.toString(response.getEntity());
					System.out.println("[TEST]content " + content);
					isAlive = (response.getStatusLine().getStatusCode() == 200);
					if (getExpectedContent()!=null){
						System.out.println("[TEST]content:" + content);
						if (content == null){
							isAlive = false;
						}else{
							if (content.equals(getExpectedContent())){
								isAlive = true;
							}else{
								isAlive = false;
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					// Release the connection.
					getRequest.abort();
				}

				return isAlive;
		
			}
		};
	}
	
	@Bean
	public IRule ribbonRule(final IClientConfig config) {
		return new AvailabilityFilteringRule();
	}
}