package cn.pioneeruniverse.common.sonar.bean;

import java.util.List;

/**
 * Created by klodye on 3/21/2018.
 */
public class Errors {

    List<Error> errors;

    public List<Error> getErrors() {
        return errors;
    }

    public class Error {

        String msg;

        public String getMsg() {
            return msg;
        }
    }
}
