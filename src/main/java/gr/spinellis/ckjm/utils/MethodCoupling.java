/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gr.spinellis.ckjm.utils;


/**
 *
 * @author mjureczk
 */
public class MethodCoupling implements Comparable<MethodCoupling>{
    private String mClassA;
    private String mClassB;
    private String mMethodA;
    private String mMethodB;
    

    public MethodCoupling( String classA, String methodA, String classB, String methodB) {
        mClassA = classA;
        mMethodA = methodA;
        mClassB = classB;
        mMethodB = methodB;

        if( classA.equals(classB) && methodA.equals(methodB) ){
            LoggerHelper.printError( "Method "+classA+"."+methodA+" is coupled to itself!", new RuntimeException() );

        }
        else if( classA.equals(classB) ){
            LoggerHelper.printError( "Coupling within methods in the same class ("+classA+"): "+methodA+" is coupled to "+methodB+"!", new RuntimeException() );
        }

    }


    @Override
    public String toString(){
        return mClassA+"."+mMethodA+" is coupled to "+mClassB+"."+mMethodB;
    }

    // 如果是無向耦合關係（A-B 等同於 B-A），使用以下版本：
    public int compareTo(MethodCoupling mc) {
        // 標準化順序：確保較小的在前面
        String thisClass1 = mClassA.compareTo(mClassB) <= 0 ? mClassA : mClassB;
        String thisClass2 = mClassA.compareTo(mClassB) <= 0 ? mClassB : mClassA;
        String thisMethod1 = mClassA.compareTo(mClassB) <= 0 ? mMethodA : mMethodB;
        String thisMethod2 = mClassA.compareTo(mClassB) <= 0 ? mMethodB : mMethodA;

        String otherClass1 = mc.getClassA().compareTo(mc.getClassB()) <= 0 ? mc.getClassA() : mc.getClassB();
        String otherClass2 = mc.getClassA().compareTo(mc.getClassB()) <= 0 ? mc.getClassB() : mc.getClassA();
        String otherMethod1 = mc.getClassA().compareTo(mc.getClassB()) <= 0 ? mc.getMethodA() : mc.getMethodB();
        String otherMethod2 = mc.getClassA().compareTo(mc.getClassB()) <= 0 ? mc.getMethodB() : mc.getMethodA();

        // 比較標準化後的值
        int result = thisClass1.compareTo(otherClass1);
        if (result != 0) return result;

        result = thisClass2.compareTo(otherClass2);
        if (result != 0) return result;

        result = thisMethod1.compareTo(otherMethod1);
        if (result != 0) return result;

        return thisMethod2.compareTo(otherMethod2);
    }

    /**
     * @return the mClassA
     */
    public String getClassA() {
        return mClassA;
    }

    /**
     * @return the mClassB
     */
    public String getClassB() {
        return mClassB;
    }

    /**
     * @return the mMethodA
     */
    public String getMethodA() {
        return mMethodA;
    }

    /**
     * @return the mMethodB
     */
    public String getMethodB() {
        return mMethodB;
    }



}
