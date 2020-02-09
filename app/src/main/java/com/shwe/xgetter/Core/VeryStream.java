package com.shwe.xgetter.Core;


import com.shwe.xgetter.Model.XModel;
import com.shwe.xgetter.Utils.Utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VeryStream {
    public static ArrayList<XModel> fetch(String response) {
        ArrayList<XModel> xModels = new ArrayList<>();
        String token = get_token(response);
        if (token != null) {
            Utils.putModel("https://verystream.com/gettoken/" + token, "Normal", xModels);
            return xModels;
        }
        return null;
    }


    private static String get_token(String string) {
        final String regex = "videolink\">(.*)<";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
