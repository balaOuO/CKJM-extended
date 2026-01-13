package gr.spinellis.ckjm.utils;

public class MethodUtils {
    /**
     * Strips the return type from a method signature.
     * e.g. "method(I)V" -> "method(I)"
     *
     * @param signature The method signature including return type.
     * @return The signature without the return type, or the original if no ')' found.
     */
    public static String stripReturnType(String signature) {
        int rightParenIndex = signature.lastIndexOf(')');
        if (rightParenIndex != -1) {
            return signature.substring(0, rightParenIndex + 1);
        }
        return signature;
    }
}
