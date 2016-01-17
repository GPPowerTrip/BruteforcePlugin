# Excalibot
## Bruteforce plugin

SSH Bruteforce plugin, returns the password of an ssh server.

## Usage
	bruteforce host:<target_address> port:<target_port> bots:<number_of_bots> user:<ssh_user> dict:<dictionary_address>

The dictionary_address is the url to a text file with a list of words, each in a separate line.

## Dependencies

common.jar and jsch-0.1.53.jar
The manifest file needs to declare the path to the starting classes as well as the command the plugin responds to.
		
	Manifest-Version: 1.0
	Command: bruteforce
	ArthurPlug: org.powertrip.excalibot.common.plugins.bruteforce.Server
	KnightPlug: org.powertrip.excalibot.common.plugins.bruteforce.Bot

