# Excalibot
Bruteforce plugin

SSH Bruteforce plugin, returns the password of an ssh server.

## Usage
	bruteforce host:<address> port:<port> bots:<number of bots> user:<ssh username> dict:<dictionary of passwords>

## Dependencies

Needs common.jar from common and jsch-0.1.53.jar from jsch
Also the manifest file needs to declare the path to the starting classes
as well as the command the plugin responds to.
		
    Manifest-Version: 1.0
    Command: bruteforce
    ArthurPlug: org.powertrip.excalibot.common.plugins.bruteforce.Server
    KnightPlug: org.powertrip.excalibot.common.plugins.bruteforce.Bot



		String host = subTask.getParameter("host");
		String user = subTask.getParameter("user");
        String port = subTask.getParameter("port");
        String dict = subTask.getParameter("dict");
		String lowerBound = subTask.getParameter("lowerBound");
        String higherBound = subTask.getParameter("higherBound");


	