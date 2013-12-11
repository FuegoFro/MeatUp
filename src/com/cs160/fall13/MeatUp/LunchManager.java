package com.cs160.fall13.MeatUp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LunchManager {
    private static final String LUNCHES_PREFS = "meatup saved lunches";
    private static final String LUNCHES_SET = "lunches set";
    private static final String LUNCH_ID_DELIM = ";";

    public static void addLunch(Context context, Lunch lunch) {
        SharedPreferences prefs = context.getSharedPreferences(LUNCHES_PREFS, Context.MODE_PRIVATE);
        Set<Integer> lunchIds = getLunchIds(prefs);
        int id = lunch.getId();
        lunchIds.add(id);

        SharedPreferences.Editor editor = prefs.edit();
        putLunch(editor, lunch);
        setLunchIds(editor, lunchIds);
        editor.commit();
    }

    public static void removeLunch(Context context, int id) {
        SharedPreferences prefs = context.getSharedPreferences(LUNCHES_PREFS, Context.MODE_PRIVATE);
        Set<Integer> lunchIds = getLunchIds(prefs);
        lunchIds.remove(id);

        SharedPreferences.Editor editor = prefs.edit();
        setLunchIds(editor, lunchIds);
        editor.remove(String.valueOf(id));
        editor.commit();
    }

    public static List<Lunch> getLunches(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(LUNCHES_PREFS, Context.MODE_PRIVATE);
        Set<Integer> lunchIds = getLunchIds(prefs);
        ArrayList<Lunch> lunches = new ArrayList<Lunch>();
        for (Integer lunchId : lunchIds) {
            lunches.add(getLunch(prefs, lunchId));
        }
        return lunches;
    }

    private static Set<Integer> getLunchIds(SharedPreferences preferences) {
        // Hack to store sets/lists of objects on gingerbread
        String lunchIdStrings = preferences.getString(LUNCHES_SET, null);
        if (lunchIdStrings == null) {
            return new HashSet<Integer>();
        }
        String[] splitLunchIdStrings = lunchIdStrings.split(LUNCH_ID_DELIM);
        Set<Integer> lunchIds = new HashSet<Integer>();
        for (String idString : splitLunchIdStrings) {
            lunchIds.add(Integer.parseInt(idString));
        }
        return lunchIds;
    }

    private static void setLunchIds(SharedPreferences.Editor editor, Set<Integer> lunchIds) {
        // Hack to store sets/lists of objects on gingerbread
        // Note that you have to commit the editor afterword
        StringBuilder lunchIdStrings = new StringBuilder();
        boolean first = true;
        for (Integer lunchId : lunchIds) {
            if (!first) {
                lunchIdStrings.append(LUNCH_ID_DELIM);
            }
            first = false;
            lunchIdStrings.append(lunchId);
        }
        editor.putString(LUNCHES_SET, lunchIdStrings.toString());
    }

    private static void putLunch(SharedPreferences.Editor editor, Lunch lunch) {
        // Serializes an object to a string and puts the string in the shared preferences

        String id = String.valueOf(lunch.getId());

        try {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(arrayOutputStream);
            objectOutput.writeObject(lunch);
            byte[] data = arrayOutputStream.toByteArray();
            objectOutput.close();
            arrayOutputStream.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Base64OutputStream b64 = new Base64OutputStream(out, Base64.DEFAULT);
            b64.write(data);
            b64.close();
            out.close();

            editor.putString(id, new String(out.toByteArray()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Lunch getLunch(SharedPreferences preferences, int id) {
        // Deserialize an object from a string in the shared preferences

        byte[] bytes = preferences.getString(String.valueOf(id), "").getBytes();
        if (bytes.length == 0) {
            return null;
        }
        ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
        Base64InputStream base64InputStream = new Base64InputStream(byteArray, Base64.DEFAULT);
        ObjectInputStream in;
        Lunch lunch = null;
        try {
            in = new ObjectInputStream(base64InputStream);
            lunch = (Lunch) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return lunch;
    }
}
