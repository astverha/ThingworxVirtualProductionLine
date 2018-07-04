package configuration;

import java.util.List;
import org.slf4j.LoggerFactory;

public class ConfigurationAgent {
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ConfigurationAgent.class);
    
    private final String serverName;
    private final String appKey;
    private List<Line> lines;
    
    public ConfigurationAgent(String fileName){
        try {
            ClassLoader loader = this.getClass().getClassLoader();
            File xmlFile = new File(loader.getResource(fileName).getFile());
            
            lines = ReadXML.read(xmlFile);
            this.serverName = ReadXML.getServerName();
            this.appKey = ReadXML.getAppKey();
        } c
    }
}
