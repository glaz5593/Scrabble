package com.moshe.glaz.scrabble.managers;

public class ActionResult {
    public boolean success;
    public String error;
    public Object result;

    public static ActionResult toError(String error) {
        ActionResult res = new ActionResult();
        res.success = false;
        res.error = error;
        return res;
    }

    public static ActionResult toSuccess(Object result) {
        ActionResult res = new ActionResult();
        res.success = true;
        res.result = result;
        return res;
    }
}
