package gr.spinellis.ckjm.utils;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;


public class LambdaUtils {

    public static boolean isLambdaMethod(Method method) {
        String methodName = method.getName();
        if (isLambdaMethodName(methodName)) {
            return isLambdaModifiers(method);
        }
        return false;
    }

    public static boolean isLambdaMethodAdvanced(Method method, MethodGen mg) {
        if (!isLambdaMethod(method)) return false;
        try {
            return hasLambdaCharacteristics(mg);
        } catch (Exception e) {
            return isLambdaMethod(method);
        }
    }

    public static boolean isLambdaMethodName(String methodName) {
        if (methodName.startsWith("lambda$")) {
            int lastDollar = methodName.lastIndexOf('$');
            if (lastDollar > 7) {
                String suffix = methodName.substring(lastDollar + 1);
                try {
                    Integer.parseInt(suffix);
                    return true;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return false;
    }

    public static boolean isLambdaModifiers(Method method) {
        return method.isPrivate() && method.isSynthetic();
    }

    public static boolean hasLambdaCharacteristics(MethodGen mg) {
        InstructionList il = mg.getInstructionList();
        if (il == null) return false;
        Instruction[] instructions = il.getInstructions();
        if (instructions.length > 50) return false;

        int complexInstructionCount = 0;
        for (Instruction instr : instructions) {
            if (isComplexInstruction(instr.getOpcode())) {
                complexInstructionCount++;
            }
        }
        return complexInstructionCount <= 3;
    }

    private static boolean isComplexInstruction(short opcode) {
        return switch (opcode) {
            case org.apache.bcel.Const.NEW,
                 org.apache.bcel.Const.NEWARRAY,
                 org.apache.bcel.Const.ANEWARRAY,
                 org.apache.bcel.Const.MULTIANEWARRAY,
                 org.apache.bcel.Const.ATHROW,
                 org.apache.bcel.Const.CHECKCAST,
                 org.apache.bcel.Const.INSTANCEOF,
                 org.apache.bcel.Const.MONITORENTER,
                 org.apache.bcel.Const.MONITOREXIT -> true;
            default -> false;
        };
    }

    public static boolean isMethodReference(Method method) {
        return isLambdaMethodName(method.getName());
    }

    public static boolean shouldSkipLambdaMethod(Method method, MethodGen mg) {
        return isLambdaMethodAdvanced(method, mg);
    }
}
