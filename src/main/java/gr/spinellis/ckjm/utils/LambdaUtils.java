package gr.spinellis.ckjm.utils;

import com.sun.source.tree.Tree;
import gr.spinellis.ckjm.ClassMetrics;
import gr.spinellis.ckjm.TreeSetWithId;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;

import java.util.*;


public class LambdaUtils {
    public static void removeLambdas(
            List<TreeSetWithId<String>> mFieldsUsedByMethods,
            List<TreeSetWithId<String>> mMethodsUsedByMethods,
            ClassMetrics mClassMetrics,
            HashSet<String> mResponseSet,
            String className
    ) {
        // 建立一個 map 方便查找 id 對應的 TreeSetWithId
        Map<String, TreeSetWithId<String>> fieldMap = new HashMap<>();
        Map<String, TreeSetWithId<String>> methodMap = new HashMap<>();
        for (TreeSetWithId<String> ts : mFieldsUsedByMethods) {
            fieldMap.put(ts.getId(), ts);
        }
        for (TreeSetWithId<String> ts : mMethodsUsedByMethods) {
            methodMap.put(ts.getId(), ts);
        }

        for (TreeSetWithId<String> mFieldsUsedByMethod : fieldMap.values()) {
            String methodId = mFieldsUsedByMethod.getId();
            if (!methodId.startsWith("lambda$")) continue; // 只處理 lambda

            // 解析 lambda$run$0(I)V → 找到 run(I)V
            String outerId = extractOuterMethodIdFromLambda(methodId, mMethodsUsedByMethods);
            TreeSetWithId<String> fieldsUsed = fieldMap.get(methodId);
            TreeSetWithId<String> methodsUsed = methodMap.get(methodId);
            String lambdaId = mFieldsUsedByMethod.getId();

            if (outerId != null && fieldMap.containsKey(outerId)) {
                TreeSetWithId<String> original = fieldMap.get(outerId);
                original.addAll(fieldsUsed);
                mFieldsUsedByMethods.removeIf((m) -> m.getId().equals(lambdaId));
                mResponseSet.remove(className + "." + lambdaId);
                mClassMetrics.decWmc();
            }
            if (outerId != null && methodMap.containsKey(outerId)) {
                TreeSetWithId<String> original = methodMap.get(outerId);
                original.addAll(methodsUsed);
                mMethodsUsedByMethods.removeIf((m) -> m.getId().equals(lambdaId));
            }
        }
    }

    private static String extractOuterMethodIdFromLambda(String lambdaName, List<TreeSetWithId<String>> mMethodsUsedByMethods) {
        // lambda$run$0 → run
        int start = "lambda$".length();
        int end = lambdaName.lastIndexOf('$');
        if (end <= start) return null;
        String methodName = lambdaName.substring(start, end);  // e.g. "run"

        // 找出對應的 TreeSetWithId，其 id 是 methodName 開頭的
        for (TreeSetWithId<String> ts : mMethodsUsedByMethods) {
            if (ts.getId().startsWith(methodName)) {
                return ts.getId(); // e.g. "run()Ljava/util/List;"
            }
        }
        return null;
    }
}
