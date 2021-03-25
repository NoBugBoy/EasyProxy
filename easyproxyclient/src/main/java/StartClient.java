import core.ClientInitializer;
import core.NettyClientServer;
import io.netty.util.internal.StringUtil;
import org.apache.commons.cli.*;
import org.yaml.snakeyaml.Yaml;
import utils.YamlEntity;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Author yujian
 * Description 客户端cli
 * Date 2021/2/2
 */
public class StartClient {
    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("h", false, "Help");
        options.addOption("server", true, "remote server host : port");
        options.addOption("local", true, "local service host : port");
        options.addOption("pp", true, "remote proxy port");
        options.addOption("yml", false, "specify the YML configuration file");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd        = parser.parse(options, args);
        String serverHost = null, localHost = null;
        int    serverPort = 0, localPort = 0, proxyPort;
        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("options", options);
        } else {
            boolean yml = cmd.hasOption("yml");
            if(yml){
                Yaml yam = new Yaml();
                try(FileInputStream fileInputStream = new FileInputStream(
                    System.getProperty("user.dir") + "/"+ "proxy.yml")) {
                    YamlEntity yamlEntity = yam.loadAs(fileInputStream, YamlEntity.class);
                    List<Map<String, Object>> proxys = yamlEntity.getProxys();
                    for (Map<String, Object> proxy : proxys) {
                        String server = (String)proxy.get("server");
                        String[] serverSplit = server.split(":");
                        if(serverSplit.length != 2){
                            System.err.println("remote server host : port Format error");
                            System.exit(1);
                        }
                        serverHost = serverSplit[0];
                        serverPort = Integer.parseInt(serverSplit[1]);
                        String localHostAndPort = (String)proxy.get("local");
                        String[] localSplit = localHostAndPort.split(":");
                        if(localSplit.length != 2){
                            System.err.println("remote server host : port Format error");
                            System.exit(1);
                        }
                        localHost  = localSplit[0];
                        localPort = Integer.parseInt(localSplit[1]);
                        proxyPort = Integer.parseInt(String.valueOf(proxy.get("proxyPort")));
                        NettyClientServer nettyClientServer = new NettyClientServer();
                        nettyClientServer.start(serverHost, serverPort,
                            new ClientInitializer(localHost, localPort, proxyPort));
                        System.out.println("connection server" + serverHost + ":" + serverPort + " success ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                String serverHostAndPort = cmd.getOptionValue("server");
                if(StringUtil.isNullOrEmpty(serverHostAndPort)){
                    System.err.println("remote server host : port cannot be null");
                    System.exit(1);
                }else{
                    String[] serverSplit = serverHostAndPort.split(":");
                    if(serverSplit.length != 2){
                        System.err.println("remote server host : port Format error");
                        System.exit(1);
                    }
                    serverHost = serverSplit[0];
                    serverPort = Integer.parseInt(serverSplit[1]);
                }
                String localHostAndPort = cmd.getOptionValue("local");
                if(StringUtil.isNullOrEmpty(localHostAndPort)){
                    System.err.println("local service host : port cannot be null");
                    System.exit(1);
                }else{
                    String[] localSplit = localHostAndPort.split(":");
                    if(localSplit.length != 2){
                        System.err.println("remote server host : port Format error");
                        System.exit(1);
                    }
                    localHost  = localSplit[0];
                    localPort = Integer.parseInt(localSplit[1]);
                }
                String proxy = cmd.getOptionValue("pp");
                if(StringUtil.isNullOrEmpty(proxy)){
                    System.err.println("remote proxy port cannot be null");
                    System.exit(1);
                }
                proxyPort = Integer.parseInt(proxy);
                NettyClientServer nettyClientServer = new NettyClientServer();
                nettyClientServer.start(serverHost, serverPort,
                    new ClientInitializer(localHost, localPort, proxyPort));

                System.out.println("connection server" + serverHost + ":" + serverPort + " success ");
            }

        }
        // NettyClientServer nettyClientServer = new NettyClientServer();
        //     nettyClientServer.start("localhost",18888,new ClientInitializer("localhost",8081,9001));
    }
}
