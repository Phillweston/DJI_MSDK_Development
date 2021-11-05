package com.ew.autofly.utils.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public class CollectionsUtil {

    /**
     * list深拷贝
     *
     * @param src
     * @param <T>
     * @return
     * @throws IOException
     * @throws
     */
    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }


    public static <T> ArrayList<T> convertToChild(List<? super T> tList) {
        ArrayList<T> childList = new ArrayList<>();
        if (tList != null) {
            for (Object superObj : tList) {
                childList.add((T) superObj);
            }
        }
        return childList;
    }


    private static <T> ArrayList<? super T> convertToParent(List<T> tList) {
        ArrayList<? super T> superList = new ArrayList<>();
        if (tList != null) {
            superList.addAll(tList);
        }
        return superList;
    }

    public static <T> List<List<T>> spiltList(List<T> allData, int size) {
        List<List<T>> result = new ArrayList<>();
        for (int begin = 0; begin < allData.size(); begin = begin + size) {
            int end = (begin + size > allData.size() ? allData.size() : begin + size);
            result.add(allData.subList(begin, end));
        }
        return result;
    }
}
