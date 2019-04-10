package microservices.book.gateway;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

@RestController
@RequestMapping("/eureka/servers")
public class TestController {
	@Autowired
	private EurekaClient eurekaClient;
	
	@GetMapping
	public List<InstanceInfo> getServers() {
        return eurekaClient.getInstancesByVipAddress("gamification", false, null);
	}
}
