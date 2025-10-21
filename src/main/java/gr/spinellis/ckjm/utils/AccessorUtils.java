package gr.spinellis.ckjm.utils;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

public class AccessorUtils {

    public static boolean isPotentialGetterOrSetter(Method method) {
        String name = method.getName();
        Type[] args = method.getArgumentTypes();
        Type returnType = method.getReturnType();

        if ((name.startsWith("get") && name.length() > 3) || (name.startsWith("is") && name.length() > 2)) {
            return args.length == 0 && !returnType.equals(Type.VOID);
        }

        if (name.startsWith("set") && name.length() > 3) {
            return args.length == 1;
        }

        return false;
    }

    public static boolean isPureGetterOrSetter(MethodGen mg) {
        try {
            InstructionList il = mg.getInstructionList();
            if (il == null) return false;

            String methodName = mg.getName();
            if (methodName.startsWith("get") || methodName.startsWith("is")) {
                return isPureGetter(il);
            } else if (methodName.startsWith("set")) {
                return isPureSetter(il);
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isPureGetter(InstructionList il) {
        Instruction[] ins = il.getInstructions();

        boolean hasAload0 = false, hasGetfield = false, hasReturn = false;

        for (Instruction instr : ins) {
            short op = instr.getOpcode();
            if (op == org.apache.bcel.Const.ALOAD_0) hasAload0 = true;
            else if (op == org.apache.bcel.Const.GETFIELD) hasGetfield = true;
            else if (op >= org.apache.bcel.Const.IRETURN && op <= org.apache.bcel.Const.RETURN) hasReturn = true;
            else if (isMethodCall(op)) return false;
        }

        return hasAload0 && hasGetfield && hasReturn;
    }

    private static boolean isPureSetter(InstructionList il) {
        Instruction[] ins = il.getInstructions();

        boolean hasAload0 = false, hasLoadParam = false, hasPutfield = false, hasReturn = false;

        for (Instruction instr : ins) {
            short op = instr.getOpcode();
            if (op == org.apache.bcel.Const.ALOAD_0) hasAload0 = true;
            else if ((op >= org.apache.bcel.Const.ILOAD_1 && op <= org.apache.bcel.Const.ALOAD_3) ||
                    op == org.apache.bcel.Const.ILOAD || op == org.apache.bcel.Const.LLOAD ||
                    op == org.apache.bcel.Const.FLOAD || op == org.apache.bcel.Const.DLOAD ||
                    op == org.apache.bcel.Const.ALOAD) hasLoadParam = true;
            else if (op == org.apache.bcel.Const.PUTFIELD) hasPutfield = true;
            else if (op == org.apache.bcel.Const.RETURN) hasReturn = true;
            else if (isMethodCall(op)) return false;
        }

        return hasAload0 && hasLoadParam && hasPutfield && hasReturn;
    }

    private static boolean isMethodCall(short op) {
        return op == org.apache.bcel.Const.INVOKEVIRTUAL ||
                op == org.apache.bcel.Const.INVOKESPECIAL ||
                op == org.apache.bcel.Const.INVOKESTATIC ||
                op == org.apache.bcel.Const.INVOKEINTERFACE;
    }

}
