import org.apache.commons.cli.*;
import core.KeepAlive;
import core.NettyServer;
import core.ServerInitializer;

public class StartServer {
    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("h", false, "Help");
        options.addOption("port", true, "server port");
        options.addOption("sync", false, "open sync, the response time can be controlled");
        options.addOption("time", false, "waiting for response time (SECONDS)");

        CommandLineParser parser = new DefaultParser();
        CommandLine       cmd    = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("options", options);
        } else {
            int port = Integer.parseInt(cmd.getOptionValue("port", "9675"));
            int time = Integer.parseInt(cmd.getOptionValue("time", "3"));
            boolean sync = cmd.hasOption("sync");
            NettyServer nettyServer = new NettyServer();
            nettyServer.start(new ServerInitializer(time,sync),port);
            System.out.println("server started "+(sync?"sync":"")+" on port " + port);

            new KeepAlive().ping();
            System.out.println("keepalive started");
        }
        // NettyServer nettyServer = new NettyServer();
        // nettyServer.start(new ServerInitializer(3,false),18888);
        // new KeepAlive().ping();
        //     System.out.println("keepalive started");
    }
}
