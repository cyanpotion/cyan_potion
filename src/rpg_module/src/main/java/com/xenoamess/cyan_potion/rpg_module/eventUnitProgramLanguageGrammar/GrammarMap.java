package com.xenoamess.cyan_potion.rpg_module.eventUnitProgramLanguageGrammar;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XenoAmess
 */
public class GrammarMap {
    private GrammarMap() {
    }

    private static final Map<Integer, String> MethodNumToMethodNameMap =
            new HashMap<>();
    private static final Map<String, Integer> MethodNameToMethodNumMap =
            new HashMap<>();


    public static String getMethodName(int methodNum) {
        checkInit();
        return MethodNumToMethodNameMap.get(methodNum);
    }

    public static int getMethodNum(String methodName) {
        checkInit();
        return MethodNameToMethodNumMap.get(methodName);
    }

    private static void checkInit() {
        if (MethodNumToMethodNameMap == null || MethodNameToMethodNumMap == null) {
            int size = Grammar.class.getDeclaredFields().length;
            for (Field field : Grammar.class.getDeclaredFields()) {
                if (field.getType().equals(int.class) && field.getName().startsWith("G_")) {
                    String methodName = field.getName().substring(2);
                    int methodNum = -1;

                    try {
                        methodNum = (int) field.get(null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    GrammarMap.MethodNumToMethodNameMap.put(methodNum,
                            methodName);
                    GrammarMap.MethodNameToMethodNumMap.put(methodName,
                            methodNum);
                }
            }
        }
    }

    //    public static void main(String args[]) {
    //        System.out.println(getMethodName(101));
    //    }


    //    public static void main(String args[]) {
    //        checkInit();
    //        StringBuilder sb = new StringBuilder();
    //        TreeSet<Integer> seta = new TreeSet<Integer>
    //        (MethodNumToMethodNameMap.keySet());
    //        //        seta.addAll();
    //        for (int i = 0; i <= 1000; i++) {
    //            if (!seta.contains(i)) {
    //                sb.append("{\"code\":" + i + ", \"indent\":0,
    //                \"parameters\": []},");
    //            }
    //        }
    //        System.out.println(sb.toString());
    //    }

    //    public static void main(String args[]) {
    //        checkInit();
    //        System.out.println(MethodNumToMethodNameMap.size());
    //    }

}
