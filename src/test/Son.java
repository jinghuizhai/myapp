package test;

import java.util.ArrayList;

/**
 * Created by UI on 2015/12/26.
 */
public class Son extends Father {
    public Son() {
        super();
    }
    public String getParam(){
        return param;
    }
    public static void main(String args[]){
        ArrayList list = new ArrayList();
        System.out.print(list.isEmpty());
    }

}
